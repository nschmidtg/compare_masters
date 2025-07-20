package com.nschmidtg.comparemasters.infrastructure.web.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.nschmidtg.comparemasters.domain.SongRepository
import openapi.generated.model.CreateSongRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class SongControllerIT {

    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var songRepository: SongRepository

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should create a song`() {
        val createSongRequest =
            CreateSongRequest().apply {
                title = "Stairway to Heaven"
                artist = "Led Zeppelin"
                album = "Led Zeppelin IV"
                year = 1971
            }

        mockMvc
            .post("/songs") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createSongRequest)
            }
            .andExpect {
                status { isOk() }
                jsonPath("$.title") { value(createSongRequest.title) }
                jsonPath("$.artist") { value(createSongRequest.artist) }
                jsonPath("$.album") { value(createSongRequest.album) }
                jsonPath("$.year") { value(createSongRequest.year) }
            }
    }
}
