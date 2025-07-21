package com.nschmidtg.comparemasters.infrastructure.web.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.nschmidtg.comparemasters.domain.User
import com.nschmidtg.comparemasters.domain.UserRepository
import com.nschmidtg.comparemasters.infrastructure.web.viewmodels.AuthenticationRequest
import com.nschmidtg.comparemasters.infrastructure.web.viewmodels.RegistrationRequest
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
}
