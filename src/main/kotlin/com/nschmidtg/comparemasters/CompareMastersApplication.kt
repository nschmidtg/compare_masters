package com.nschmidtg.comparemasters

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class CompareMastersApplication

fun main(args: Array<String>) {
    runApplication<CompareMastersApplication>(*args)
}
