package ir.food.kitchenAndroid.dialog

import android.app.Dialog
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.app.MyApplication
import android.view.LayoutInflater
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import ir.food.kitchenAndroid.helper.TypefaceUtil
import android.content.DialogInterface
import android.graphics.Color
import android.view.View
import android.view.Window
import ir.food.kitchenAndroid.databinding.DialogGeneralBinding
import java.lang.Exception

class GeneralDialog {
    private var bodyRunnable: Runnable? = null
    private var dismissBody: Runnable? = null
    private var firstBtn: ButtonModel? = null
    private var secondBtn: ButtonModel? = null
    private var thirdBtn: ButtonModel? = null
    private var messageText = ""
    private var titleText = ""
    private var visibility = 0
    private var cancelable = true
    private var singleInstance = false
    var binding: DialogGeneralBinding? = null

    private inner class ButtonModel {
        var text: String? = null
        var body: Runnable? = null
    }

    fun isSingleMode(singleInstance: Boolean): GeneralDialog {
        this.singleInstance = singleInstance
        return this
    }

    fun messageVisibility(visible: Int): GeneralDialog {
        visibility = visible
        return this
    }

    fun afterDismiss(dismissBody: Runnable?): GeneralDialog {
        this.dismissBody = dismissBody
        return this
    }

    fun firstButton(name: String?, body: Runnable?): GeneralDialog {
        firstBtn = ButtonModel()
        firstBtn!!.body = body
        firstBtn!!.text = name
        return this
    }

    fun secondButton(name: String?, body: Runnable?): GeneralDialog {
        secondBtn = ButtonModel()
        secondBtn!!.body = body
        secondBtn!!.text = name
        return this
    }

    fun thirdButton(name: String?, body: Runnable?): GeneralDialog {
        thirdBtn = ButtonModel()
        thirdBtn!!.body = body
        thirdBtn!!.text = name
        return this
    }

    fun bodyRunnable(bodyRunnable: Runnable?): GeneralDialog {
        this.bodyRunnable = bodyRunnable
        return this
    }

    fun message(messageText: String): GeneralDialog {
        this.messageText = messageText
        return this
    }

    fun title(titleText: String): GeneralDialog {
        this.titleText = titleText
        return this
    }

    fun cancelable(cancelable: Boolean): GeneralDialog {
        this.cancelable = cancelable
        return this
    }

    private var dialog: Dialog? = null
    private var staticDialog: Dialog? = null
    fun show() {
        if (MyApplication.currentActivity == null || MyApplication.currentActivity.isFinishing) return
        var tempDialog: Dialog? = null
        if (singleInstance) {
            if (staticDialog != null) {
                staticDialog!!.dismiss()
                staticDialog = null
            }
            staticDialog = Dialog(MyApplication.currentActivity)
            tempDialog = staticDialog
        } else {
            dialog = Dialog(MyApplication.currentActivity)
            tempDialog = dialog
        }
        tempDialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogGeneralBinding.inflate(
            LayoutInflater.from(
                dialog!!.context
            )
        )
        tempDialog.setContentView(binding!!.root)
        tempDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = tempDialog.window!!.attributes
        tempDialog.window!!.attributes = wlp
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        tempDialog.setCancelable(cancelable)
        TypefaceUtil.overrideFonts(tempDialog.window!!.decorView)
        binding!!.txtMessage.text = messageText
        if (messageText.isEmpty()) {
            binding!!.txtMessage.visibility = View.GONE
        }
        if (firstBtn == null) {
            binding!!.btnPositive.visibility = View.GONE
            binding!!.vMiddle.visibility = View.GONE
        } else {
            binding!!.btnPositive.text = firstBtn!!.text
        }
        if (secondBtn == null) {
            binding!!.btnNegative.visibility = View.GONE
            binding!!.vMiddle.visibility = View.GONE
        } else {
            binding!!.btnNegative.text = secondBtn!!.text
        }
        if (firstBtn == null && secondBtn == null && thirdBtn == null) {
            binding!!.llBtnView.visibility = View.GONE
        }
        binding!!.btnNegative.setOnClickListener { view: View? ->
            dismiss()
            if (secondBtn != null) {
                if (secondBtn!!.body != null) secondBtn!!.body!!.run()
            }
        }
        binding!!.btnPositive.setOnClickListener { view: View? ->
            dismiss()
            if (firstBtn != null) {
                if (firstBtn!!.body != null) {
                    firstBtn!!.body!!.run()
                }
            }
        }
        if (bodyRunnable != null) bodyRunnable!!.run()
        tempDialog.setOnDismissListener { dialog: DialogInterface? -> if (dismissBody != null) dismissBody!!.run() }
        tempDialog.show()
    }

    // dismiss center control
    fun dismiss() {
        try {
            if (singleInstance) {
                if (staticDialog != null) {
                    staticDialog!!.dismiss()
                    staticDialog = null
                }
            } else {
                if (dialog != null) if (dialog!!.isShowing) dialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dialog = null
    }

    companion object {
        const val ERROR = "error"
    }
}