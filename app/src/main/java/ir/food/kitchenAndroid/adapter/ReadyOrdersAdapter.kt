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
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.CallDialog
import ir.food.kitchenAndroid.dialog.CancelDialogOrder
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.DateHelper
import ir.food.kitchenAndroid.helper.StringHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.CartModel
import ir.food.kitchenAndroid.model.ReadyOrdersModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import ir.food.kitchenAndroid.webServices.FinishOrder
import org.json.JSONException
import org.json.JSONObject

class ReadyOrdersAdapter(list: ArrayList<ReadyOrdersModel>) :
    RecyclerView.Adapter<ReadyOrdersAdapter.ViewHolder>() {

    private val models = list

    lateinit var cartModels: ArrayList<CartModel>
    lateinit var adapter: CartAdapter
    lateinit var vfSetReady: ViewFlipper
    lateinit var btnPack: Button
    var orderId: String = ""

    class ViewHolder(val binding: ItemReadyOrdersBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReadyOrdersBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        if (model.isPacked) {
            holder.binding.vfSetReady.visibility = GONE
        } else {
            holder.binding.vfSetReady.visibility = VISIBLE
            holder.binding.vfSetReady.setBackgroundResource(R.drawable.bg_gray)
            holder.binding.btnPacked.isEnabled = true
            holder.binding.vfSetReady.displayedChild = 0
        }

        holder.binding.txtStatus.text = model.statusName
        holder.binding.txtCustomerName.text = model.customerFamily
        holder.binding.txtTime.text =
            StringHelper.toPersianDigits(DateHelper.parseFormatToStringNoDay(model.createdAt)) + "  " + StringHelper.toPersianDigits(
                DateHelper.parseFormat(model.createdAt)
            )
        holder.binding.txtAddress.text = model.address
        holder.binding.txtDescription.text = model.description + "\n" + model.systemDescription
        holder.binding.txtTotalPrice.text = StringHelper.setComma(model.totalPrice) + " تومان"

        var icon = R.drawable.ic_coooking
        var color = R.color.cooking

        if (model.statusCode == 2) {
            icon = R.drawable.ic_coooking
            color = R.color.cooking
        }

        var tapTwice = false
        holder.binding.btnPacked.setOnClickListener {
            btnPack = holder.binding.btnPacked
            vfSetReady = holder.binding.vfSetReady
            orderId = model.id
            if (tapTwice) {
                setPack()
            } else {
                tapTwice = true
                MyApplication.handler.postDelayed({ tapTwice = false }, 500)
            }
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

        holder.binding.btnFinishOrder.setOnClickListener {
            GeneralDialog()
                .message("ایا از اتمام سفارش اطمینان دارید؟")
                .firstButton("بله") {
                    holder.binding.vfFinishOrder.displayedChild = 1
                    FinishOrder().callFinishAPI(model.id, object : FinishOrder.FinishOrder {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onSuccess(b: Boolean) {
                            holder.binding.vfFinishOrder.displayedChild = 0
                            if (b) {
                                models.removeAt(position)
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
                .secondButton("خیر") { }
                .cancelable(false)
                .show()
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
    }

    override fun getItemCount(): Int {
        return models.size
    }

    private fun setPack() {
        vfSetReady.displayedChild = 1
        RequestHelper.builder(EndPoints.PACKED)
            .addParam("orderId", orderId)
            .listener(packCallBack)
            .put()
    }

    private val packCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
//                        {"success":true,"message":"سفارش با موفقیت بسته بندی شد"}
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {
                            vfSetReady.visibility = GONE
                        } else {
                            vfSetReady.displayedChild = 0
                            GeneralDialog()
                                .message(message)
                                .firstButton("بستن") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { setPack() }
                                .cancelable(false)
                                .show()
                        }
                    } catch (e: JSONException) {
                        vfSetReady.displayedChild = 0
                        GeneralDialog()
                            .message("مشکلی پیش آمده، لطفا مجدد امتحان کنید")
                            .firstButton("بستن") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { setPack() }
                            .cancelable(false)
                            .show()
                        e.printStackTrace()
                        AvaCrashReporter.send(e, "NotReadyOrderFragment class, readyCallBack")
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    vfSetReady.displayedChild = 0
                    GeneralDialog()
                        .firstButton("بستن") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { setPack() }
                        .cancelable(false)
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }
}