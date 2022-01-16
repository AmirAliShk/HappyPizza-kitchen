package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.adapter.SendingAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentSendingBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.SendingOrdersModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import kotlin.collections.ArrayList

class SendingFragment : Fragment() {

    lateinit var binding: FragmentSendingBinding
    private lateinit var response: String

    var readyOrdersModels: ArrayList<SendingOrdersModel> = ArrayList()
    var adapter: SendingAdapter = SendingAdapter(readyOrdersModels)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendingBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)
        binding.txtTitle.typeface = MyApplication.IraSanSMedume
        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        callList()

        binding.imgRefresh.setOnClickListener { callList() }

        binding.llRefresh.setOnClickListener { callList() }

        binding.imgRefreshFail.setOnClickListener { callList() }

        return binding.root
    }

    private fun callList() {
        binding.imgRefreshActionBar.startAnimation(
            AnimationUtils.loadAnimation(
                MyApplication.context,
                R.anim.rotate
            )
        )
        getSending()
    }

    private fun getSending() {
        RequestHelper.builder(EndPoints.SENDING)
            .listener(sendingCallBack)
            .get()
    }

    private val sendingCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                binding.imgRefreshActionBar.clearAnimation()
                try {
                    parseData(args[0].toString())
                } catch (e: JSONException) {
                    binding.vfOrdersPage.displayedChild = 2
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("بستن") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getSending() }
                        .cancelable(false)
                        .show()
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "SendingFragment class, readyCallBack")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfOrdersPage.displayedChild = 2
                binding.imgRefreshActionBar.clearAnimation()
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("بستن") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getSending() }
                    .cancelable(false)
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }

    private fun parseData(result: String) {
        try {
            readyOrdersModels.clear()
            response = result
            val response = JSONObject(result)

            val success = response.getBoolean("success")
            val message = response.getString("message")

            if (success) {
                val dataObject = response.getJSONArray("data")

                for (i in 0 until dataObject.length()) {
                    val orderDetails: JSONObject = dataObject.getJSONObject(i)
                    val customer = orderDetails.getJSONObject("customer")
                    val status = orderDetails.getJSONObject("status")

                    val latlng =
                        orderDetails.getJSONObject("deliveryId").getJSONObject("lastLocation")
                            .getJSONArray("geo")

                    val deliveryId = orderDetails.getJSONObject("deliveryId")
                    val model = SendingOrdersModel(
                        orderDetails.getJSONArray("products"),
                        orderDetails.getString("_id"),
                        customer.getString("mobile"),
                        customer.getString("family"),
                        orderDetails.getString("address"),
                        status.getString("name"),
                        status.getInt("status"),
                        orderDetails.getString("createdAt"),
                        orderDetails.getString("description"),
                        orderDetails.getString("systemDescription"),
                        deliveryId.getString("family"),
                        deliveryId.getString("mobile"),
                        orderDetails.getBoolean("isPack"),
                        orderDetails.getString("total"),
                        orderDetails.getString("deliveryAcceptedTime"),
                        LatLng(latlng.getDouble(1), latlng.getDouble(0))
                    )
                    readyOrdersModels.add(model)
                }
                if (readyOrdersModels.size == 0) {
                    binding.vfOrdersPage.displayedChild = 1
                } else {
                    binding.vfOrdersPage.displayedChild = 0
                }
                binding.readyList.adapter = adapter
            } else {
                GeneralDialog()
                    .message(message)
                    .firstButton("بستن") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getSending() }
                    .cancelable(false)
                    .show()
                binding.vfOrdersPage.displayedChild = 2
            }
        } catch (e: Exception) {
            binding.vfOrdersPage.displayedChild = 2
            binding.imgRefreshActionBar.clearAnimation()
            e.printStackTrace()
        }
    }
}