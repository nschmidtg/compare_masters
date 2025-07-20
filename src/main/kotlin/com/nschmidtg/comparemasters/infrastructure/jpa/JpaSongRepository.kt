package com.nschmidtg.comparemasters.infrastructure.jpa

import com.nschmidtg.comparemasters.domain.SongRepository
import com.nschmidtg.comparemasters.domain.Songs
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface JpaSongRepository : SongRepository, JpaRepository<Songs, UUID>
