package com.raphaeldelio.jedis

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch
import redis.clients.jedis.Jedis
import kotlin.reflect.full.memberProperties


@Component
class TestRunner : CommandLineRunner {
    private val airport = Airport(
        id = "5910",
        ident = "SBGR",
        name = "Guarulhos - Governador André Franco Montoro International Airport",
        type = "airport",
        latitudeDeg = -23.431944,
        longitudeDeg = -46.467778,
        elevationFt = 2461,
        continent = "SA",
        isoCountry = "BR",
        isoRegion = "BR-SP",
        municipality = "São Paulo",
        scheduledService = "yes",
        gpsCode = "SBGR",
        iataCode = "GRU",
        localCode = "SP0002",
        homeLink = "http://www.aeroportoguarulhos.net/",
        wikipediaLink = "https://en.wikipedia.org/wiki/S%C3%A3o_Paulo-Guarulhos_International_Airport",
        keywords = "Cumbica"
    )

    override fun run(vararg args: String) {
        val jedis = Jedis()
        jedis.connection.setTimeoutInfinite()
        val spentTimeJson = mutableListOf<Long>()
        val spentTimeHash = mutableListOf<Long>()
        val spentTimeHashNonPipelined = mutableListOf<Long>()

        for (x in 0..NUMBER_OF_RUNS) {
            flushAll(jedis)
            spentTimeJson.add(testJson(jedis))

            flushAll(jedis)
            spentTimeHash.add(testHash(jedis))

            flushAll(jedis)
            spentTimeHashNonPipelined.add(testHashNonPipelined(jedis))
        }

        val avgTimeSpentJson = spentTimeJson.average()
        val avgTimeSpentHash = spentTimeHash.average()
        val avgTimeSpentHashNonPipelined = spentTimeHashNonPipelined.average()

        val avgPerInsertJson = avgTimeSpentJson / NUMBER_OF_KEYS
        val avgPerInsertHash = avgTimeSpentHash / NUMBER_OF_KEYS
        val avgPerInsertHashNonPipelined = avgTimeSpentHashNonPipelined / NUMBER_OF_KEYS

        logger.info("Hash Non Pipelined: It took an avg of $avgTimeSpentHashNonPipelined ms to insert $NUMBER_OF_KEYS in the database")
        logger.info("Hash Non Pipelined: It took an avg of $avgPerInsertHashNonPipelined ms to insert each key in the database")

        logger.info("Hash: It took an avg of $avgTimeSpentHash ms to insert $NUMBER_OF_KEYS in the database")
        logger.info("Hash: It took an avg of $avgPerInsertHash ms to insert each key in the database")

        logger.info("Json: It took an avg of $avgTimeSpentJson ms to insert $NUMBER_OF_KEYS in the database")
        logger.info("Json : It took an avg of $avgPerInsertJson ms to insert each key in the database")
    }

    private fun testJson(jedis: Jedis): Long {
        logger.info("JSON:Inserting airports")

        val stopWatch = StopWatch()
        stopWatch.start()
        val pipelined = jedis.pipelined()
        for (x in 1..NUMBER_OF_KEYS) {
            pipelined.jsonSetLegacy("airport:$x", airport)
        }
        pipelined.sync()
        stopWatch.stop()

        logger.info("JSON: Insert Total time: ${stopWatch.totalTimeMillis} ms")
        return stopWatch.totalTimeMillis
    }

    private fun testHashNonPipelined(jedis: Jedis): Long {
        logger.info("HashMap: Inserting airports")
        val airportMap = airport.asMap()

        val stopWatch = StopWatch()
        stopWatch.start()
        for (x in 1..NUMBER_OF_KEYS) {
            jedis.hmset("airport:$x", airportMap)
        }
        stopWatch.stop()

        logger.info("HashMap: Insert Total time: ${stopWatch.totalTimeMillis} ms")
        return stopWatch.totalTimeMillis
    }

    private fun testHash(jedis: Jedis): Long {
        logger.info("HashMap: Inserting airports")
        val airportMap = airport.asMap()

        val stopWatch = StopWatch()
        stopWatch.start()
        val pipelined = jedis.pipelined()
        for (x in 1..NUMBER_OF_KEYS) {
            pipelined.hmset("airport:$x", airportMap)
        }
        pipelined.sync()
        stopWatch.stop()

        logger.info("HashMap: Insert Total time: ${stopWatch.totalTimeMillis} ms")
        return stopWatch.totalTimeMillis
    }

    private fun flushAll(jedis: Jedis) {
        logger.info("Flushing ${jedis.dbSize()} keys.")
        jedis.flushAll()
        logger.info("${jedis.dbSize()} keys in the db")
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
        private const val NUMBER_OF_KEYS = 1000
        private const val NUMBER_OF_RUNS = 10
    }
}

inline fun <reified T : Any> T.asMap() : Map<String, String> {
    val props = T::class.memberProperties.associateBy { it.name }
    return props.keys.associateWith { props[it]?.get(this).toString() }
}