package ir.food.kitchenAndroid.model

import org.json.JSONArray

data class OrdersModel(
    val id: String,
    val products: JSONArray,
    val createdAt: String,
    val customerId: String,
    val customerFamily: String,
    val customerMobile: String
)
