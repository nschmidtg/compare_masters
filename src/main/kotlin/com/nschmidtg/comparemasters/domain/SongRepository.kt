package com.nschmidtg.comparemasters.domain

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface SongRepository : JpaRepository<Songs, UUID>
