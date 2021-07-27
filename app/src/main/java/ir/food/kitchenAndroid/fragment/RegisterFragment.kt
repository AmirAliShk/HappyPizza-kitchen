package ir.food.kitchenAndroid.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentRegisterBinding
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONObject
import java.lang.Exception

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.darkGray)
        }

        TypefaceUtil.overrideFonts(binding.root)



        return binding.root
    }

    private fun register() {
//        if (binding.vfOrders != null) {
//            binding.vfOrders.displayedChild = 0
//        }

        RequestHelper.builder(EndPoints.REGISTER)
            .addParam("password", "pass")
            .addParam("family", "family")
            .addParam("mobile", "mobile")
            .addParam("code", "code")
            .addParam("scope", "cook")
            .listener(registerCallBack)
            .get()
    }

    private val registerCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {

            override fun onResponse(reCall: Runnable?, vararg args: Any?) {

                val response = JSONObject(args[0].toString())
                val success = response.getBoolean("success")
                val message = response.getString("message")

            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                super.onFailure(reCall, e)
            }

        }
}