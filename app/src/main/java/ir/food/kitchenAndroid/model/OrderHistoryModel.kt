package ir.food.kitchenAndroid.model

import org.json.JSONArray

data class OrderHistoryModel(
    val products: JSONArray,
    val id: String,
    val customerFamily: String,
    val address: String,
    val statusName: String,
    val statusCode: Int,
    val description: String,
    val date: String
)
