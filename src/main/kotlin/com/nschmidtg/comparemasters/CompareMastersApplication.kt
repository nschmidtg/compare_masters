package com.nschmidtg.comparemasters

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SecurityScheme(
    name = "BearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class CompareMastersApplication

fun main(args: Array<String>) {
    runApplication<CompareMastersApplication>(*args)
}
