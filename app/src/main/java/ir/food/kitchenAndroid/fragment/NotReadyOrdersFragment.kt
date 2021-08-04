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
import ir.food.kitchenAndroid.adapter.ProductsAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentNotReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ProductModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class NotReadyOrdersFragment : Fragment() {

    private lateinit var binding: FragmentNotReadyOrdersBinding

    var productModels: ArrayList<ProductModel> = ArrayList()
    var adapter: ProductsAdapter = ProductsAdapter(productModels)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNotReadyOrdersBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.darkGray)
        }

        TypefaceUtil.overrideFonts(binding.root, MyApplication.IraSanSMedume)

        getOrders()
//        val data =
//            "{\"products\":[{ \"quantity\":3, \"name\":\"پپرونی\" },{ \"quantity\":3, \"name\":\"کوکا\" },{ \"quantity\":1, \"name\":\"سالاد فصل\" },{ \"quantity\":6, \"name\":\"سس کچاپ\" },{ \"quantity\":1, \"name\":\"نان سیر\" }]}"
//        val dataObject = JSONObject(data)
//        val active = dataObject.getJSONArray("products")
//        for (i in 0 until active.length()) {
//            val dataObj: JSONObject = active.getJSONObject(i)
//
//            var model = ProductModel(
//                dataObj.getString("name"),
//                dataObj.getInt("quantity")
//            )
//
//            productModels.add(model)
//        }
//        binding.productList.adapter = adapter

        binding.imgRefresh.setOnClickListener { getOrders() }

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.btnOrderReady.setOnClickListener {
            setReady()
            //todo change status of order
        }

        return binding.root
    }

    private fun getOrders() {
        if (binding.vfOrders != null) {
            binding.vfOrders.displayedChild = 0
        }

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
                        //    ""active":[{ "id":"60fe47361468d133e036ef4c", "products":[{ "quantity":3, "name":"پپرونی" },{ "quantity":3, "name":"کوکا" },{ "quantity":1, "name":"سالاد فصل" },{ "quantity":6, "name":"سس کچاپ" },{ "quantity":1, "name":"نان سیر" }], "createdAt":"2021-07-26T05:25:10.497Z", "customer":{ "_id":"60fcfe176ea36757d055ffe7", "mobile":"09307580143", "family":"زهرا رضوی" } }]"
                        val success = response.getBoolean("success")
                        val message = response.getString("message")

                        if (success) {
//                            val dataObject = response.getJSONObject("data")
                            val products = response.getJSONArray("data")
                            for (i in 0 until products.length()) {
                                val dataObj: JSONObject = products.getJSONObject(i)

                                var model = ProductModel(
                                    dataObj.getString("name"),
                                    dataObj.getInt("quantity")
                                )

                                productModels.add(model)
                            }

                            if (productModels.size == 0) {
                                binding.vfOrders.displayedChild = 2
                            } else {
                                binding.vfOrders.displayedChild = 1
                            }
                            binding.productList.adapter = adapter

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
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post { binding.vfOrders.displayedChild = 3 }
                super.onFailure(reCall, e)
            }
        }

    private fun setReady() {
        if (binding.vfSetReady != null) {
            binding.vfSetReady.displayedChild = 0
        }

        RequestHelper.builder(EndPoints.READY)
            .addParam("orderId", "order id")//todo
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

                        if (success) {
                            if (binding.vfSetReady != null) {
                                binding.vfSetReady.displayedChild = 1
                            }
                        }
                        //todo
                    } catch (e: JSONException) {
                        if (binding.vfSetReady != null) {
                            binding.vfSetReady.displayedChild = 1
                        }
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    if (binding.vfSetReady != null) {
                        binding.vfSetReady.displayedChild = 1
                    }
                }
                super.onFailure(reCall, e)
            }
        }
}