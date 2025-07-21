package com.nschmidtg.comparemasters.infrastructure.web.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.nschmidtg.comparemasters.application.AuthenticationService
import com.nschmidtg.comparemasters.domain.User
import com.nschmidtg.comparemasters.infrastructure.web.viewmodels.RegistrationRequest
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIT {

    @Autowired private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var authenticationService: AuthenticationService

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should register user and return 200`() {
        val registrationRequest =
            RegistrationRequest("test@test.com", "testuser")
        val user = User(1, "test@test.com", "testuser", true)

        whenever(authenticationService.register(any(), any())).thenReturn(user)

        mockMvc
            .post("/api/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(registrationRequest)
            }
            .andExpect { status { isOk() } }
    }
}
