package ir.food.kitchenAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.EditText
import android.widget.Spinner
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.DialogProductsBinding
import ir.food.kitchenAndroid.helper.KeyBoardHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ProductModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONException
import org.json.JSONObject

class ProductDialog {
    lateinit var dialog: Dialog
    lateinit var binding: DialogProductsBinding
    var pCount = ""

    interface ProductDialogInterface {
        fun dismissListener(b: Boolean, value: String)
    }

    lateinit var pDialogInterface: ProductDialogInterface

    fun show(productModel: ProductModel, type: Int, pDialogInterface: ProductDialogInterface) {
        dialog = Dialog(MyApplication.currentActivity)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DialogProductsBinding.inflate(LayoutInflater.from(MyApplication.context))
        dialog.setContentView(binding.root)
        TypefaceUtil.overrideFonts(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp: WindowManager.LayoutParams? = dialog.window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = wlp
        dialog.setCancelable(false)
        binding.imgClose.setOnClickListener { dismiss() }
        this.pDialogInterface = pDialogInterface
        setCursorEnd(binding.root)
        MyApplication.handler.postDelayed({
            if (type == 1) { // 1 mean edit
                binding.txtType.text = productModel.typeName
                binding.edtPName.text = productModel.name
                binding.edtQuantity.setText(productModel.supply.toString())
                binding.edtDesc.text = productModel.description
            }
        }, 100)
        binding.btnRegister.setOnClickListener {
            val message = if (type == 1) {
                "آیا از ویرایش محصول اطمینان دارید؟"
            } else {
                "آیا از ثبت محصول اطمینان دارید؟"
            }
            GeneralDialog()
                .message(message)
                .firstButton("بله") {
                    if (type == 1) {
                        editProductType(productModel.id, binding.edtQuantity.text.trim().toString())
                    } else {
                        addProductType(productModel.id, binding.edtQuantity.text.trim().toString())
                    }
                }
                .secondButton("خیر") { dismiss() }
                .show()
        }
        dialog.show()
    }

    private fun setCursorEnd(v: View?) {
        try {
            if (v is ViewGroup) {
                val vg = v
                for (i in 0 until vg.childCount) {
                    val child = vg.getChildAt(i)
                    setCursorEnd(child)
                }
            } else if (v is EditText) {
                val e = v
                e.onFocusChangeListener = View.OnFocusChangeListener { view: View?, b: Boolean ->
                    if (b) MyApplication.handler.postDelayed(
                        { e.setSelection(e.text.length) },
                        200
                    )
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "ProductDialog class, setCursorEnd method")
            // ignore
        }
    }

    private fun editProductType(id: String, supply: String) {
        binding.vfSubmit.displayedChild = 1
        pCount = supply
        RequestHelper.builder(EndPoints.EDIT_PRODUCTS)
            .listener(editProductTypeCallBack)
            .addParam("productId", id)
            .addParam("supply", supply)
            .put()
    }

    private val editProductTypeCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        binding.vfSubmit.displayedChild = 0
                        dismiss()
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {

                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") {
                                    pDialogInterface.dismissListener(true, pCount)
                                    GeneralDialog().dismiss()
                                }
                                .show()
                        } else {
                            GeneralDialog()
                                .message(message)
                                .secondButton("باشه") { GeneralDialog().dismiss() }
                                .show()
                        }

                    } catch (e: JSONException) {
                        binding.vfSubmit.displayedChild = 0
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .secondButton("باشه") { GeneralDialog().dismiss() }
                            .show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    binding.vfSubmit.displayedChild = 0
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .secondButton("باشه") { GeneralDialog().dismiss() }
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }

    private fun addProductType(id: String, supply: String) {
        RequestHelper.builder(EndPoints.EDIT_PRODUCTS)
            .listener(editProductTypeCallBack)
            .addParam("productId", id)
            .addParam("supply", supply)
            .put()
    }

    private val addProductTypeCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {

                        } else {
                            GeneralDialog()
                                .message(message)
                                .secondButton("باشه") { GeneralDialog().dismiss() }
                                .show()
                        }

                    } catch (e: JSONException) {
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .secondButton("باشه") { GeneralDialog().dismiss() }
                            .show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .secondButton("باشه") { GeneralDialog().dismiss() }
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }

    private fun dismiss() {
        try {
            dialog.dismiss()
            KeyBoardHelper.hideKeyboard()
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "ProductDialog class, dismiss method")
        }
    }
}