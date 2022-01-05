package ir.food.kitchenAndroid.model

data class CheckoutDeliModel(
    val deliId: String,
    val deliName: String,
    val totalPrice: String,
    val totalOnline: String,
    val remainder: String
)
