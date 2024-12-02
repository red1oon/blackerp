package org.blackerp.application.services

import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.stereotype.Component
import org.springframework.http.HttpStatus
import org.blackerp.application.services.SecurityService
import reactor.core.publisher.Mono
import org.slf4j.LoggerFactory

@Component
class SecurityFilter(
    private val securityService: SecurityService
) : WebFilter {
    private val logger = LoggerFactory.getLogger(SecurityFilter::class.java)
    private val publicPaths = setOf("/api/auth/login", "/api/auth/refresh")

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.value()
        
        if (publicPaths.any { path.startsWith(it) }) {
            return chain.filter(exchange)
        }

        val token = exchange.request.headers.getFirst("Authorization")?.removePrefix("Bearer ")
        
        if (token == null) {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            return exchange.response.setComplete()
        }

        return Mono.fromCallable {
            securityService.validateToken(token)
        }.flatMap { result ->
            result.fold(
                { error ->
                    logger.error("Authentication failed: $error")
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    exchange.response.setComplete()
                },
                { context ->
                    exchange.attributes["securityContext"] = context
                    chain.filter(exchange)
                }
            )
        }
    }
}