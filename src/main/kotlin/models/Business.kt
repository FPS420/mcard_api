package models

import kotlinx.serialization.Serializable

@Serializable
data class Business(
    val _id: String,
    val name: String,
    val entrepreneur_id: String,
    val menu_ids: MutableList<String>,
    val city: String,
    val zip: String,
    val street: String
)