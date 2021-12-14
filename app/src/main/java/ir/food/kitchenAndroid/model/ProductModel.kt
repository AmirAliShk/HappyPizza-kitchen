package ir.food.kitchenAndroid.model

data class ProductModel(
    val id: String,
    val name: String,
    val description: String,
    val supply: Int,
    val updatedAt: String,
    val typeName: String,
    val typeId: String
)
