package com.raphaeldelio.lettuce

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch
import java.time.Duration
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
        val client = RedisClient.create("redis://localhost:6379/0")
        val conn = client.connect()
        conn.timeout = Duration.ZERO

        val spentTimeHash = mutableListOf<Long>()
        val spentTimeHashNonPipelined = mutableListOf<Long>()

        for (x in 0..NUMBER_OF_RUNS) {
            flushAll(conn)
            spentTimeHash.add(testHash(conn))

            flushAll(conn)
            spentTimeHashNonPipelined.add(testHashNonPipelined(conn))
        }

        val avgTimeSpentHash = spentTimeHash.average()
        val avgTimeSpentHashNonPipelined = spentTimeHashNonPipelined.average()

        val avgPerInsertHash = avgTimeSpentHash / NUMBER_OF_KEYS
        val avgPerInsertHashNonPipelined = avgTimeSpentHashNonPipelined / NUMBER_OF_KEYS

        logger.info("Hash Non Pipelined: It took an avg of $avgTimeSpentHashNonPipelined ms to insert $NUMBER_OF_KEYS in the database")
        logger.info("Hash Non Pipelined: It took an avg of $avgPerInsertHashNonPipelined ms to insert each key in the database")

        logger.info("Hash: It took an avg of $avgTimeSpentHash ms to insert $NUMBER_OF_KEYS in the database")
        logger.info("Hash: It took an avg of $avgPerInsertHash ms to insert each key in the database")
    }

    private fun testHashNonPipelined(connection: StatefulRedisConnection<String, String>): Long {
        logger.info("HashMap: Inserting airports")
        val airportMap = airport.asMap()

        val stopWatch = StopWatch()
        stopWatch.start()

        connection.setAutoFlushCommands(true)
        val commands = connection.sync()
        for (x in 1..NUMBER_OF_KEYS) {
            commands.hmset("airport:$x", airportMap)
        }
        connection.flushCommands()
        stopWatch.stop()

        logger.info("HashMap Non Pipelined: Insert Total time: ${stopWatch.totalTimeMillis} ms")
        return stopWatch.totalTimeMillis
    }

    private fun testHash(connection: StatefulRedisConnection<String, String>): Long {
        logger.info("HashMap: Inserting airports")
        val airportMap = airport.asMap()

        val stopWatch = StopWatch()
        stopWatch.start()

        connection.setAutoFlushCommands(false)
        val commands = connection.async()
        for (x in 1..NUMBER_OF_KEYS) {
            commands.hmset("airport:$x", airportMap)
        }
        connection.flushCommands()

        stopWatch.stop()

        logger.info("HashMap: Insert Total time: ${stopWatch.totalTimeMillis} ms")
        return stopWatch.totalTimeMillis
    }

    private fun flushAll(connection: StatefulRedisConnection<String, String>) {
        connection.setAutoFlushCommands(true)
        val commands = connection.sync()
        logger.info("Flushing ${commands.dbsize()} keys.")
        commands.flushall()
        logger.info("${commands.dbsize()} keys in the db")
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