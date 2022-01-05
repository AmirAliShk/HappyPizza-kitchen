package ir.food.kitchenAndroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.DialogCancelOrderBinding
import ir.food.kitchenAndroid.helper.KeyBoardHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.push.AvaCrashReporter
import ir.food.kitchenAndroid.webServices.CancelOrder

class CancelDialogOrder {

    lateinit var dialog: Dialog
    lateinit var binding: DialogCancelOrderBinding

    fun show(orderId: String) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCancelOrderBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypefaceUtil.overrideFonts(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = wlp
        dialog.setCancelable(true)

        binding.imgClose.setOnClickListener {
            MyApplication.handler.postDelayed({
                dismiss()
                KeyBoardHelper.hideKeyboard()
            }, 200)
        }

//        binding.btnCancelOrder.setOnClickListener {
//
//            binding.vfCancelOrder.displayedChild = 1
//            CancelOrder().callCancelAPI(orderId, object : CancelOrder.CancelOrder {
//                @SuppressLint("NotifyDataSetChanged")
//                override fun onSuccess(b: Boolean) {
//                    binding.vfCancelOrder.displayedChild = 0
//                    if (b) {
////                                holder.binding.btnDeliverLocation.visibility = View.GONE
//                        holder.binding.vfCancelOrder.visibility = View.GONE
//                        holder.binding.imgStatus.setImageResource(R.drawable.ic_close)
//                        holder.binding.txtStatus.text = "لغو شده"
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            val header =
//                                AppCompatResources.getDrawable(
//                                    MyApplication.context,
//                                    R.drawable.bg_orders_header
//                                )
//                            holder.binding.llHeaderStatus.background = header
//                            DrawableCompat.setTint(
//                                header!!,
//                                MyApplication.currentActivity.resources.getColor(R.color.canceled)
//                            )
//                        } else {
//                            holder.binding.llHeaderStatus.setBackgroundColor(
//                                MyApplication.currentActivity.resources.getColor(
//                                    R.color.canceled
//                                )
//                            )
//                        }
//                        holder.binding.txtStatus.setTextColor(
//                            MyApplication.currentActivity.resources.getColor(
//                                R.color.white
//                            )
//                        )
//                        holder.binding.txtTime.setTextColor(
//                            MyApplication.currentActivity.resources.getColor(
//                                R.color.white
//                            )
//                        )
//                    } else {
//                        GeneralDialog()
//                            .message("مشکلی پیش آمده، لطفا مجدد امتحان کنید")
//                            .firstButton("بستن") { GeneralDialog().dismiss() }
//                            .cancelable(false)
//                            .show()
//                    }
//                }
//            })
//        }

        dialog.show()
    }

    private fun dismiss() {
        try {
            dialog.dismiss()
            KeyBoardHelper.hideKeyboard()
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "CallDialog class, dismiss method")
        }
    }

}