package com.nschmidtg.comparemasters.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "songs")
class Songs(
    @Id @GeneratedValue(strategy = GenerationType.UUID) var id: UUID? = null,
    val title: String,
    val artist: String,
    val album: String,
    val year: Int
)
