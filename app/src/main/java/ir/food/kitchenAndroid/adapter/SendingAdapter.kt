package ir.food.kitchenAndroid.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ViewFlipper
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemReadyOrdersBinding
import ir.food.kitchenAndroid.databinding.ItemSendingBinding
import ir.food.kitchenAndroid.dialog.CallDialog
import ir.food.kitchenAndroid.dialog.CancelDialogOrder
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.fragment.DeliverLocationFragment
import ir.food.kitchenAndroid.helper.DateHelper
import ir.food.kitchenAndroid.helper.FragmentHelper
import ir.food.kitchenAndroid.helper.StringHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.CartModel
import ir.food.kitchenAndroid.model.SendingOrdersModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import ir.food.kitchenAndroid.webServices.CancelOrder
import ir.food.kitchenAndroid.webServices.FinishOrder
import org.json.JSONException
import org.json.JSONObject

class SendingAdapter(list: ArrayList<SendingOrdersModel>) :
    RecyclerView.Adapter<SendingAdapter.ViewHolder>() {

    private val models = list

    lateinit var cartModels: ArrayList<CartModel>
    lateinit var adapter: CartAdapter
    lateinit var vfCancelDeliver: ViewFlipper
    var orderId: String = ""
    var clickedPos = -1

    class ViewHolder(val binding: ItemSendingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSendingBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        holder.binding.txtStatus.text = model.statusName
        holder.binding.txtCustomerName.text = model.customerFamily
        holder.binding.txtTime.text =
            StringHelper.toPersianDigits(DateHelper.parseFormatToStringNoDay(model.createdAt)) + "  " + StringHelper.toPersianDigits(
                DateHelper.parseFormat(model.createdAt)
            )

        holder.binding.txtAcceptTime.text =
            StringHelper.toPersianDigits(DateHelper.parseFormatToStringNoDay(model.acceptTime)) + "  " + StringHelper.toPersianDigits(
                DateHelper.parseFormat(model.acceptTime)
            )
        holder.binding.txtAddress.text = model.address
        holder.binding.txtDescription.text = model.description
        holder.binding.txtDeliverName.text = model.deliverName
        holder.binding.txtTotalPrice.text = StringHelper.setComma(model.totalPrice) + " تومان"

        var icon = R.drawable.ic_delivery
        var color = R.color.delivery
        if (model.statusCode == 3) {
            holder.binding.llDeliverName.visibility = VISIBLE
            holder.binding.imgCallDriver.visibility = VISIBLE
            icon = R.drawable.ic_delivery
            color = R.color.delivery
        }

        holder.binding.imgStatus.setImageResource(icon)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val header =
                AppCompatResources.getDrawable(MyApplication.context, R.drawable.bg_orders_header)
            holder.binding.llHeaderStatus.background = header
            DrawableCompat.setTint(
                header!!,
                MyApplication.currentActivity.resources.getColor(color)
            )
        } else {
            holder.binding.llHeaderStatus.setBackgroundColor(
                MyApplication.currentActivity.resources.getColor(
                    color
                )
            )
        }
        cartModels = ArrayList()
        adapter = CartAdapter(cartModels)
        for (i in 0 until model.products.length()) {
            val productsDetail = model.products.getJSONObject(i)
            val model = CartModel(
                productsDetail.getString("name"),
                productsDetail.getInt("quantity"),
                productsDetail.getString("size")
            )
            cartModels.add(model)
        }
        holder.binding.orderList.adapter = adapter

        holder.binding.imgCall.setOnClickListener { CallDialog().show(model.customerMobile) }
        holder.binding.imgCallDriver.setOnClickListener { CallDialog().show(model.deliverMobile) }
        holder.binding.btnCancelDeliver.setOnClickListener {
            clickedPos = position
            vfCancelDeliver = holder.binding.vfCancelDeliver
            orderId = model.id
            GeneralDialog()
                .message("آیا از لغو پیک اطمینان دارید؟")
                .firstButton("بله") { cancelDeliver() }
                .secondButton("خیر") {}
                .show()

        }

        holder.binding.btnFinishOrder.setOnClickListener {
            GeneralDialog()
                .message("ایا از اتمام سفارش اطمینان دارید؟")
                .firstButton("بله") {
                    holder.binding.vfFinishOrder.displayedChild = 1
                    FinishOrder().callFinishAPI(model.id,object : FinishOrder.FinishOrder{
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onSuccess(b: Boolean) {
                            holder.binding.vfFinishOrder.displayedChild = 0
                            if (b){
                                models.removeAt(position)
                                notifyDataSetChanged()
                            }else{
                                GeneralDialog()
                                    .message("مشکلی پیش آمده، لطفا مجدد امتحان کنید")
                                    .firstButton("بستن") { GeneralDialog().dismiss() }
                                    .cancelable(false)
                                    .show()
                            }
                        }
                    })
                }
                .secondButton("خیر") { }
                .cancelable(false)
                .show()
        }


        holder.binding.btnCancelOrder.setOnClickListener {
            CancelDialogOrder().show(model.id, object : CancelDialogOrder.CancelOrderDialog {
                override fun onSuccess(b: Boolean) {
                    if (b) {
                        models.removeAt(holder.adapterPosition)
                        notifyDataSetChanged()
                    } else {
                        GeneralDialog()
                            .message("مشکلی پیش آمده، لطفا مجدد امتحان کنید")
                            .firstButton("بستن") { GeneralDialog().dismiss() }
                            .cancelable(false)
                            .show()
                    }
                }
            })
        }

        holder.binding.btnDeliverLocation.setOnClickListener {
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                DeliverLocationFragment(model.location)
            )
                .add()
        }

    }

    override fun getItemCount(): Int {
        return models.size
    }

    private fun cancelDeliver() {
        vfCancelDeliver.displayedChild = 1
        RequestHelper.builder(EndPoints.CANCEL_DELIVER)
            .addParam("orderId", orderId)
            .listener(cancelDeliverCallBack)
            .put()
    }

    private val cancelDeliverCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    vfCancelDeliver.displayedChild = 0
                    try {
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {
                            models.remove(models[clickedPos])
                            notifyDataSetChanged()
                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("بستن") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { cancelDeliver() }
                                .cancelable(false)
                                .show()
                        }
                    } catch (e: JSONException) {
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("بستن") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { cancelDeliver() }
                            .cancelable(false)
                            .show()
                        e.printStackTrace()
                        AvaCrashReporter.send(e, "SendingAdapter class, readyCallBack")
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    vfCancelDeliver.displayedChild = 0
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("بستن") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { cancelDeliver() }
                        .cancelable(false)
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }
}