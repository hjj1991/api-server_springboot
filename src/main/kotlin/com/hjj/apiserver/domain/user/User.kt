package com.hjj.apiserver.domain.user

import com.hjj.apiserver.domain.BaseTimeEntity
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.purchase.Purchase
import org.hibernate.annotations.DynamicUpdate
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@DynamicUpdate
@Table(
    name = "tb_user",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["providerId", "provider"])
    ]
)
class User(
    userNo: Long? = null,
    userId: String? = null,
    nickName: String,
    userEmail: String? = null,
    userPw: String? = null,
    picture: String? = null,
    providerId: String? = null,
    provider: Provider? = null,
    providerConnectDate: LocalDateTime? = null,
    purchaseList: MutableList<Purchase> = mutableListOf(),
    userLogList: MutableList<UserLog> = mutableListOf(),
    accountBookUserList: MutableList<AccountBookUser> = mutableListOf(),
    cards: MutableList<Card> = mutableListOf(),
    role: Role = Role.USER,
    refreshToken: String? = null,


): UserDetails, BaseTimeEntity() {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userNo: Long? = null

    @Column(length = 100, unique = true)
    val userId: String? = userId

    @Column(length = 20, unique = true)
    var nickName: String = nickName

    @Column(length = 200)
    var userEmail: String? = userEmail

    @Column(length = 300)
    var userPw: String? = userPw

    @Column
    var picture: String? = picture

    @Column(length = 40)
    var providerId: String? = providerId

    @Column
    @Enumerated(EnumType.STRING)
    var provider: Provider? = provider

    @Column(columnDefinition =  "datetime default null")
    var providerConnectDate: LocalDateTime? = providerConnectDate

    @Column
    var refreshToken: String? = refreshToken

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", fetch = FetchType.LAZY)
    val purchaseList: MutableList<Purchase> = purchaseList

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @OrderBy("loginDateTime desc")
    val userLogList: MutableList<UserLog> = userLogList

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", fetch = FetchType.LAZY)
    val accountBookUserList: MutableList<AccountBookUser> = accountBookUserList

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", fetch = FetchType.LAZY)
    val cards: MutableList<Card> = cards

    @Column(columnDefinition = "char(1) default 'N'", nullable = false, insertable = false)
    var deleteYn:Char = 'N'

    @Column(nullable = false, columnDefinition = "varchar(20) default 'USER'")
    @Enumerated(EnumType.STRING)
    var role: Role = role


    fun updateUserLogin(refreshToken: String): User{
        this.refreshToken = refreshToken
        return this
    }

    fun updateUser(nickName: String? = null,
                   userEmail: String? = null,
                   picture: String? = null,
                   provider: Provider? = null,
                   providerId: String? = null,
                   providerConnectDate: LocalDateTime? = null
    ): User{
        nickName?.also { this.nickName = it }
        userEmail?.also { this.userEmail = it }
        picture?.also { this.picture = it }
        provider?.also { this.provider = it }
        providerId?.also { this.providerId = it }
        providerConnectDate?.also { this.providerConnectDate = it }

        return this
    }

    fun isSocialUser(): Boolean{
        return this.provider != null
    }



    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return arrayListOf(SimpleGrantedAuthority(this.role.key))
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