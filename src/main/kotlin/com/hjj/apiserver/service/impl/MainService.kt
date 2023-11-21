package com.hjj.apiserver.service.impl

import com.hjj.apiserver.dto.main.response.MainFindResponse
import com.hjj.apiserver.repository.deposit.DepositRepository
import com.hjj.apiserver.repository.saving.SavingRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class MainService(
    private val depositRepository: DepositRepository,
    private val savingRepository: SavingRepository,
) {

    private val log = LoggerFactory.getLogger(MainService::class.java)
    fun findMain(): MainFindResponse {
        val deposit = Mono.fromCallable { depositRepository.findDepositByHome() }
            .doOnError{ error -> log.error("[Error] MainService::findDepositByHome, error={}", error.message)}
            .subscribeOn(Schedulers.parallel())
        val saving = Mono.fromCallable { savingRepository.findSavingByHome() }
            .doOnError{ error -> log.error("[Error] MainService::findSavingByHome, error={}", error.message)}
            .subscribeOn(Schedulers.parallel())
        return Mono.zip(deposit, saving)
            .map { MainFindResponse(it.t1, it.t2) }
            .subscribeOn(Schedulers.parallel())
//            .onErrorMap { Exception() }
            .blockOptional().orElse(MainFindResponse())
    }
}