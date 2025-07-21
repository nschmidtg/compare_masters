package com.nschmidtg.comparemasters.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "tokens")
data class Token(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    @ManyToOne @JoinColumn(name = "user_id", nullable = false) val user: User,
    val token: String,
    val refreshToken: String,
    val expiresAt: Instant,
    val issuedAt: Instant,
    val refreshed: Boolean = false,
    var revoked: Boolean = false
)
