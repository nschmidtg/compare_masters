package com.nschmidtg.comparemasters.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : JpaRepository<Token, Long> {
    fun findByToken(token: String): Token?
    fun findByRefreshToken(refreshToken: String): Token?

    @Query(
        """
        select t from Token t inner join User u on t.user.id = u.id
        where u.id = :id and (t.revoked = false)
        """
    )
    fun findAllValidTokenByUser(id: Long): List<Token>
}
