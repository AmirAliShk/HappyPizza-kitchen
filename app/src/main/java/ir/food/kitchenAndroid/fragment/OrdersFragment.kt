package ir.food.kitchenAndroid.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.adapter.OrdersAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentOrdersBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.OrdersModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    lateinit var ordersModel: OrdersModel
    lateinit var ordersModels: ArrayList<OrdersModel>
    lateinit var adapter: OrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOrdersBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.darkGray)
        }

        TypefaceUtil.overrideFonts(binding.root)

        getOrders()

        return binding.root
    }

    private fun getOrders() {
//        if (binding.vfOrders != null) {
//            binding.vfOrders.displayedChild = 0
//        }

        RequestHelper.builder(EndPoints.ORDER)
            .listener(ordersCallBack)
            .get()
    }

    private val ordersCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        ordersModels = ArrayList()
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
//                        active: [{
//                            id: "60b72a70e353f0385c2fe5af",
//                            products: [{
//                            name: "لاته",
//                            quantity: 2,
//                        }],
//                            customer: {
//                            _id: "7465148754878",
//                            family: "مصطفایی",
//                            mobile: "09152631225",
//                        },
//                            createdAt: "2021-06-01T06:54:01.691Z",
//                            address: "معلم 43"
//                        }]
                        if (success) {
                            val dataObject = response.getJSONObject("data")
                            val active = dataObject.getJSONArray("active")
                            for (i in 0 until active.length()) {
                                val dataObj: JSONObject = active.getJSONObject(i)
                                val customer = active.getJSONObject(i)

                                var model = OrdersModel(
                                    dataObj.getString("id"),
                                    dataObj.getJSONArray("products"),
                                    customer.getString("_id"),
                                    customer.getString("family"),
                                    customer.getString("mobile"),
                                    dataObj.getString("createdAt"),
                                    dataObj.getString("address"),
                                )

                                ordersModels.add(model)
                            }
//
                            if (ordersModels.size == 0) {
//                                binding.vfOrders.displayedChild = 2
                            } else {
//                                binding.vfOrders.displayedChild = 1
                                adapter = OrdersAdapter(ordersModels)
                            }
                            binding.listOrders.adapter = adapter;
                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { getOrders() }
                                .show()
//                            binding.vfOrders.displayedChild = 3
                        }
                    } catch (e: JSONException) {
//                        binding.vfOrders.displayedChild = 3
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
//                MyApplication.handler.post { binding.vfOrders.displayedChild = 3 }
                super.onFailure(reCall, e)
            }
        }
}