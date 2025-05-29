package com.rodi.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Category : IntIdTable() {
    val name = varchar("name", 255)
}