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
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentOrdersBinding
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class OrdersFragment : Fragment() {

    lateinit var binding: FragmentOrdersBinding


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
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.white)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.white)
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        TypefaceUtil.overrideFonts(binding.root)

        return binding.root
    }

//    private fun getOrders() {
//        if (binding.vfOrders != null) {
//            binding.vfOrders.displayedChild = 0
//        }
//
//        RequestHelper.builder(EndPoints.ORDER)
//            .listener(ordersCallBack)
//            .get()
//    }
//
//    private val ordersCallBack: RequestHelper.Callback =
//        object : RequestHelper.Callback() {
//            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
//                MyApplication.handler.post {
//                    try {
//                        ordersModel = ArrayList()
//                        val response = JSONObject(args[0].toString())
//                        val success = response.getBoolean("success")
//                        val message = response.getString("message")
//
//                        if (success) {
//                            val dataObject = response.getJSONArray("data")
//                            for (i in 0 until dataObject.length()) {
//                                val dataObj: JSONObject = dataObject.getJSONObject(i);
//                                var model = OrdersModel(
//                                    dataObj.getString("active")
//                                )
////                                "active":true,
////                                "_id":"60d7291c519b311c905f9567",
////                                "name":"لاته",
////                                "sellingPrice":"17000",
////                                "user":"60d72865519b311c905f9566",
////                                "updatedAt":"2021-06-27T10:49:51.916Z",
////                                "createdAt":"2021-06-26T13:18:20.104Z",
////                                "v":0,
////                                "description":"شیر قهوه"
//                                ordersModel.add(model)
//                            }
//
//                            if (ordersModel.size == 0) {
//                                binding.vfOrders.displayedChild = 2
//                            } else {
//                                binding.vfOrders.displayedChild = 1
//                                adapter = OrdersAdapter(ordersModel)
//                            }
//                            binding.listOrders.adapter = adapter;
//                        } else {
//                            GeneralDialog()
//                                .message(message)
//                                .firstButton("باشه") { GeneralDialog().dismiss() }
//                                .secondButton("تلاش مجدد") { getOrders() }
//                                .show()
//                            binding.vfOrders.displayedChild = 3
//                        }
//                    } catch (e: JSONException) {
//                        binding.vfOrders.displayedChild = 3
//                        e.printStackTrace()
//                    }
//                }
//            }
//
//            override fun onFailure(reCall: Runnable?, e: Exception?) {
//                MyApplication.handler.post { binding.vfOrders.displayedChild = 3 }
//                super.onFailure(reCall, e)
//            }
//        }
}