package ir.food.kitchenAndroid.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.adapter.OrdersAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentNotReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.OrdersModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class NotReadyOrdersFragment : Fragment() {

    private lateinit var binding: FragmentNotReadyOrdersBinding

    //    lateinit var ordersModel: OrdersModel
    var ordersModels: ArrayList<OrdersModel> = ArrayList()
    var adapter: OrdersAdapter = OrdersAdapter(ordersModels)

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

        TypefaceUtil.overrideFonts(binding.root)

        getOrders()

        binding.listOrders.isNestedScrollingEnabled = false

        binding.imgRefresh.setOnClickListener { getOrders() }

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        binding.btnOrderReady.setOnClickListener {
            //todo set condition for first and last item
            if (binding.listOrders.scrollState == binding.listOrders.size - 1) {
                getOrders()
            } else {
                binding.listOrders.scrollToPosition(binding.listOrders.scrollState + 1)
            }
            //todo change status of order
        }

        return binding.root
    }

    private fun getOrders() {
        if (binding.vfOrders != null) {
            binding.vfOrders.displayedChild = 0
        }

        RequestHelper.builder(EndPoints.ORDER)
            .listener(ordersCallBack)
            .get()
    }

    private val ordersCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
//                   "active":[
//                   {
//                       "id":"60fe47361468d133e036ef4c",
//                       "products":[
//                       {
//                           "quantity":3,
//                           "name":"پپرونی"
//                       },
//                       {
//                           "quantity":3,
//                           "name":"کوکا"
//                       },
//                       {
//                           "quantity":1,
//                           "name":"سالاد فصل"
//                       },
//                       {
//                           "quantity":6,
//                           "name":"سس کچاپ"
//                       },
//                       {
//                           "quantity":1,
//                           "name":"نان سیر"
//                       }
//                       ],
//                       "createdAt":"2021-07-26T05:25:10.497Z",
//                       "customer":{
//                       "_id":"60fcfe176ea36757d055ffe7",
//                       "mobile":"09307580143",
//                       "family":"زهرا رضوی"
//                   }
//                   }
//                   ]
                        if (success) {
                            val dataObject = response.getJSONObject("data")
                            val active = dataObject.getJSONArray("active")
                            for (i in 0 until active.length()) {
                                val dataObj: JSONObject = active.getJSONObject(i)
                                val customer: JSONObject =
                                    active.getJSONObject(i).getJSONObject("customer")

                                var model = OrdersModel(
                                    dataObj.getString("id"),
                                    dataObj.getJSONArray("products"),
                                    dataObj.getString("createdAt"),
                                    customer.getString("_id"),
                                    customer.getString("family"),
                                    customer.getString("mobile")
                                )

                                ordersModels.add(model)
                            }

                            if (ordersModels.size == 0) {
                                binding.vfOrders.displayedChild = 2
                            } else {
                                binding.vfOrders.displayedChild = 1
                            }
                            binding.listOrders.adapter = adapter

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
}