package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.adapter.CartAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentNotReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.CallDialog
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.DateHelper
import ir.food.kitchenAndroid.helper.StringHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.CartModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class NotReadyOrdersFragment : Fragment() {

    lateinit var binding: FragmentNotReadyOrdersBinding
    private lateinit var response: String
    private val KEY_ORDER = "lastOrder"
    lateinit var cartModels: ArrayList<CartModel>
    lateinit var adapter: CartAdapter
    lateinit var orderId: String
    private lateinit var timer: Timer
    lateinit var customerNum: String
    var tapTwice = false
    var lastFiveSecond: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotReadyOrdersBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        if (savedInstanceState != null) {
            response = savedInstanceState.getString(KEY_ORDER).toString()
            parseDate(response)
        }
        timer = Timer()
        if (MyApplication.prefManager.activeInQueue) {
            startGetOrdersTimer()
            binding.txtEnable.setBackgroundResource(R.drawable.bg_green)
            binding.txtDisable.setBackgroundResource(0)
            binding.txtDisable.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.black))
            binding.txtEnable.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.white))
        } else {
            stopGetOrdersTimer()
            binding.txtEnable.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.black))
            binding.txtDisable.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.white))
            binding.txtEnable.setBackgroundResource(0)
            binding.txtDisable.setBackgroundResource(R.drawable.bg_gray)
            binding.vfOrders.displayedChild = 0
        }
        binding.txtOrder.typeface = MyApplication.IraSanSMedume
        binding.txtQuantity.typeface = MyApplication.IraSanSMedume
        binding.txtSize.typeface = MyApplication.IraSanSMedume

        binding.imgRefresh.setOnClickListener { getOrders() }

        binding.imgRefreshFail.setOnClickListener { getOrders() }

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.btnOrderReady.setOnClickListener {
            if (tapTwice) {
                if (MyApplication.prefManager.activeInQueue) {
                    setReady()
                } else {
                    binding.vfOrders.displayedChild = 0
                }
            } else {
                tapTwice = true
                MyApplication.handler.postDelayed({ tapTwice = false }, 500)
            }
        }

        binding.imgCall.setOnClickListener { CallDialog().show(customerNum) }

        binding.txtEnable.setOnClickListener {
            startGetOrdersTimer()
            MyApplication.prefManager.activeInQueue = true
            binding.txtEnable.setBackgroundResource(R.drawable.bg_green)
            binding.txtDisable.setBackgroundResource(0)
            binding.txtDisable.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.black))
            binding.txtEnable.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.white))
        }

        binding.txtDisable.setOnClickListener {
            stopGetOrdersTimer()
            MyApplication.prefManager.activeInQueue = false
            binding.txtEnable.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.black))
            binding.txtDisable.setTextColor(MyApplication.currentActivity.resources.getColor(R.color.white))
            binding.txtEnable.setBackgroundResource(0)
            binding.txtDisable.setBackgroundResource(R.drawable.bg_gray)
            binding.pendingNum.text = ""
            binding.freeDeliver.text = ""
        }

        return binding.root
    }

    private fun getOrders() {
        binding.vfOrders.displayedChild = 1
        lastFiveSecond = Calendar.getInstance().timeInMillis + 5000
        RequestHelper.builder(EndPoints.NOT_READY_ORDER)
            .listener(ordersCallBack)
            .get()
    }

    private fun refresh() {
        RequestHelper.builder(EndPoints.NOT_READY_ORDER)
            .listener(ordersCallBack)
            .get()
    }

    private val ordersCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        parseDate(args[0].toString())
                    } catch (e: JSONException) {
                        binding.vfOrders.displayedChild = 4
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { getOrders() }
                            .show()
                        e.printStackTrace()
                        AvaCrashReporter.send(e, "NotReadyOrdersFragment class, ordersCallBack")
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
                    binding.vfOrders.displayedChild = 4
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
                        AvaCrashReporter.send(e, "NotReadyOrderFragment class, readyCallBack")
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
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    MyApplication.handler.post {
                        if (lastFiveSecond < Calendar.getInstance().timeInMillis) {
                            Log.i("TAG", "run: start timer")
                            refresh()
                        }
                    }
                }
            }, 0, 10000)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "NotReadyOrderFragment class, startGetOrdersTimer method")
        }
    }

    private fun stopGetOrdersTimer() {
        try {
            Log.i("TAG", "stopGetOrdersTimer: stop timer")
            timer.cancel()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "NotReadyOrderFragment class, stopGetOrdersTimer method")
        }
    }

    private fun parseDate(result: String) {
        try {
            response = result
            val response = JSONObject(result)
//{"success":true,"message":"سفارش با موفقیت ارسال شد","data":{"GPS":{"coordinates":[33.29792,59.605933],"type":"Point"},"products":[{"_id":{"_id":"61091b0ca9335b389819e896","name":"مرغ و قارچ"},"quantity":1,"size":"large"}],"_id":"61092c9e4af8121f58108d97","customer":{"_id":"6107bd65e5bdcc11fd46bff2","mobile":"09105044033","family":"محمد جواد حیدری"},"address":"راهنمایی 24","status":{"name":"در حال اماده سازی","status":5},"description":"ساعت 12 تحویل داده شود","createdAt":"2021-08-03T11:46:38.117Z","__v":0,"cookId":"610a6fa3e5bdcc11fd46c0aa"}}
            val success = response.getBoolean("success")
            val message = response.getString("message")
            if (success) {
                cartModels = ArrayList()
                adapter = CartAdapter(cartModels)
                val dataObject = response.getJSONObject("data")
                if (dataObject.toString() == "{}") {
                    binding.vfOrders.displayedChild = 3
                    binding.pendingNum.text = ""
                } else {
                    binding.vfOrders.displayedChild = 2
                    val cookOrder = dataObject.getJSONObject("cookOrder")
                    val products = cookOrder.getJSONArray("products")
                    for (i in 0 until products.length()) {
                        val productDetail: JSONObject = products.getJSONObject(i)
                        val productId = productDetail.getJSONObject("_id")
                        val model = CartModel(
                            productId.getString("name"),
                            productDetail.getInt("quantity"),
                            productDetail.getString("size")
                        )

                        cartModels.add(model)
                    }
                    binding.productList.adapter = adapter

                    orderId = cookOrder.getString("_id")
                    val customer = cookOrder.getJSONObject("customer")
                    customerNum = customer.getString("mobile")
                    val customerName = customer.getString("family")
                    val description = cookOrder.getString("description")
                    val date = cookOrder.getString("createdAt")

                    binding.customerName.text = customerName
                    binding.registerTime.text = StringHelper.toPersianDigits(
                        DateHelper.parseFormat(date)
                    )
                    binding.description.text =
                        StringHelper.toPersianDigits(description)
                    binding.pendingNum.text = dataObject.getString("queueOrderCount")
                    binding.freeDeliver.text = dataObject.getString("queueOrderCount")
                }
            } else {
                GeneralDialog()
                    .message(message)
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getOrders() }
                    .show()
                binding.vfOrders.displayedChild = 4
            }

        } catch (e: java.lang.Exception) {
            binding.vfSetReady.displayedChild = 0
            e.printStackTrace()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_ORDER, response)
    }

    override fun onDestroy() {
        stopGetOrdersTimer()
        super.onDestroy()
    }
}