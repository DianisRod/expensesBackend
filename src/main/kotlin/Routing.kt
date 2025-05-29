package com.rodi

import com.rodi.data.ExpenseDto
import com.rodi.model.Expense
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.text.category
import kotlin.text.get
import kotlin.text.insert
import kotlin.text.set
import org.jetbrains.exposed.sql.statements.InsertStatement
import kotlin.collections.get
import kotlin.collections.set
import kotlin.text.category
import kotlin.text.get
import kotlin.text.insert
import kotlin.text.set
import kotlin.toString

fun Application.configureRouting() {
    install(Resources)
    routing {
        get("/") {
            call.respondText("Das ist das Backend von meiner Ausgaben-App!")
        }
        get("/expenses") {
            call.respond(getExpenses())
            //call.respondText("Das ist das Backend von meiner Ausgaben-App!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")
        get<ExpenseDto> { expense ->
            call.respond("List of expenses sorted starting from ${expense.reason}")
        }
        post("/expenses") {
            try {
                //val expense = call.receive<ExpenseDto>()
                val rawRequest = call.receiveText()
                val expense = Json.decodeFromString<ExpenseDto>(rawRequest)
                val amount =  if (expense.amount.isEmpty())  0.0 else expense.amount.toDouble()
                val categoryId : Int = expense.category.toInt()

                // Insert into database
                val insertedExpense = transaction {
                    val result = Expense.insert { stmt: InsertStatement<Number> ->
                        stmt[Expense.amount] = amount
                        stmt[Expense.reason] = expense.reason
                        stmt[Expense.date] = expense.date.toLong()
                        stmt[Expense.category] = org.jetbrains.exposed.dao.id.EntityID(categoryId, com.rodi.model.Category)
                    }
                    result.resultedValues?.firstOrNull()?.let { row ->
                        ExpenseDto(
                            amount = row[Expense.amount].toString(),
                            reason = row[Expense.reason],
                            category = row[Expense.category].value.toString(),
                            date = row[Expense.date].toString()
                        )
                    }
                }

//                val insertedExpense = ExpenseDto(
//                    amount = amount.toString(),
//                    reason = expense.reason,
//                    category = categoryId.toString(),
//                    date = expense.date
//                )
                if (insertedExpense != null) {
                    call.respond(HttpStatusCode.Created, insertedExpense)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to insert expense")
                }
                //call.respond(HttpStatusCode.Created, insertedExpense)
            } catch (ex : Exception){
                call.respond(HttpStatusCode.BadRequest,"Not a valid expense!: "+ex.message.toString())
            }
        }
    }
}

fun getExpenses() : List<ExpenseDto> {
    val list = ArrayList<ExpenseDto>()
    transaction {
        Expense.selectAll().forEach { row: ResultRow ->
            val expenseDto = ExpenseDto(
                amount = row[Expense.amount].toString(),
                reason = row[Expense.reason],
                category = row[Expense.category].value.toString(),
                date = row[Expense.date].toString()
            )
            list.add(expenseDto)
        }
    }
    return list
}


//fun insertExpense(amount: Double, reason: String, date: Long, categoryId: Int) : ResultRow? {
//    return transaction {
//        Expense.insert { stmt ->
//            stmt[Expense.amount] = amount
//            stmt[Expense.reason] = reason
//            stmt[Expense.date] = date
//            stmt[Expense.category] = EntityID(categoryId, Category)
//        }. resultedValues?.firstOrNull()
//    }
//}
//
//fun getExpenseById(id: Int): Map<String, Any>? {
//    return transaction {
//        Expense.selectAll().where { Expense.id eq id }.map { row: ResultRow ->
//            mapOf(
//                "id" to row[Expense.id],
//                "amount" to row[Expense.amount],
//                "reason" to row[Expense.reason],
//                "date" to row[Expense.date],
//                "category" to row[Expense.category]
//            )
//        }.singleOrNull()
//    }
//}