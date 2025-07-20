package com.nschmidtg.comparemasters.application

import com.nschmidtg.comparemasters.domain.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JpaUserDetailsService(private val userRepository: UserRepository) :
    UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user =
            userRepository.findByEmail(username)
                ?: throw UsernameNotFoundException("User not found")
        return ApplicationUser(user)
    }
}
