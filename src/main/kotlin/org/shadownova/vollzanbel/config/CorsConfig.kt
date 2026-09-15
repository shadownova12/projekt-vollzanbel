package org.shadownova.vollzanbel.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Allows the local Kotlin web development server to call the API.
 *
 * The client sends X-User-Id, so browsers issue an OPTIONS preflight before
 * the actual request. Without this mapping, the browser blocks the request
 * even though the same endpoint works from curl.
 */
@Configuration(proxyBeanMethods = false)
class CorsConfig {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer = object : WebMvcConfigurer {
        override fun addCorsMappings(registry: CorsRegistry) {
            registry.addMapping("/**")
                .allowedOriginPatterns(
                    "http://localhost:[*]",
                    "http://127.0.0.1:[*]",
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600)
        }
    }
}
