package com.rodi.data

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/expense")
data class ExpenseDto(
    val amount: String,
    val reason: String,
    val category: String,
    val date: String,
)