package com.raphaeldelio.jedis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JedisApplication

fun main(args: Array<String>) {
    runApplication<JedisApplication>(*args)
}
