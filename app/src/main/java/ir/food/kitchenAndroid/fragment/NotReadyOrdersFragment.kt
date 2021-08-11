package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.adapter.ProductsAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentNotReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.CallDialog
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.DateHelper
import ir.food.kitchenAndroid.helper.StringHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ProductModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class NotReadyOrdersFragment : Fragment() {

    lateinit var binding: FragmentNotReadyOrdersBinding

    lateinit var productModels: ArrayList<ProductModel>
    lateinit var adapter: ProductsAdapter
    lateinit var orderId: String
    private lateinit var timer: Timer
    lateinit var customerNum: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotReadyOrdersBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)

        getOrders()
        startGetOrdersTimer()

        binding.imgRefresh.setOnClickListener { getOrders() }

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.btnOrderReady.setOnClickListener { setReady() }

        binding.imgCall.setOnClickListener { CallDialog().show(customerNum) }

        return binding.root
    }

    private fun getOrders() {
        binding.vfOrders.displayedChild = 0

        RequestHelper.builder(EndPoints.NOT_READY_ORDER)
            .listener(ordersCallBack)
            .get()
    }

    private val ordersCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        val response = JSONObject(args[0].toString())
//{"success":true,"message":"سفارش با موفقیت ارسال شد","data":{"GPS":{"coordinates":[33.29792,59.605933],"type":"Point"},"products":[{"_id":{"_id":"61091b0ca9335b389819e896","name":"مرغ و قارچ"},"quantity":1,"size":"large"}],"_id":"61092c9e4af8121f58108d97","customer":{"_id":"6107bd65e5bdcc11fd46bff2","mobile":"09105044033","family":"محمد جواد حیدری"},"address":"راهنمایی 24","status":{"name":"در حال اماده سازی","status":5},"description":"ساعت 12 تحویل داده شود","createdAt":"2021-08-03T11:46:38.117Z","__v":0,"cookId":"610a6fa3e5bdcc11fd46c0aa"}}
                        val success = response.getBoolean("success")
                        val message = response.getString("message")

                        if (success) {
                            productModels = ArrayList()
                            adapter = ProductsAdapter(productModels)
                            val dataObject = response.getJSONObject("data")
                            if (dataObject.toString() == "{}") {
                                binding.vfOrders.displayedChild = 2
                            } else {
                                binding.vfOrders.displayedChild = 1
                                val products = dataObject.getJSONArray("products")
                                for (i in 0 until products.length()) {
                                    val productDetail: JSONObject = products.getJSONObject(i)
                                    val productId = productDetail.getJSONObject("_id")
                                    var model = ProductModel(
                                        productId.getString("name"),
                                        productDetail.getInt("quantity"),
                                        productDetail.getString("size")
                                    )

                                    productModels.add(model)
                                }
                                binding.productList.adapter = adapter

                                orderId = dataObject.getString("_id")

                                val customer = dataObject.getJSONObject("customer")
                                customerNum = customer.getString("mobile")
                                val customerName = customer.getString("family")

                                val address = dataObject.getString("address")

//                            val status = dataObject.getJSONObject("status")
//                            val statusCode = status.getInt("status")
//                            val statusName = status.getString("name")

                                val description = dataObject.getString("description")

                                val date = dataObject.getString("createdAt")

                                binding.customerName.text = customerName
                                binding.time.text =
                                    StringHelper.toPersianDigits(
                                        DateHelper.parseFormatToStringNoDay(date) + "  " + StringHelper.toPersianDigits(
                                            DateHelper.parseFormat(date)
                                        )
                                    )
                                binding.description.text = StringHelper.toPersianDigits(description)
                                binding.txtAddress.text = StringHelper.toPersianDigits(address)
                            }
                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { getOrders() }
                                .show()
                            binding.vfOrders.displayedChild = 3
                        }
                    } catch (e: JSONException) {
                        binding.vfOrders.displayedChild = 3
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { getOrders() }
                            .show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
                    binding.vfOrders.displayedChild = 3
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getOrders() }
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }

    private fun setReady() {
        binding.vfSetReady.displayedChild = 1
        RequestHelper.builder(EndPoints.READY)
            .addParam("orderId", orderId)
            .listener(readyCallBack)
            .put()
    }

    private val readyCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {
                            binding.vfSetReady.displayedChild = 0
                            getOrders()
                        } else {
                            binding.vfSetReady.displayedChild = 0
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { setReady() }
                                .show()
                        }
                    } catch (e: JSONException) {
                        binding.vfSetReady.displayedChild = 0
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { setReady() }
                            .show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    binding.vfSetReady.displayedChild = 0
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { setReady() }
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }

    private fun startGetOrdersTimer() {
        timer = Timer()
        try {
            if (timer != null) {
                return
            }
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    getOrders()
                }
            }, 0, 10000)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun stopGetOrdersTimer() {
        try {
            if (timer != null) {
                timer.cancel()
//                timer = null //todo
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        stopGetOrdersTimer()
        super.onDestroy()
    }
}