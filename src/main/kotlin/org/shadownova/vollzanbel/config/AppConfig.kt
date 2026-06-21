package org.shadownova.vollzanbel.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class AppConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        // Use jacksonObjectMapper() so the Kotlin module is registered and
        // Kotlin data classes deserialize correctly.
        return jacksonObjectMapper()
    }

    @Bean
    fun restClient(): RestClient {
        return RestClient.create()
    }
}