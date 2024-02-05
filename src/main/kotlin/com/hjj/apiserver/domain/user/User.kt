package com.hjj.apiserver.domain.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class User(
    val userNo: Long = 0L,
    val nickName: String,
    val userEmail: String? = null,
    val userPw: String? = null,
    val picture: String? = null,
    val role: Role = Role.GUEST,
) : UserDetails {

    companion object {
        fun createGuestUser(): User{
            return User(nickName = "")
        }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return arrayListOf(SimpleGrantedAuthority(role.key))
    }

    override fun getPassword(): String? {
        return userPw
    }

    override fun getUsername(): String {
        return userNo.toString()
    }

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

}