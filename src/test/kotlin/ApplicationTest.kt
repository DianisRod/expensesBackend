package com.rodi

import com.rodi.data.ExpenseDto
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testGetExpenses() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val response = client.get("/expenses")
        assertEquals(HttpStatusCode.OK, response.status)
        val body: List<ExpenseDto> = response.body<List<ExpenseDto>>()
        assertTrue { body.isNotEmpty() }
        // TODO Inhalte der Elementen prüfen
    }

    @Test
    fun testExpectedFail() = testApplication {
        application {
            module()
        }
        client.get("/noEndpointHere").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    // TODO Mehr Tests schreiben, z.B. für POST, PUT, DELETE
}
