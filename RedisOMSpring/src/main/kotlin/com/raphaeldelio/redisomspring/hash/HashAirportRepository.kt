package com.raphaeldelio.redisomspring.hash

import com.redis.om.spring.repository.RedisDocumentRepository

interface HashAirportRepository : RedisDocumentRepository<HashAirport, String>
