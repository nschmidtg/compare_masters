package com.nschmidtg.comparemasters.application

import com.nschmidtg.comparemasters.domain.Token
import com.nschmidtg.comparemasters.domain.TokenRepository
import com.nschmidtg.comparemasters.domain.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val tokenRepository: TokenRepository
) {

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

    fun login(idToken: String): Token {
        return tokenRepository.findAll()[0]
    }
}

class UserNotFoundException(msg: String) : RuntimeException(msg)

class UserNotValidatedException(msg: String) : RuntimeException(msg)

class TokenExpiredOrRevokedException(msg: String) : RuntimeException(msg)

class TokenNotFoundForUserException(msg: String) : RuntimeException(msg)
