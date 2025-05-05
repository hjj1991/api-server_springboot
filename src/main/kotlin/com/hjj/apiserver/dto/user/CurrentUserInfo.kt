package com.hjj.apiserver.dto.user

import com.hjj.apiserver.domain.user.RoleType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serial

class CurrentUserInfo(
    val userId: String? = null,
    val nickName: String? = null,
    val userNo: Long,
    val roleType: RoleType,
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return arrayListOf(SimpleGrantedAuthority(this.roleType.code))
    }

    override fun getPassword(): String = ""

    override fun getUsername(): String = userNo.toString()

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    companion object {
        @Serial
        private const val serialVersionUID: Long = 341834540654704414L
    }
}
