package models
import kotlinx.serialization.Serializable


@Serializable
data class Menu(val _id : String , val items : MutableList<Food>, val owner : String)
