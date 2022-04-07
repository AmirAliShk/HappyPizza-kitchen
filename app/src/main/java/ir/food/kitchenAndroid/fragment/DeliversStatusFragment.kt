package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import ir.food.kitchenAndroid.adapter.DeliverStatusAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentDeliverStatusBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.DeliverStatusModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONException
import org.json.JSONObject

class DeliversStatusFragment : Fragment() {

    lateinit var binding: FragmentDeliverStatusBinding
    var statusModels: ArrayList<DeliverStatusModel> = ArrayList()
    var adapter: DeliverStatusAdapter = DeliverStatusAdapter(statusModels)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeliverStatusBinding.inflate(inflater)
        TypefaceUtil.overrideFonts(binding.root)

        getDeliversList()

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        return binding.root
    }

    private fun getDeliversList() {
        RequestHelper.builder(EndPoints.STATUS_DELI)
            .listener(deliveryCallback)
            .get()
    }

    private val deliveryCallback: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//{"success":true,"message":"وضعیت پیک ها با موفقیت ارسال شدند.","data":{"status":true,"deliveriesStatus":[{"id":"61d159f46fe5275b29f2cb97","name":"محمد جواد حیدری","orders":[{"customer":{"family":"محمد جواد حیدری"},"address":"موسوی قوچانی 25 بلوک 6 زنگ 5","status":{"status":1,"name":"لغو شده"},"deliveryAcceptedTime":"2022-01-16T09:22:57.896Z"}],"mobile":"09105044033","approveKitchen":0,"lastLocation":{"geo":[59.572952,36.298774],"saveDate":"2022-01-16T15:34:47.295Z"}}]}}
                    val response = JSONObject(args[0].toString())
                    val success = response.getBoolean("success")
                    val message = response.getString("message")

                    if (success) {
                        val dataObject = response.getJSONObject("data")
                        val status = dataObject.getBoolean("status")
                        if (status) {
                            val deliverStatus = dataObject.getJSONArray("deliveriesStatus")
                            for (i in 0 until deliverStatus.length()) {
                                val dataObj: JSONObject = deliverStatus.getJSONObject(i)
                                if (dataObj.getJSONObject("lastLocation").has("geo")) {
                                    val latlng =
                                        dataObj.getJSONObject("lastLocation").getJSONArray("geo")
                                    val model = DeliverStatusModel(
                                        dataObj.getString("id"),
                                        dataObj.getString("name"),
                                        dataObj.getString("mobile"),
                                        dataObj.getJSONArray("orders"),
                                        LatLng(
                                            latlng.getDouble(1), latlng.getDouble(0)
                                        ),
                                        dataObj.getInt("approveKitchen") == 1
                                    )
                                    statusModels.add(model)
                                } else {
                                    val model = DeliverStatusModel(
                                        dataObj.getString("id"),
                                        dataObj.getString("name"),
                                        dataObj.getString("mobile"),
                                        dataObj.getJSONArray("orders"),
                                        LatLng(
                                            0.0, 0.0
                                        ),
                                        dataObj.getInt("approveKitchen") == 1
                                    )
                                    statusModels.add(model)
                                }

                            }
                            if (statusModels.size == 0) {
                                binding.vfDeliverStatus.displayedChild = 2
                            } else {
                                binding.vfDeliverStatus.displayedChild = 1
                                binding.listDeliverStatus.adapter = adapter
                            }
                        }
                    } else {
                        binding.vfDeliverStatus.displayedChild = 3
                        GeneralDialog()
                            .message(message)
                            .firstButton("بستن") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { getDeliversList() }
                            .cancelable(false)
                            .show()
                    }
                } catch (e: JSONException) {
                    binding.vfDeliverStatus.displayedChild = 3
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("بستن") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getDeliversList() }
                        .cancelable(false)
                        .show()
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "CheckoutDeliFragment class, readyCallBack")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfDeliverStatus.displayedChild = 3
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("بستن") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getDeliversList() }
                    .cancelable(false)
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }
}