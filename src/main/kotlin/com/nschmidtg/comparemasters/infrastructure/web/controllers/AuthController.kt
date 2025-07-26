package com.nschmidtg.comparemasters.infrastructure.web.controllers

import com.nschmidtg.comparemasters.application.AuthenticationService
import com.nschmidtg.comparemasters.domain.Token
import com.nschmidtg.comparemasters.infrastructure.web.api.AuthApi
import com.nschmidtg.comparemasters.infrastructure.web.model.AuthenticationResponse
import com.nschmidtg.comparemasters.infrastructure.web.model.RefreshRequest
import com.nschmidtg.comparemasters.infrastructure.web.model.RegistrationRequest
import jakarta.servlet.http.Cookie
import java.time.Instant
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class AuthController(private val authenticationService: AuthenticationService) :
    AuthApi {
    override fun login(
        registrationRequest: RegistrationRequest
    ): ResponseEntity<AuthenticationResponse>? =
        authenticationService
            .login(
                registrationRequest.idToken,
            )
            .let {
                ResponseEntity.ok()
                    .headers(buildHttpHeaders(it))
                    .body(AuthenticationResponse(it.token, it.refreshToken))
            }

    override fun refresh(
        refreshRequest: RefreshRequest
    ): ResponseEntity<AuthenticationResponse> =
        authenticationService
            .refresh(refreshRequest.refreshToken)
            .getOrNull()
            ?.let {
                ResponseEntity.ok()
                    .headers(buildHttpHeaders(it))
                    .body(AuthenticationResponse(it.token, it.refreshToken))
            }
            ?: ResponseEntity.status(403).build()

    private fun buildHttpHeaders(it: Token): HttpHeaders {
        val refreshTokenCookie =
            Cookie("refreshToken", it.refreshToken).apply {
                path = "api/v1/auth/refresh"
                isHttpOnly = true
                secure = true
                maxAge =
                    ((it.expiresAt.epochSecond - Instant.now().epochSecond)
                            .coerceAtLeast(0))
                        .toInt()
            }

        val header = HttpHeaders()
        header.add(
            "Set-Cookie",
            "${refreshTokenCookie.name}=${refreshTokenCookie.value}; Path=${refreshTokenCookie.path}; HttpOnly; Max-Age=${refreshTokenCookie.maxAge}; Secure;"
        )
        return header
    }
}
