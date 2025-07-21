package com.nschmidtg.comparemasters.application

import com.nschmidtg.comparemasters.domain.Token
import com.nschmidtg.comparemasters.domain.TokenRepository
import com.nschmidtg.comparemasters.domain.User
import com.nschmidtg.comparemasters.domain.UserRepository
import java.time.Instant
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val tokenRepository: TokenRepository
) {

    fun authenticate(email: String): Token {
        val user = userRepository.findByEmail(email)!!
        if (!user.validated) {
            throw RuntimeException("User not validated")
        }

        val validUserTokens = tokenRepository.findAllValidTokenByUser(user.id!!)
        if (validUserTokens.isNotEmpty()) {
            val stillValidTokens =
                validUserTokens.filter { it.expiresAt.isAfter(Instant.now()) }
            if (stillValidTokens.isNotEmpty()) {
                return stillValidTokens.first()
            }
        }

        return tokenService.createToken(user)
    }

    fun register(email: String, gecos: String): User {
        val user = User(email = email, gecos = gecos)
        return userRepository.save(user)
    }
}
