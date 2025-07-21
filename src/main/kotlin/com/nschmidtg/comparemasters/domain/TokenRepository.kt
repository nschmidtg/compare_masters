package com.nschmidtg.comparemasters.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : JpaRepository<Token, Long> {
    fun findByToken(token: String): Token?
    fun findByRefreshToken(refreshToken: String): Token?
    fun findByUserId(id: Long): Token?
}
