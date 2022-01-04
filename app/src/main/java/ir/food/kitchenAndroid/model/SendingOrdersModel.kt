package ir.food.kitchenAndroid.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

data class SendingOrdersModel(
    val products: JSONArray,
    val id: String,
    val customerMobile: String,
    val customerFamily: String,
    val address: String,
    val statusName: String,
    val statusCode: Int,
    val createdAt: String,
    val description: String,
    val deliverName: String,
    val deliverMobile: String,
    val isPacked: Boolean,
    val totalPrice: String,
    val acceptTime: String,
    val location : LatLng
)
