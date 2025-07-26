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

    fun authenticate(token: String): Result<Token> = runCatching {
        tokenRepository.findByToken(token)?.let {
            if (!it.user.validated)
                throw UserNotValidatedException("User not validated")
            it.takeIf(tokenService::isValid)
                ?: throw TokenExpiredOrRevokedException(
                    "User access is revoked or token is expired"
                )
        }
            ?: throw TokenNotFoundForUserException("Token not found for user")
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

    fun login(idToken: String): Token {
        TODO()
    }
}

class UserNotFoundException(msg: String) : RuntimeException(msg)

class UserNotValidatedException(msg: String) : RuntimeException(msg)

class TokenExpiredOrRevokedException(msg: String) : RuntimeException(msg)

class TokenNotFoundForUserException(msg: String) : RuntimeException(msg)
