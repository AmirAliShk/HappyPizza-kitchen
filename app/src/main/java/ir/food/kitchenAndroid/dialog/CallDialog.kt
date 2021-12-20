package ir.food.kitchenAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.DialogCallBinding
import ir.food.kitchenAndroid.helper.CallHelper
import ir.food.kitchenAndroid.helper.KeyBoardHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.push.AvaCrashReporter

class CallDialog {

    lateinit var dialog: Dialog
    lateinit var binding: DialogCallBinding

    fun show(number1: String) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCallBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypefaceUtil.overrideFonts(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = wlp
        dialog.setCancelable(true)

        binding.imgClose.setOnClickListener { dismiss() }
        binding.txtNumber1.text = number1

        binding.llNumber1.setOnClickListener {
            CallHelper.make(if (number1.startsWith("0")) number1 else "0$number1")
        }

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