package com.nschmidtg.comparemasters.infrastructure.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry
                    .addMapping("/**")
                    .allowedOrigins(
                        "http://localhost:8080"
                    ) // Replace with Swagger UI host
                    .allowedMethods("*")
                    .allowCredentials(true)
            }
        }
    }
}
