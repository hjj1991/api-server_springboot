package com.hjj.apiserver.adapter.out.persistence.user

import org.springframework.data.jpa.repository.JpaRepository

interface CredentialRepository: JpaRepository<CredentialEntity, Long>, CredentialRepositoryCustom {
}