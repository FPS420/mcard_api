package models
import kotlinx.serialization.Serializable


@Serializable
data class Food(val name: String, val price : Double, val description: String, val tag: Tag)
