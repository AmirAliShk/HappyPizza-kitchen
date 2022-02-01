package ir.food.kitchenAndroid.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ItemDeliverStatusBinding
import ir.food.kitchenAndroid.dialog.CallDialog
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.fragment.DeliverLocationFragment
import ir.food.kitchenAndroid.helper.FragmentHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.DeliverOrderModel
import ir.food.kitchenAndroid.model.DeliverStatusModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DeliverStatusAdapter(list: ArrayList<DeliverStatusModel>) :
    RecyclerView.Adapter<DeliverStatusAdapter.ViewHolder>() {
    private val models = list
    var statusInt: Int = 0
    private lateinit var holder: ViewHolder
    var pos = -1

    class ViewHolder(val binding: ItemDeliverStatusBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDeliverStatusBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root, MyApplication.iranSance)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        this.holder = holder

        holder.binding.txtDeliverName.text = model.deliName

        holder.binding.imgCall.setOnClickListener { CallDialog().show(model.deliNum) }

        holder.binding.btnDeliverLocation.setOnClickListener {
            FragmentHelper.toFragment(
                MyApplication.currentActivity,
                DeliverLocationFragment(model.deliLoc, model.deliId)
            )
                .setAddToBackStack(true)
                .replace()
        }

        holder.binding.btnActiveDeliver.setOnClickListener {
            this.holder = holder
            pos = holder.adapterPosition
            setStatus(model.deliId, 1)
            holder.binding.vfDeliverOrders.visibility = VISIBLE
            showList(model.deliOrders)
        }

        holder.binding.btnDeActiveDeliver.setOnClickListener {
            this.holder = holder
            pos = holder.adapterPosition
            holder.binding.vfDeliverOrders.visibility = GONE
            setStatus(model.deliId, 0)
        }

        if (model.approveKitchen) {
            holder.binding.vfActive.displayedChild = 1
            holder.binding.vfDeliverOrders.visibility = VISIBLE
            holder.binding.listDeliverOrders.visibility = VISIBLE
            showList(model.deliOrders)
        } else {
            holder.binding.vfActive.displayedChild = 0
            holder.binding.vfDeliverOrders.visibility = GONE
            holder.binding.listDeliverOrders.visibility = GONE
        }

    }

    override fun getItemCount(): Int {
        return models.size
    }

    private fun showList(deliOrders: JSONArray) {
        val statusModels: ArrayList<DeliverOrderModel> = ArrayList()
        val adapter = DeliverOrderAdapter(statusModels)
        try {
            for (i in 0 until deliOrders.length()) {
                val dataObj: JSONObject = deliOrders.getJSONObject(i)

                val model = DeliverOrderModel(
                    dataObj.getJSONObject("status").getString("name"),
                    dataObj.getJSONObject("status").getInt("status"),
                    dataObj.getString("deliveryAcceptedTime"),
                    dataObj.getJSONObject("customer").getString("family"),
                    if (dataObj.has("finishDate")) dataObj.getString("finishDate") else "",
                    dataObj.getString("address")
                )
                statusModels.add(model)
            }
            if (statusModels.size == 0) {
                holder.binding.vfDeliverOrders.displayedChild = 2
                holder.binding.listDeliverOrders.visibility = GONE
            } else {
                holder.binding.vfDeliverOrders.displayedChild = 1
                holder.binding.listDeliverOrders.visibility = VISIBLE
                holder.binding.listDeliverOrders.adapter = adapter
            }
        } catch (e: Exception) {
            e.printStackTrace()
            holder.binding.vfDeliverOrders.displayedChild = 3
            AvaCrashReporter.send(e, "DeliverStatusAdapter class, deliverStatus for")
        }
    }

    private fun setStatus(id: String, status: Int) {
        this.statusInt = status
        holder.binding.vfActive.displayedChild = 2
        RequestHelper.builder(EndPoints.STATUS_DELI)
            .addParam("deliveryId", id)
            .addParam("status", status)
            .listener(statusCallback)
            .put()
    }

    private val statusCallback: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
//                    {"success":true,"message":"پیک مورد نظر فعال شد","data":{"status":true}}
                    val response = JSONObject(args[0].toString())
                    val success = response.getBoolean("success")
                    val message = response.getString("message")

                    if (success) {
                        val dataObject = response.getJSONObject("data")
                        val status = dataObject.getBoolean("status")
                        if (status) {
                            notifyDataSetChanged()
                            when (statusInt) {
                                1 -> {
                                    holder.binding.vfActive.displayedChild = 1
                                    models[pos].approveKitchen = true
                                }
                                0 -> {
                                    holder.binding.vfActive.displayedChild = 0
                                    models[pos].approveKitchen = false
                                }
                            }
                        } else {
                            when (statusInt) {
                                1 -> {
                                    holder.binding.vfActive.displayedChild = 0
                                }
                                0 -> {
                                    holder.binding.vfActive.displayedChild = 1
                                }
                            }
                        }
                    } else {
                        when (statusInt) {
                            1 -> {
                                holder.binding.vfActive.displayedChild = 0
                            }
                            0 -> {
                                holder.binding.vfActive.displayedChild = 1
                            }
                        }
                        GeneralDialog()
                            .message(message)
                            .firstButton("بستن") { GeneralDialog().dismiss() }
                            .cancelable(false)
                            .show()
                    }
                } catch (e: JSONException) {
                    when (statusInt) {
                        1 -> {
                            holder.binding.vfActive.displayedChild = 0
                        }
                        0 -> {
                            holder.binding.vfActive.displayedChild = 1
                        }
                    }
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("بستن") { GeneralDialog().dismiss() }
                        .cancelable(false)
                        .show()
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "CheckoutDeliFragment class, readyCallBack")
                }
            }
        }
    }

}