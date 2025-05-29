package com.rodi.data

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/category")
data class CategoryDto(
    val name: String
)