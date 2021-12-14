package com.example.data.packet

import org.joda.time.LocalDate

data class Packet(
    val id: Int?,
    val from: LocalDate,
    val to: LocalDate,
    val valid: Boolean
)