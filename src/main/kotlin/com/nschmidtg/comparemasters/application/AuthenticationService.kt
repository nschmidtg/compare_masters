package com.nschmidtg.comparemasters.application

import com.nschmidtg.comparemasters.domain.Token
import com.nschmidtg.comparemasters.domain.TokenRepository
import com.nschmidtg.comparemasters.domain.User
import com.nschmidtg.comparemasters.domain.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val tokenRepository: TokenRepository
) {

    fun authenticate(email: String): Result<Token> = runCatching {
        userRepository.findByEmail(email)?.let { user ->
            if (!user.validated)
                throw UserNotValidatedException("User not validated")
            tokenRepository.findByUserId(user.id)?.let {
                it.takeIf(tokenService::isValid)
                    ?: throw TokenExpiredOrRevokedException(
                        "User access is revoked or token is expired"
                    )
            }
                ?: throw TokenNotFoundForUserException(
                    "Token not found for user"
                )
        }
            ?: throw UserNotFoundException("User not found")
    }

    fun refresh(refreshToken: String): Result<Token> = runCatching {
        tokenRepository
            .findByRefreshToken(refreshToken)
            ?.takeIf { !it.revoked }
            ?.let { validToken ->
                if (!validToken.user.validated)
                    throw UserNotValidatedException("User not validated")
                tokenService.refreshToken(validToken)
            }
            ?: throw TokenExpiredOrRevokedException(
                "User access is revoked or token is expired"
            )
    }

    fun register(email: String, gecos: String): User {
        val user = User(email = email, gecos = gecos)
        return userRepository.save(user)
    }
}

class UserNotFoundException(msg: String) : RuntimeException(msg)

class UserNotValidatedException(msg: String) : RuntimeException(msg)

class TokenExpiredOrRevokedException(msg: String) : RuntimeException(msg)

class TokenNotFoundForUserException(msg: String) : RuntimeException(msg)
