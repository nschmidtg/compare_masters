package com.nschmidtg.comparemasters.infrastructure.web.controllers

import com.nschmidtg.comparemasters.application.AuthenticationService
import com.nschmidtg.comparemasters.infrastructure.web.api.AuthApi
import com.nschmidtg.comparemasters.infrastructure.web.model.AuthenticationRequest
import com.nschmidtg.comparemasters.infrastructure.web.model.AuthenticationResponse
import com.nschmidtg.comparemasters.infrastructure.web.model.RefreshRequest
import com.nschmidtg.comparemasters.infrastructure.web.model.RegistrationRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(private val authenticationService: AuthenticationService) :
    AuthApi {
    override fun login(
        registrationRequest: RegistrationRequest
    ): ResponseEntity<Void> {
        authenticationService.login(
            registrationRequest.idToken,
        )
        return ResponseEntity.ok().build()
    }

    override fun authenticate(
        authenticationRequest: AuthenticationRequest
    ): ResponseEntity<AuthenticationResponse> =
        authenticationService
            .authenticate(authenticationRequest.token)
            .getOrNull()
            ?.let {
                ResponseEntity.ok(
                    AuthenticationResponse().apply {
                        token = it.token
                        refreshToken = it.refreshToken
                    }
                )
            }
            ?: ResponseEntity.status(403).build()

    override fun refresh(
        refreshRequest: RefreshRequest
    ): ResponseEntity<AuthenticationResponse> =
        authenticationService
            .refresh(refreshRequest.refreshToken)
            .getOrNull()
            ?.let {
                ResponseEntity.ok(
                    AuthenticationResponse().apply {
                        token = it.token
                        refreshToken = it.refreshToken
                    }
                )
            }
            ?: ResponseEntity.status(403).build()
}
