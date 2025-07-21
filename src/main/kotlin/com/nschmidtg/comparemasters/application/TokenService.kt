package com.nschmidtg.comparemasters.application

import com.nschmidtg.comparemasters.domain.Token
import com.nschmidtg.comparemasters.domain.TokenRepository
import com.nschmidtg.comparemasters.domain.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val tokenRepository: TokenRepository,
    @Value("\${jwt.secret}") private val jwtSecret: String,
    @Value("\${jwt.expiration}") private val jwtExpiration: Long
) {

    private val secretKey: SecretKey =
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun createToken(user: User): Token {
        val issuedAt = Instant.now()
        val expiresAt = issuedAt.plusMillis(jwtExpiration)

        val token =
            Jwts.builder()
                .subject(user.email)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact()

        val refreshToken =
            Jwts.builder()
                .subject(user.email)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusMillis(jwtExpiration * 10)))
                .signWith(secretKey)
                .compact()

        val tokenEntity =
            Token(
                user = user,
                token = token,
                refreshToken = refreshToken,
                expiresAt = expiresAt,
                issuedAt = issuedAt
            )

        return tokenRepository.save(tokenEntity)
    }

    fun refreshToken(token: Token): Token {
        val issuedAt = Instant.now()
        val expiresAt = issuedAt.plusMillis(jwtExpiration)

        token.token =
            Jwts.builder()
                .subject(token.user.email)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact()

        token.refreshToken =
            Jwts.builder()
                .subject(token.user.email)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusMillis(jwtExpiration * 10)))
                .signWith(secretKey)
                .compact()

        token.issuedAt = issuedAt
        token.expiresAt = expiresAt

        return tokenRepository.save(token)
    }

    fun parseToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val claims = parseToken(token)
        val username = claims.subject
        val expiration = claims.expiration
        return username == userDetails.username && expiration.after(Date())
    }
}
