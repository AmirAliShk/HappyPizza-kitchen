package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.food.kitchenAndroid.R
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
        binding.vfCheckout?.displayedChild = 0
        RequestHelper.builder(EndPoints.DELI_FINANCIAL)
            .listener(checkoutCallBack)
            .get()
    }

    private val checkoutCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    val response = JSONObject(args[0].toString())


                } catch (e: JSONException) {
                    binding.vfCheckout?.displayedChild = 3
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getList() }
                        .show()
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "ReadyOrdersFragment class, readyCallBack")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfCheckout?.displayedChild = 3
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getList() }
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }

}