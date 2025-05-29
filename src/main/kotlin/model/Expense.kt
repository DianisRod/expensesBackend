package com.rodi.model

import org.jetbrains.exposed.sql.Table

object Expense : Table() {
    val id = integer("id").autoIncrement()
    val reason = varchar("reason", 255)
    val amount = double("amount")
    val date = long("date")
    val category = reference("category_id", Category)
    override val primaryKey = PrimaryKey(id)
}