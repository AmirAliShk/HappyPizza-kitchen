package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentGetProductsBinding
import ir.food.kitchenAndroid.databinding.FragmentReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ReadyOrdersModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class GetProductsFragment : Fragment() {
    lateinit var binding: FragmentGetProductsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGetProductsBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        getProducts()

        return binding.root
    }

    private fun getProducts() {
        binding.vfProducts.displayedChild = 0
        RequestHelper.builder(EndPoints.GET_PRODUCTS)
            .listener(productsCallBack)
            .get()
    }

    private val productsCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    binding.vfProducts.displayedChild = 1
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
                                orderDetails.getString("createdAt"),
                                orderDetails.getString("description")
                            )
//                            readyOrdersModels.add(model)
                        }
//                        if (readyOrdersModels.size == 0) {
//                            binding.vfProducts.displayedChild = 2
//                        } else {
//                            binding.vfProducts.displayedChild = 1
//                        }
//                        binding.readyList.adapter = adapter
                    } else {
                        GeneralDialog()
                            .message(message)
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { getProducts() }
                            .show()
                        binding.vfProducts.displayedChild = 3
                    }

                } catch (e: JSONException) {
                    binding.vfProducts.displayedChild = 3
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getProducts() }
                        .show()
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfProducts.displayedChild = 3
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getProducts() }
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }

}