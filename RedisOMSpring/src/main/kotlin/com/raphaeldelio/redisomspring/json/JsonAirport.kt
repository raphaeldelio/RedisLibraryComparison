package com.raphaeldelio.redisomspring.json

import com.redis.om.spring.annotations.Document
import org.springframework.data.annotation.Id

@Document("AirportJson")
data class JsonAirport(
    @Id var id: String? = null,
    var ident: String,
    var type: String,
    var name: String,
    var latitudeDeg: Double?,
    var longitudeDeg: Double?,
    var elevationFt: Int?,
    var continent: String,
    var isoCountry: String,
    var isoRegion: String,
    var municipality: String,
    var scheduledService: String,
    var gpsCode: String,
    var iataCode: String,
    var localCode: String,
    var homeLink: String,
    var wikipediaLink: String,
    var keywords: String
)
