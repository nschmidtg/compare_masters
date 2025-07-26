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
    var token: String,
    var refreshToken: String,
    var expiresAt: Instant,
    var issuedAt: Instant,
    var revoked: Boolean = false
) {

    fun isIssuedDateValid(jwtExpirationMillis: Long): Boolean =
        issuedAt.plusMillis(jwtExpirationMillis).isAfter(Instant.now())
}
