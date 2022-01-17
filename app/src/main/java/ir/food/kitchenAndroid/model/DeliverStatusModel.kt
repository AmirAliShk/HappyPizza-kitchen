package ir.food.kitchenAndroid.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

data class DeliverStatusModel(
    val deliId: String,
    val deliName: String,
    val deliNum: String,
    val deliOrders: JSONArray,
    val deliLoc: LatLng,
    var approveKitchen: Boolean
)
