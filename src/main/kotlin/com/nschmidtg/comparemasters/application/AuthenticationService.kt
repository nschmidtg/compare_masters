package com.nschmidtg.comparemasters.application

import com.nschmidtg.comparemasters.domain.Token
import com.nschmidtg.comparemasters.domain.User
import com.nschmidtg.comparemasters.domain.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) {

    fun authenticate(email: String): Token {
        val user = userRepository.findByEmail(email)!!
        if (!user.validated) {
            throw RuntimeException("User not validated")
        }
        return tokenService.createToken(user)
    }

    fun register(email: String, gecos: String): User {
        val user = User(email = email, gecos = gecos)
        return userRepository.save(user)
    }
}
