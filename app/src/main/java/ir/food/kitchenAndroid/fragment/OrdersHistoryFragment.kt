package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.adapter.OrdersHistoryAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentOrdersHistoryBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.OrderHistoryModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class OrdersHistoryFragment : Fragment() {

    lateinit var binding: FragmentOrdersHistoryBinding

    var readyOrdersModels: ArrayList<OrderHistoryModel> = ArrayList()
    var adapter: OrdersHistoryAdapter = OrdersHistoryAdapter(readyOrdersModels)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersHistoryBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        getHistory()
        return binding.root
    }

    private fun getHistory() {
        binding.vfHistory.displayedChild = 0
        RequestHelper.builder(EndPoints.HISTORY)
            .listener(historyCallBack)
            .get()
    }

    private val historyCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val response = JSONObject(args[0].toString())
// {"success":true,"message":"سفارشات با موفقیت ارسال شد","data":[{"products":[{"name":"مرغ و قارچ","quantity":1}],"_id":"61092c9e4af8121f58108d97","customer":{"mobile":"09105044033","family":"محمد جواد حیدری"},"address":"راهنمایی 24","status":{"name":"در حال پخت","status":2},"createdAt":"2021-08-03T11:46:38.117Z"},{"products":[{"name":"مرغ و قارچ","quantity":1},{"name":"سس کچاپ","quantity":2}],"_id":"61092c9e4af8121f58108d98","customer":{"mobile":"09105044033","family":"محمد جواد حیدری"},"address":"راهنمایی 24","status":{"name":"در حال پخت","status":2},"createdAt":"2021-08-03T11:46:38.117Z"}]}
                    val success = response.getBoolean("success")
                    val message = response.getString("message")

                    if (success) {
                        val dataObject = response.getJSONArray("data")

                        for (i in 0 until dataObject.length()) {
                            val orderDetails: JSONObject = dataObject.getJSONObject(i)
                            val customer = orderDetails.getJSONObject("customer")
                            val status = orderDetails.getJSONObject("status")

                            val model = OrderHistoryModel(
                                orderDetails.getJSONArray("products"),
                                orderDetails.getString("_id"),
                                customer.getString("family"),
                                orderDetails.getString("address"),
                                status.getString("name"),
                                status.getInt("status"),
                                orderDetails.getString("description"),
                                orderDetails.getString("finishDate")
                            )
                            readyOrdersModels.add(model)
                        }
                        if (readyOrdersModels.size == 0) {
                            binding.vfHistory.displayedChild = 2
                        } else {
                            binding.vfHistory.displayedChild = 1
                        }
                        binding.historyList.adapter = adapter
                    } else {
                        GeneralDialog()
                            .message(message)
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { getHistory() }
                            .show()
                        binding.vfHistory.displayedChild = 3
                    }

                } catch (e: JSONException) {
                    binding.vfHistory.displayedChild = 3
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getHistory() }
                        .show()
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfHistory.displayedChild = 3
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getHistory() }
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }
}