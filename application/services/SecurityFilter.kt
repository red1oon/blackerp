package org.blackerp.application.services

import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.stereotype.Component
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import kotlinx.coroutines.runBlocking
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

       val authHeader = exchange.request.headers.getFirst("Authorization")
       val authToken = authHeader?.removePrefix("Bearer ")
       
       if (authToken == null) {
           exchange.response.statusCode = HttpStatus.UNAUTHORIZED
           return exchange.response.setComplete()
       }

       return Mono.defer {
           runBlocking {
               securityService.validateToken(authToken)
           }.fold(
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
           Mono.empty()
       }
   }
}
