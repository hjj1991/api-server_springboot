package com.hjj.apiserver.service

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

class MonoTest {
    @Test
    fun monoTest() {
        val mono = Mono1()

//        val mono1 = Mono.just(mono.mono1()).subscribeOn(Schedulers.parallel())
//        val mono2 = Mono.just(mono.mono1()).subscribeOn(Schedulers.parallel())

        val mono1 =
            Mono.just(1).map {
                println("안녕1")
                Thread.sleep(2000L)
            }.subscribeOn(Schedulers.parallel())

        val mono2 =
            Mono.just(1).map {
                println("안녕2")
                Thread.sleep(2000L)
            }.subscribeOn(Schedulers.parallel())

        Mono.zip(mono1, mono2)
            .subscribe()
    }
}

class Mono1 {
    fun mono1() {
        println("하이1")
        Thread.sleep(3000)
    }
}
