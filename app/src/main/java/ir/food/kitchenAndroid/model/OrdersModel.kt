package ir.food.kitchenAndroid.model

import org.json.JSONArray

data class OrdersModel(
    val id: String,
    val products: JSONArray,
    val customerId: String,
    val customerFamily: String,
    val customerMobile: String,
    val createdAt: String,
    val address: String
)
