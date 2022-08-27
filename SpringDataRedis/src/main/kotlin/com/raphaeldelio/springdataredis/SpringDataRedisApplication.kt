package com.raphaeldelio.springdataredis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringDataRedisApplication

fun main(args: Array<String>) {
    runApplication<SpringDataRedisApplication>(*args)
}
