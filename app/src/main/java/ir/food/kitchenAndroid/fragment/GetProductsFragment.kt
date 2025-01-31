package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.adapter.ProductsAdapter
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentGetProductsBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.model.ProductModel
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class GetProductsFragment : Fragment() {
    lateinit var binding: FragmentGetProductsBinding
    private lateinit var response: String
    private lateinit var responsePType: String
    lateinit var productModels: ArrayList<ProductModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGetProductsBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)
        binding.imgBack.setOnClickListener {
            MyApplication.currentActivity.onBackPressed()
        }

        callList()

        binding.llRefresh.setOnClickListener {
            callList()
        }

        return binding.root
    }

    private fun callList() {
        binding.imgRefreshActionBar.startAnimation(
            AnimationUtils.loadAnimation(
                MyApplication.context,
                R.anim.rotate
            )
        )
        getProducts()
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
                        .firstButton("بستن") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getProductsType() }
                        .cancelable(false)
                        .show()
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "GetProductsFragment class, productsTypeCallBack")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("بستن") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getProductsType() }
                    .cancelable(false)
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
                binding.imgRefreshActionBar.clearAnimation()
                try {
                    binding.vfProducts.displayedChild = 1
                    parseProducts(args[0].toString())
                } catch (e: JSONException) {
                    binding.vfProducts.displayedChild = 3
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("بستن") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { getProducts() }
                        .cancelable(false)
                        .show()
                    e.printStackTrace()
                    AvaCrashReporter.send(e, "GetProductsFragment class, productsCallBack")
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.imgRefreshActionBar.clearAnimation()
                binding.vfProducts.displayedChild = 3
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("بستن") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { getProducts() }
                    .cancelable(false)
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }

    private fun parseProducts(result: String) {
        try {
            productModels = ArrayList()
            var adapter = ProductsAdapter(productModels, object : ProductsAdapter.ProductAdapterInterface { override fun dismissListener(b: Boolean) {} })
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
                        objProduct.getString("typeId")
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

        } catch (e: Exception) {
            binding.imgRefreshActionBar.clearAnimation()
            e.printStackTrace()
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
                .firstButton("بستن") { GeneralDialog().dismiss() }
                .secondButton("تلاش مجدد") { getProducts() }
                .cancelable(false)
                .show()
        }
    }
}