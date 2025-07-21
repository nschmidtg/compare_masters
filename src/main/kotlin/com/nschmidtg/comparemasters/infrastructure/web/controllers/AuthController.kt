package com.nschmidtg.comparemasters.infrastructure.web.controllers

import com.nschmidtg.comparemasters.application.AuthenticationService
import com.nschmidtg.comparemasters.infrastructure.web.viewmodels.AuthenticationRequest
import com.nschmidtg.comparemasters.infrastructure.web.viewmodels.AuthenticationResponse
import com.nschmidtg.comparemasters.infrastructure.web.viewmodels.RegistrationRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authenticationService: AuthenticationService) {

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegistrationRequest
    ): ResponseEntity<Void> {
        authenticationService.register(request.email, request.gecos)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/authenticate")
    fun authenticate(
        @RequestBody request: AuthenticationRequest
    ): ResponseEntity<AuthenticationResponse>? =
        authenticationService.authenticate(request.email).getOrNull()?.let {
            ResponseEntity.ok(AuthenticationResponse(it.token))
        }
            ?: ResponseEntity.status(403).build()
}
