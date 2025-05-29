package com.rodi

import com.rodi.model.Category
import com.rodi.model.Expense
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    connectToDatabase()
    createTables()
    configureSerialization()
    configureRouting()
}

fun connectToDatabase() {
    Database.connect("jdbc:sqlite:expensesdata.db", driver = "org.sqlite.JDBC")
}

fun createTables() {
    transaction {
        SchemaUtils.create(Expense)
        SchemaUtils.create(Category)
    }
}