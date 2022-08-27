package com.raphaeldelio.redisomspring

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories
import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableRedisDocumentRepositories("com.raphaeldelio.redisomspring.json")
@EnableRedisEnhancedRepositories("com.raphaeldelio.redisomspring.hash")
@SpringBootApplication
class RedisOmSpringApplication

fun main(args: Array<String>) {
    runApplication<RedisOmSpringApplication>(*args)
}
