package com.nschmidtg.comparemasters.application

import com.nschmidtg.comparemasters.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class ApplicationUser(private val user: User) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

    override fun getPassword(): String = ""

    override fun getUsername(): String = user.email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = user.validated

    fun getGecos(): String = user.gecos
}
