package org.blackerp.application.services.core

import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Propagation
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlinx.coroutines.*
import arrow.core.*
import java.util.UUID
