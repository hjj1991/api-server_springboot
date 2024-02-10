package com.hjj.apiserver.domain.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Clock
import java.time.ZonedDateTime

class User(
    val userNo: Long = 0L,
    val nickName: String,
    val userEmail: String? = null,
    val userPw: String? = null,
    val picture: String? = null,
    val role: Role = Role.GUEST,
    val createdAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
    val modifiedAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (userNo != other.userNo) return false
        if (nickName != other.nickName) return false
        if (userEmail != other.userEmail) return false
        if (userPw != other.userPw) return false
        if (picture != other.picture) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userNo.hashCode()
        result = 31 * result + nickName.hashCode()
        result = 31 * result + (userEmail?.hashCode() ?: 0)
        result = 31 * result + (userPw?.hashCode() ?: 0)
        result = 31 * result + (picture?.hashCode() ?: 0)
        result = 31 * result + role.hashCode()
        return result
    }


}