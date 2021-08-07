package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.adapter.ReadyOrdersAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ReadyOrdersModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class ReadyOrdersFragment : Fragment() {

    lateinit var binding: FragmentReadyOrdersBinding

    var readyOrdersModels: ArrayList<ReadyOrdersModel> = ArrayList()
    var adapter: ReadyOrdersAdapter = ReadyOrdersAdapter(readyOrdersModels)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReadyOrdersBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }
        getReady()
        return binding.root
    }

    private fun getReady() {
        binding.vfOrders.displayedChild = 0
        RequestHelper.builder(EndPoints.READY)
            .listener(readyCallBack)
            .get()
    }

    private val readyCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    binding.vfOrders.displayedChild = 1
                    val response = JSONObject(args[0].toString())

                    val success = response.getBoolean("success")
                    val message = response.getString("message")

                    if (success) {
                        val dataObject = response.getJSONArray("data")

                        for (i in 0 until dataObject.length()) {
                            val orderDetails: JSONObject = dataObject.getJSONObject(i)
                            val customer = orderDetails.getJSONObject("customer")
                            val status = orderDetails.getJSONObject("status")

                            val model = ReadyOrdersModel(
                                orderDetails.getJSONArray("products"),
                                orderDetails.getString("_id"),
                                customer.getString("mobile"),
                                customer.getString("family"),
                                orderDetails.getString("address"),
                                status.getString("name"),
                                status.getInt("status"),
                                orderDetails.getString("createdAt")
                            )
                            readyOrdersModels.add(model)
                        }
                        if (readyOrdersModels.size == 0) {
                            binding.vfOrders.displayedChild = 2
                        } else {
                            binding.vfOrders.displayedChild = 1
                        }
                        binding.readyList.adapter = adapter
                    } else {
                        GeneralDialog()
                            .message(message)
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { getReady() }
                            .show()
                        binding.vfOrders.displayedChild = 3
                    }

                } catch (e: JSONException) {
                    binding.vfOrders.displayedChild = 3
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getReady() }
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
                    .secondButton("تلاش مجدد") { getReady() }
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }
}