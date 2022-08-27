package com.raphaeldelio.springdataredis

import org.springframework.data.repository.CrudRepository

interface AirportRepository : CrudRepository<Airport, String>
