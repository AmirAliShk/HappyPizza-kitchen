package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.adapter.CheckoutDeliAdapter
import ir.food.kitchenAndroid.adapter.ReadyOrdersAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentCheckoutDeliBinding
import ir.food.kitchenAndroid.databinding.FragmentReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.CheckoutDeliModel
import ir.food.kitchenAndroid.model.ReadyOrdersModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class CheckoutDeliFragment : Fragment() {
    lateinit var binding: FragmentCheckoutDeliBinding
    var checkoutModels: ArrayList<CheckoutDeliModel> = ArrayList()
    var adapter: CheckoutDeliAdapter = CheckoutDeliAdapter(checkoutModels)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutDeliBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        getList()

        return binding.root
    }

    private fun getList() {
        binding.vfCheckout.displayedChild = 0
        RequestHelper.builder(EndPoints.DELI_FINANCIAL)
            .listener(checkoutCallBack)
            .get()
    }

    private val checkoutCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val response = JSONObject(args[0].toString())

                    val success = response.getBoolean("success")
                    val message = response.getString("message")

                    if (success) {
                        val dataObject = response.getJSONObject("data")
                        val status = dataObject.getBoolean("status")
                        if (status) {
                            val deliveryFinancial = dataObject.getJSONArray("deliveryFinancial")
                            for (i in 0 until deliveryFinancial.length()) {
                                val dataObj: JSONObject = deliveryFinancial.getJSONObject(i)
                                val model = CheckoutDeliModel(
                                    dataObj.getString("_id"),
                                    dataObj.getString("name"),
                                    dataObj.getString("totalSaleAmount"),
                                    dataObj.getString("totalPaidOnline"),
                                    dataObj.getString("totalRemainingAmount")
                                )
                                checkoutModels.add(model)
                            }
                            if (checkoutModels.size == 0) {
                                binding.vfCheckout.displayedChild = 2
                            } else {
                                binding.vfCheckout.displayedChild = 1
                                binding.listCheckout.adapter = adapter
                            }
                        }
                    } else {
                        binding.vfCheckout.displayedChild = 3
                        GeneralDialog()
                            .message(message)
                            .firstButton("بستن") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { getList() }
                            .cancelable(false)
                            .show()
                    }

                } catch (e: JSONException) {
                    binding.vfCheckout.displayedChild = 3
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("بستن") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getList() }
                        .cancelable(false)
                        .show()
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "CheckoutDeliFragment class, readyCallBack")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfCheckout.displayedChild = 3
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("بستن") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getList() }
                    .cancelable(false)
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }

}