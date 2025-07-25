package com.nschmidtg.comparemasters.infrastructure.web.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.nschmidtg.comparemasters.domain.Token
import com.nschmidtg.comparemasters.domain.TokenRepository
import com.nschmidtg.comparemasters.domain.User
import com.nschmidtg.comparemasters.domain.UserRepository
import com.nschmidtg.comparemasters.infrastructure.web.viewmodels.AuthenticationRequest
import com.nschmidtg.comparemasters.infrastructure.web.viewmodels.AuthenticationResponse
import com.nschmidtg.comparemasters.infrastructure.web.viewmodels.RegistrationRequest
import java.time.Instant
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIT {

    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Autowired private lateinit var userRepository: UserRepository

    @Autowired private lateinit var tokenRepository: TokenRepository

    @Test
    fun `should register user and return 200`() {
        val registrationRequest =
            RegistrationRequest("test@test.com", "testuser")

        mockMvc
            .post("/api/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(registrationRequest)
            }
            .andExpect { status { isOk() } }
    }

    @Test
    fun `should authenticate user and return 200`() {
        val user =
            User(email = "test@test.com", gecos = "testuser", validated = true)
        userRepository.save(user)

        val authenticationRequest = AuthenticationRequest("test@test.com")

        mockMvc
            .post("/api/auth/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(authenticationRequest)
            }
            .andExpect { status { isOk() } }
    }

    @Test
    fun `should return 403 when user is not validated`() {
        val user =
            User(email = "test@test.com", gecos = "testuser", validated = false)
        userRepository.save(user)

        val authenticationRequest = AuthenticationRequest("test@test.com")

        mockMvc
            .post("/api/auth/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(authenticationRequest)
            }
            .andExpect { status { isForbidden() } }
    }

    @Test
    fun `should return 200 and new token when token is expired`() {
        val user =
            User(email = "test@test.com", gecos = "testuser", validated = true)
        userRepository.save(user)
        val oldToken = "token"
        tokenRepository.save(
            Token(
                user = user,
                token = "test",
                refreshToken = "test",
                expiresAt = Instant.now().minusSeconds(1000),
                issuedAt = Instant.now(),
                revoked = false
            )
        )
        val authenticationRequest = AuthenticationRequest("test@test.com")

        val result =
            mockMvc
                .post("/api/auth/authenticate") {
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        objectMapper.writeValueAsString(authenticationRequest)
                }
                .andExpect { status { isOk() } }
                .andReturn()

        val response =
            objectMapper.readValue(
                result.response.contentAsString,
                AuthenticationResponse::class.java
            )

        assert(response.token != oldToken)
    }

    @Test
    fun `should return 200 and same token when token is not expired`() {
        val user =
            User(email = "test@test.com", gecos = "testuser", validated = true)
        userRepository.save(user)
        val oldToken = "token"
        tokenRepository.save(
            Token(
                user = user,
                token = oldToken,
                refreshToken = "test",
                expiresAt = Instant.now().plusSeconds(300),
                issuedAt = Instant.now(),
                revoked = false
            )
        )
        val authenticationRequest = AuthenticationRequest("test@test.com")

        val result =
            mockMvc
                .post("/api/auth/authenticate") {
                    contentType = MediaType.APPLICATION_JSON
                    content =
                        objectMapper.writeValueAsString(authenticationRequest)
                }
                .andExpect { status { isOk() } }
                .andReturn()

        val response =
            objectMapper.readValue(
                result.response.contentAsString,
                AuthenticationResponse::class.java
            )

        assert(response.token == oldToken)
    }

    @Test
    fun `should return 403 when token is revoked`() {
        val user =
            User(email = "test@test.com", gecos = "testuser", validated = true)
        userRepository.save(user)
        tokenRepository.save(
            Token(
                user = user,
                token = "test",
                refreshToken = "test",
                expiresAt = Instant.now().plusSeconds(300),
                issuedAt = Instant.now(),
                revoked = true
            )
        )
        val authenticationRequest = AuthenticationRequest("test@test.com")

        mockMvc
            .post("/api/auth/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(authenticationRequest)
            }
            .andExpect { status { isForbidden() } }
    }
}
