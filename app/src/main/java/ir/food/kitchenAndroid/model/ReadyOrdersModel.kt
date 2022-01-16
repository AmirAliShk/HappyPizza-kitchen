package ir.food.kitchenAndroid.model

import org.json.JSONArray

data class ReadyOrdersModel(
    val products: JSONArray,
    val id: String,
    val customerMobile: String,
    val customerFamily: String,
    val address: String,
    val statusName: String,
    val statusCode: Int,
    val createdAt: String,
    val description: String,
    val systemDescription: String,
    val isPacked: Boolean,
    val totalPrice: String
)
