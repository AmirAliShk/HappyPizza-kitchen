package ir.food.kitchenAndroid.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.recyclerview.widget.RecyclerView
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemCheckoutBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.StringHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.CheckoutDeliModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONException
import org.json.JSONObject

class CheckoutDeliAdapter(list: ArrayList<CheckoutDeliModel>) :
    RecyclerView.Adapter<CheckoutDeliAdapter.ViewHolder>() {

    private val models = list
    var deliId = ""
    lateinit var viewFlipper: ViewFlipper
    var pos = -1

    class ViewHolder(val binding: ItemCheckoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCheckoutBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.iranSance)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtDeliverName.text = model.deliName
        holder.binding.txtTotalPrice.text = "${StringHelper.setComma(model.totalPrice)} تومان "
        holder.binding.txtTotalOnline.text = "${StringHelper.setComma(model.totalOnline)} تومان "
        holder.binding.txtRemainingAmount.text = "${StringHelper.setComma(model.remainder)} تومان "

        holder.binding.btnCheckoutDeli.setOnClickListener {
            deliId = model.deliId
            viewFlipper = holder.binding.vfCheckout
            pos = position
            GeneralDialog()
                .message("آیا از تسویه پیک اطمینان دارید؟")
                .firstButton("بله") { checkoutDeli() }
                .secondButton("خیر") {}
                .show()
        }

    }

    override fun getItemCount(): Int {
        return models.size
    }

    private fun checkoutDeli() {
        viewFlipper.displayedChild = 1
        RequestHelper.builder(EndPoints.CHECKOUT_DELI)
            .listener(checkoutDeliCallBack)
            .addParam("deliveryId", deliId)
            .put()
    }

    private val checkoutDeliCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    viewFlipper.displayedChild = 0
                    try {
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") {
                                    models.removeAt(pos)
                                    notifyDataSetChanged()
                                }
                                .cancelable(false)
                                .show()
                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("بستن") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { checkoutDeli() }
                                .cancelable(false)
                                .show()
                        }
                    } catch (e: JSONException) {
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("بستن") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { checkoutDeli() }
                            .cancelable(false)
                            .show()
                        e.printStackTrace()
                        AvaCrashReporter.send(e, "SendingAdapter class, readyCallBack")
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    viewFlipper.displayedChild = 0
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("بستن") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { checkoutDeli() }
                        .cancelable(false)
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }
}