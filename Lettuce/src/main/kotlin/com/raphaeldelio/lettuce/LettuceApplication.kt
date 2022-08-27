package com.raphaeldelio.lettuce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LettuceApplication

fun main(args: Array<String>) {
    runApplication<LettuceApplication>(*args)
}
