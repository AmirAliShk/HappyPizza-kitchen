package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.adapter.OrdersHistoryAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentOrdersHistoryBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.OrderHistoryModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class OrdersHistoryFragment : Fragment() {

    lateinit var binding: FragmentOrdersHistoryBinding
    private lateinit var response: String
    var readyOrdersModels: ArrayList<OrderHistoryModel> = ArrayList()
    var adapter: OrdersHistoryAdapter = OrdersHistoryAdapter(readyOrdersModels)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersHistoryBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)
        binding.txtTitle.typeface = MyApplication.IraSanSMedume
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        callList()

        binding.imgRefresh.setOnClickListener { callList() }

        binding.imgRefreshFail.setOnClickListener { callList() }

        binding.llRefresh.setOnClickListener {callList() }

        return binding.root
    }

    private fun callList(){
        binding.imgRefreshActionBar.startAnimation(
            AnimationUtils.loadAnimation(
                MyApplication.context,
                R.anim.rotate
            )
        )
        getHistory()
    }

    private fun getHistory() {
        RequestHelper.builder(EndPoints.HISTORY)
            .listener(historyCallBack)
            .get()
    }

    private val historyCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                binding.imgRefreshActionBar?.clearAnimation()
                try {
                    parseDate(args[0].toString())
                } catch (e: JSONException) {
                    binding.vfHistory.displayedChild = 2
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getHistory() }
                        .show()
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "OrdersHistoryFragment class, historyCallBack")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfHistory.displayedChild = 2
                binding.imgRefreshActionBar.clearAnimation()
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getHistory() }
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }

    private fun parseDate(result: String) {
        try {
            response = result
            val response = JSONObject(result)
// {"success":true,"message":"سفارشات با موفقیت ارسال شد","data":[{"products":[{"name":"مرغ و قارچ","quantity":1}],"_id":"61092c9e4af8121f58108d97","customer":{"mobile":"09105044033","family":"محمد جواد حیدری"},"address":"راهنمایی 24","status":{"name":"در حال پخت","status":2},"createdAt":"2021-08-03T11:46:38.117Z"},{"products":[{"name":"مرغ و قارچ","quantity":1},{"name":"سس کچاپ","quantity":2}],"_id":"61092c9e4af8121f58108d98","customer":{"mobile":"09105044033","family":"محمد جواد حیدری"},"address":"راهنمایی 24","status":{"name":"در حال پخت","status":2},"createdAt":"2021-08-03T11:46:38.117Z"}]}
            val success = response.getBoolean("success")
            val message = response.getString("message")

            if (success) {
                val dataObject = response.getJSONArray("data")

                for (i in 0 until dataObject.length()) {
                    val orderDetails: JSONObject = dataObject.getJSONObject(i)
                    val customer = orderDetails.getJSONObject("customer")
                    val status = orderDetails.getJSONObject("status")

                    if (orderDetails.has("deliveryId")) {
                        val deliveryId = orderDetails.getJSONObject("deliveryId")
                        val model = OrderHistoryModel(
                            orderDetails.getJSONArray("products"),
                            orderDetails.getString("_id"),
                            customer.getString("mobile"),
                            customer.getString("family"),
                            orderDetails.getString("address"),
                            status.getString("name"),
                            status.getInt("status"),
                            orderDetails.getString("finishDate"),
                            orderDetails.getString("description"),
                            deliveryId.getString("family"),
                            deliveryId.getString("mobile")
                        )
                        readyOrdersModels.add(model)
                    } else {
                        val model = OrderHistoryModel(
                            orderDetails.getJSONArray("products"),
                            orderDetails.getString("_id"),
                            customer.getString("mobile"),
                            customer.getString("family"),
                            orderDetails.getString("address"),
                            status.getString("name"),
                            status.getInt("status"),
                            orderDetails.getString("finishDate"),
                            orderDetails.getString("description"),
                            "0",
                            "0"
                        )
                        readyOrdersModels.add(model)
                    }
                }
                if (readyOrdersModels.size == 0) {
                    binding.vfHistory.displayedChild = 1
                } else {
                    binding.vfHistory.displayedChild = 0
                }
                binding.historyList.adapter = adapter
            } else {
                GeneralDialog()
                    .message(message)
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getHistory() }
                    .show()
                binding.vfHistory.displayedChild = 2
            }
        } catch (e: Exception) {
        binding.vfHistory.displayedChild = 2
        binding.imgRefreshActionBar.clearAnimation()
        e.printStackTrace()
    }
    }
}