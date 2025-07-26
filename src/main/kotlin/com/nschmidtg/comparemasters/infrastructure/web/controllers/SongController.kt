package com.nschmidtg.comparemasters.infrastructure.web.controllers

import com.nschmidtg.comparemasters.domain.SongRepository
import com.nschmidtg.comparemasters.domain.Songs
import com.nschmidtg.comparemasters.infrastructure.web.api.SongsApi
import com.nschmidtg.comparemasters.infrastructure.web.model.CreateSongRequest
import com.nschmidtg.comparemasters.infrastructure.web.model.SongVM
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import java.util.UUID
import kotlin.jvm.optionals.getOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "BearerAuth")
class SongController(private val songRepository: SongRepository) : SongsApi {

    @Transactional
    override fun createSong(
        @Parameter(
            description = "",
            name = "CreateSongRequest",
            required = true
        )
        @Valid
        @RequestBody
        createSongRequest: @Valid CreateSongRequest?
    ): ResponseEntity<SongVM>? {
        val songs =
            songRepository.save(
                Songs(
                    title = createSongRequest!!.title,
                    artist = createSongRequest.artist,
                    album = createSongRequest.album,
                    year = createSongRequest.year
                )
            )
        return ResponseEntity.ok(
            SongVM().apply {
                id = songs.id.toString()
                title = songs.title
                artist = songs.artist
                album = songs.album
                year = songs.year
            }
        )
    }

    override fun getSongs(): ResponseEntity<List<SongVM>> {
        return ResponseEntity.ok(
            songRepository.findAll().map {
                SongVM().apply {
                    id = it.id.toString()
                    title = it.title
                    artist = it.artist
                    album = it.album
                    year = it.year
                }
            }
        )
    }

    override fun getSong(songId: UUID): ResponseEntity<SongVM> {
        return ResponseEntity.ok(
            songRepository.findById(songId).getOrNull()?.let {
                SongVM().apply {
                    id = it.id.toString()
                    title = it.title
                    artist = it.artist
                    album = it.album
                    year = it.year
                }
            }
        )
    }
}
