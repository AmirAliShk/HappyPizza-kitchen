package ir.food.kitchenAndroid.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.activity.MainActivity
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentRegisterBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
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
        TypefaceUtil.overrideFonts(binding.root)

        binding.btnRegister.setOnClickListener {
//            MyApplication.currentActivity.startActivity(
//                Intent(
//                    MyApplication.currentActivity,
//                    MainActivity::class.java
//                )
//            )
//            MyApplication.currentActivity.finish()
            register()
        }

        binding.txtLogin.setOnClickListener {
//            FragmentHelper
//                .toFragment(MyApplication.currentActivity, VerificationFragment())
//                .setStatusBarColor(MyApplication.currentActivity.resources.getColor(R.color.black))
//                .add()
            register()
        }

        binding.btnSendCode.setOnClickListener { sendCode() }

        return binding.root
    }


    private fun sendCode() {
        binding.vfSendCode.displayedChild = 1
        RequestHelper.builder(EndPoints.REGISTER_CODE)
            .addParam("mobile", binding.edtMobile.text.toString())
            .listener(sendCodeCallBack)
            .post()
    }

    private val sendCodeCallBack: RequestHelper.Callback = object : RequestHelper.Callback() {
        override fun onResponse(reCall: Runnable?, vararg args: Any?) {
            MyApplication.handler.post {
                try {
                    binding.vfSendCode.displayedChild = 0

                    val response = JSONObject(args[0].toString())
                    val success = response.getBoolean("success")
                    val message = response.getString("message")

                    if (success) {
//                 "success": true, "message": "کد تاییدیه به شماره موبایل داده شده ، با موفقیت فرستاده شد"

                        binding.edtVerificationCode.isEnabled = true
                        binding.btnRegister.isEnabled = true
//                        binding.vfSendCode.visibility = View.GONE
                    } else {
                        binding.vfSendCode.displayedChild = 0
                        GeneralDialog()
                            .message(message)
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { sendCode() }
                            .show()
                    }

                } catch (e: JSONException) {
                    binding.vfSendCode.displayedChild = 0
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { sendCode() }
                        .show()
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: Exception?) {
            MyApplication.handler.post {
                binding.vfSendCode.displayedChild = 0
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { sendCode() }
                    .show()
            }
            super.onFailure(reCall, e)
        }
    }

    private fun register() {
        binding.vfSignUp.displayedChild = 1
        RequestHelper.builder(EndPoints.REGISTER)
            .addParam("password", binding.edtPassword.text.toString())
            .addParam("family", binding.edtName.text.toString())
            .addParam("mobile", binding.edtMobile.text.toString())
            .addParam("code", binding.edtVerificationCode.text.toString())
            .addParam("scope", "cook")
            .listener(registerCallBack)
            .post()
    }

    private val registerCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        binding.vfSignUp.displayedChild = 0
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {
                            val data = response.getJSONObject("data")
                            val status = data.getBoolean("status")
                            if (status) {
                                MyApplication.prefManager.idToken = data.getString("idToken")
                                MyApplication.prefManager.authorization =
                                    data.getString("accessToken")
                                MyApplication.currentActivity.startActivity(
                                    Intent(
                                        MyApplication.currentActivity,
                                        MainActivity::class.java
                                    )
                                )
                                MyApplication.currentActivity.finish()
                            }
                        } else {
                            binding.vfSignUp.displayedChild = 0
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { register() }
                                .show()
                        }
                    } catch (e: JSONException) {
                        binding.vfSignUp.displayedChild = 0
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { register() }
                            .show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { register() }
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }
}