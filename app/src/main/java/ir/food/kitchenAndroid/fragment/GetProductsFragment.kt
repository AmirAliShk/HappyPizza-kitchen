package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.adapter.ProductsAdapter
import ir.food.kitchenAndroid.adapter.ReadyOrdersAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentGetProductsBinding
import ir.food.kitchenAndroid.databinding.FragmentReadyOrdersBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.dialog.ProductDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.OrderHistoryModel
import ir.food.kitchenAndroid.model.ProductModel
import ir.food.kitchenAndroid.model.ReadyOrdersModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class GetProductsFragment : Fragment() {
    lateinit var binding: FragmentGetProductsBinding
    private lateinit var response: String
    private lateinit var responsePType: String
    private val KEY_LAST_DATA = "lastData"
    private val KEY_LAST_DATA_PTYPE = "lastDatePType"
    var productModels: ArrayList<ProductModel> = ArrayList()
    var adapter: ProductsAdapter =
        ProductsAdapter(productModels, object : ProductsAdapter.ProductAdapterInterface {
            override fun dismissListener(b: Boolean) {
                if (b) {
                    productModels.clear()
                    getProducts()

                }
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGetProductsBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)
        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        if (savedInstanceState == null) {
            getProducts()
            getProductsType()
        } else {
            response = savedInstanceState.getString(KEY_LAST_DATA).toString()
            responsePType = savedInstanceState.getString(KEY_LAST_DATA_PTYPE).toString()
            parseDate(response)
        }

        return binding.root
    }

    private fun getProductsType() {
        RequestHelper.builder(EndPoints.GET_PRODUCTS_TYPE)
            .listener(productsTypeCallBack)
            .get()
    }

    private val productsTypeCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    parseTypeData(args[0].toString())
                } catch (e: JSONException) {
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getProductsType() }
                        .show()
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getProductsType() }
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }

    private fun getProducts() {
        binding.vfProducts.displayedChild = 0
        RequestHelper.builder(EndPoints.GET_PRODUCTS)
            .listener(productsCallBack)
            .get()
    }

    private val productsCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    binding.vfProducts.displayedChild = 1
                    parseDate(args[0].toString())

                } catch (e: JSONException) {
                    binding.vfProducts.displayedChild = 3
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getProducts() }
                        .show()
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfProducts.displayedChild = 3
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getProducts() }
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_LAST_DATA, response)
        outState.putString(KEY_LAST_DATA_PTYPE, responsePType)
    }

    private fun parseDate(result: String) {
        response = result
        val response = JSONObject(result)

        val success = response.getBoolean("success")
        val message = response.getString("message")

        if (success) {
            val dataObject = response.getJSONArray("data")

            for (i in 0 until dataObject.length()) {
                val objProduct: JSONObject = dataObject.getJSONObject(i)
                val model = ProductModel(
                    objProduct.getString("_id"),
                    objProduct.getString("name"),
                    objProduct.getString("description"),
                    objProduct.getInt("supply"),
                    objProduct.getString("updatedAt"),
                    objProduct.getString("typeName"),
                    objProduct.getString("typeId"),

                    )
                productModels.add(model)
            }
            if (productModels.size == 0) {
                binding.vfProducts.displayedChild = 2
            } else {
                binding.vfProducts.displayedChild = 1
            }
            binding.listProducts.adapter = adapter
        } else {
            GeneralDialog()
                .message(message)
                .firstButton("باشه") { GeneralDialog().dismiss() }
                .secondButton("تلاش مجدد") { getProducts() }
                .show()
            binding.vfProducts.displayedChild = 3
        }
    }

    private fun parseTypeData(result: String) {
        responsePType = result
        val response = JSONObject(result)
        val success = response.getBoolean("success")
        val message = response.getString("message")
        if (success) {
            val data = response.getJSONArray("data")
            MyApplication.prefManager.productList = data.toString()
        } else {
            GeneralDialog()
                .message(message)
                .firstButton("باشه") { GeneralDialog().dismiss() }
                .secondButton("تلاش مجدد") { getProductsType() }
                .show()
        }
    }
}