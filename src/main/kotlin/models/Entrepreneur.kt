package models

import kotlinx.serialization.Serializable

@Serializable
data class Entrepreneur(
    val _id: String,
    val firstname: String,
    val lastname: String,
    val city: String,
    val zip: String,
    val street: String
)

