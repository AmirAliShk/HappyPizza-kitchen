package ir.food.kitchenAndroid.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemOrdersHistoryBinding
import ir.food.kitchenAndroid.dialog.CallDialog
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.fragment.DeliverLocationFragment
import ir.food.kitchenAndroid.helper.DateHelper
import ir.food.kitchenAndroid.helper.FragmentHelper
import ir.food.kitchenAndroid.helper.StringHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.OrderHistoryModel
import ir.food.kitchenAndroid.model.CartModel
import ir.food.kitchenAndroid.webServices.CancelOrder

class OrdersHistoryAdapter(list: ArrayList<OrderHistoryModel>) :
    RecyclerView.Adapter<OrdersHistoryAdapter.ViewHolder>() {

    private val models = list

    lateinit var cartModels: ArrayList<CartModel>
    lateinit var adapter: CartAdapter

    class ViewHolder(val binding: ItemOrdersHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrdersHistoryBinding.inflate(
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
        holder.binding.txtAddress.text = model.address
        holder.binding.txtDescription.text = model.description
        holder.binding.txtDeliverName.text = model.deliverName

        var icon = R.drawable.ic_close
        var color = R.color.canceled
        when (model.statusCode) {
            0 -> { // pending
//                holder.binding.btnDeliverLocation.visibility = View.GONE
                holder.binding.llDeliverName.visibility = View.GONE
                holder.binding.imgCallDriver.visibility = View.GONE
                icon = R.drawable.ic_waiting_black
                color = R.color.waiting
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.black
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.black
                    )
                )
            }
            1 -> { // cancel
//                holder.binding.btnDeliverLocation.visibility = View.GONE
                icon = R.drawable.ic_close
                color = R.color.canceled
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
            2 -> { // cooking
//                holder.binding.btnDeliverLocation.visibility = View.GONE
                holder.binding.llDeliverName.visibility = View.GONE
                holder.binding.imgCallDriver.visibility = View.GONE
                icon = R.drawable.ic_coooking
                color = R.color.cooking
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
            3 -> { // sending
//                holder.binding.btnDeliverLocation.visibility = View.VISIBLE
                holder.binding.llDeliverName.visibility = View.VISIBLE
                holder.binding.imgCallDriver.visibility = View.VISIBLE
                icon = R.drawable.ic_delivery
                color = R.color.delivery
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
            4 -> { // finish
//                holder.binding.btnDeliverLocation.visibility = View.GONE
                holder.binding.llDeliverName.visibility = View.VISIBLE
                holder.binding.imgCallDriver.visibility = View.VISIBLE
                icon = R.drawable.ic_round_done_24
                color = R.color.finished
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
            5 -> { // preparing
//                holder.binding.btnDeliverLocation.visibility = View.GONE
                holder.binding.llDeliverName.visibility = View.GONE
                holder.binding.imgCallDriver.visibility = View.GONE
                icon = R.drawable.ic_chef
                color = R.color.preparing
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
            6 -> { // 6 waiting for pay
//                holder.binding.btnDeliverLocation.visibility = View.GONE
                holder.binding.llDeliverName.visibility = View.GONE
                holder.binding.imgCallDriver.visibility = View.GONE
                icon = R.drawable.ic_refresh_white
                color = R.color.color_Titles
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
            7 -> { //  7 calculated
//                holder.binding.btnDeliverLocation.visibility = View.GONE
                holder.binding.llDeliverName.visibility = View.GONE
                holder.binding.imgCallDriver.visibility = View.GONE
                icon = R.drawable.ic_baseline_attach_money_24
                color = R.color.calculated
                holder.binding.txtStatus.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
                holder.binding.txtTime.setTextColor(
                    MyApplication.currentActivity.resources.getColor(
                        R.color.white
                    )
                )
            }
        }

        holder.binding.btnDeliverLocation.setOnClickListener {
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                DeliverLocationFragment(model.location)
            )
                .add()
        }

        holder.binding.btnCancelOrder.setOnClickListener {
            GeneralDialog()
                .message("ایا از لغو سفارش اطمینان دارید؟")
                .firstButton("بله") {
                    holder.binding.vfCancelOrder.displayedChild = 1
                    CancelOrder().callCancelAPI(model.id, object : CancelOrder.CancelOrder{
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onSuccess(b: Boolean) {
                            holder.binding.vfCancelOrder.displayedChild = 0
                            if (b){
//                                holder.binding.btnDeliverLocation.visibility = View.GONE
                                holder.binding.vfCancelOrder.visibility=View.GONE
                                holder.binding.imgStatus.setImageResource(R.drawable.ic_close)
                                holder.binding.txtStatus.text = "لغو شده"
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    val header =
                                        AppCompatResources.getDrawable(MyApplication.context, R.drawable.bg_orders_header)
                                    holder.binding.llHeaderStatus.background = header
                                    DrawableCompat.setTint(
                                        header!!,
                                        MyApplication.currentActivity.resources.getColor( R.color.canceled)
                                    )
                                } else {
                                    holder.binding.llHeaderStatus.setBackgroundColor(
                                        MyApplication.currentActivity.resources.getColor(
                                            R.color.canceled
                                        )
                                    )
                                }
                                holder.binding.txtStatus.setTextColor(
                                    MyApplication.currentActivity.resources.getColor(
                                        R.color.white
                                    )
                                )
                                holder.binding.txtTime.setTextColor(
                                    MyApplication.currentActivity.resources.getColor(
                                        R.color.white
                                    )
                                )
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
    }

    override fun getItemCount(): Int {
        return models.size
    }

}