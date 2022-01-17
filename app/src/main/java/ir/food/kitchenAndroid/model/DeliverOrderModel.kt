package ir.food.kitchenAndroid.model

data class DeliverOrderModel(
    val statusName: String,
    val statusCode: Int,
    val acceptedTime: String,
    val customerFamily: String,
    val finishedTime: String = "",
    val address: String
)
