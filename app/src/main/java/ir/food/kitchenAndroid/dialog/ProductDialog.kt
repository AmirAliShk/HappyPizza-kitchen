package ir.food.kitchenAndroid.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.adapter.SpinnerAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.DialogProductsBinding
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ProductModel
import ir.food.kitchenAndroid.model.ProductsTypeModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ProductDialog {
    lateinit var dialog: Dialog
    lateinit var binding: DialogProductsBinding
    var typesModels: ArrayList<ProductsTypeModel> = ArrayList()
    var productTypes: String = ""
    lateinit var spinner: Spinner

    fun show(productModel: ProductModel, type: Int) {
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
        spinner = binding.spType
        initProductTypeSpinner()
        binding.imgClose.setOnClickListener { dismiss() }

        MyApplication.handler.postDelayed({
            if (type == 1) { // 1 mean edit
                binding.spType.setSelection(getIndex(binding.spType, productModel.typeName))
                binding.edtPName.setText(productModel.name)
                binding.edtQuantity.setText(productModel.supply.toString())
                binding.edtDesc.setText(productModel.description)
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
                        editProductType(
                            productModel.id,
                            binding.edtQuantity.text.trim().toString(),
                            binding.edtPName.text.toString(),
                            binding.edtDesc.text.toString(),
                            productTypes
                        )
                    } else {
                        addProductType(
                            productModel.id,
                            binding.edtQuantity.text.trim().toString(),
                            binding.edtPName.text.toString(),
                            binding.edtDesc.text.toString(),
                            productTypes
                        )
                    }
                }
                .secondButton("خیر") { dismiss() }
                .show()
        }

        dialog.show()

    }

    private fun editProductType(
        id: String,
        supply: String,
        name: String,
        description: String,
        type: String
    ) {
        RequestHelper.builder(EndPoints.EDIT_PRODUCTS)
            .listener(editProductTypeCallBack)
            .addParam("productId", id)
            .addParam("supply", supply)
            .addParam("name", name)
            .addParam("description", description)
            .addParam("type", type)
            .put()
    }

    private val editProductTypeCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        dismiss()
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") {
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

    private fun addProductType(
        id: String,
        supply: String,
        name: String,
        description: String,
        type: String
    ) {
        RequestHelper.builder(EndPoints.EDIT_PRODUCTS)
            .listener(editProductTypeCallBack)
            .addParam("productId", id)
            .addParam("supply", supply)
            .addParam("name", name)
            .addParam("description", description)
            .addParam("type", type)
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

    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    private fun initProductTypeSpinner() {
        val typesList = ArrayList<String>()
        try {
            typesList.add(0, "نوع محصول")
            val typesArr = JSONArray(MyApplication.prefManager.productList)
            for (i in 0 until typesArr.length()) {
                val types = ProductsTypeModel(
                    typesArr.getJSONObject(i).getString("_id"),
                    typesArr.getJSONObject(i).getString("name")
                )
                typesModels.add(types)

                typesList.add(i + 1, typesArr.getJSONObject(i).getString("name"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (spinner == null) return

        try {
            spinner.adapter =
                SpinnerAdapter(MyApplication.context, R.layout.item_spinner, typesList)
            spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            productTypes = ""
                            return
                        }
                        productTypes = typesModels[position - 1].id
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun dismiss() {
        try {
            dialog.dismiss()
        } catch (e: Exception) {
        }
    }


}