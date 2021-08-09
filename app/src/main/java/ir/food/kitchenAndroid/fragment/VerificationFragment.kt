package ir.food.kitchenAndroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentVerificationBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.FragmentHelper
import ir.food.kitchenAndroid.helper.KeyBoardHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.webServices.GetAppInfo
import org.json.JSONException
import org.json.JSONObject

class VerificationFragment : Fragment() {

    lateinit var binding: FragmentVerificationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentVerificationBinding.inflate(layoutInflater)
        TypefaceUtil.overrideFonts(binding.root)

        binding.edtMobile.requestFocus()
        MyApplication.handler.postDelayed(
            { KeyBoardHelper.showKeyboard(MyApplication.context) },
            400
        )

        binding.btnSendCode.setOnClickListener {
            if (binding.edtMobile.text.toString()
                    .isEmpty() || binding.edtMobile.text.toString().length != 11
            ) {
                MyApplication.Toast("لطفا شماره موبایل خود را وارد کنید.", Toast.LENGTH_SHORT)
            } else {
                sendCode()
            }
        }

        binding.btnLogin.setOnClickListener {
            if (binding.edtMobile.toString()
                    .isEmpty() || binding.edtCode.toString()
                    .isEmpty() || binding.edtMobile.text.toString().length != 11 || binding.edtCode.text.toString().length != 4
            ) {
                MyApplication.Toast("لطفا تمام موارد را کامل کنید", Toast.LENGTH_SHORT)
            } else {
                login()
            }
        }

        binding.txtRegister.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, RegisterFragment())
                .add()
        }

        return binding.root
    }

    private fun sendCode() {
        binding.vfSendCode.displayedChild = 1
        RequestHelper.builder(EndPoints.LOGIN_CODE)
            .addParam("mobile", binding.edtMobile.text.toString())
            .addParam("scope", "cook")
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
                        .secondButton("تلاش مجدد") { login() }
                        .show()
                    e.printStackTrace()
                }
            }
        }

        override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
            MyApplication.handler.post {
                binding.vfSendCode.displayedChild = 0
                GeneralDialog()
                    .message("خطایی پیش آمده دوباره امتحان کنید.")
                    .firstButton("باشه") { GeneralDialog().dismiss() }
                    .secondButton("تلاش مجدد") { login() }
                    .show()
            }
            super.onFailure(reCall, e)
        }

    }

    private fun login() {
        binding.vfLogin.displayedChild = 1
        RequestHelper.builder(EndPoints.LOG_IN)
            .addParam("mobile", binding.edtMobile.text.toString())
            .addParam("code", binding.edtCode.text.toString())
            .addParam("scope", "cook")
            .listener(loginCallBack)
            .post()
    }

    private val loginCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        binding.vfLogin.displayedChild = 0
//{"success":true,"message":"کاربر با موفقیت وارد شد","data":{"idToken":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjBkOWJlNGY4ZTJiN2QyOTdjMmU0NjUwIiwidXNlcl9hY3RpdmUiOnRydWUsInVzZXJfZW1wbG95ZXIiOiI2MGQ5YmU0ZjhlMmI3ZDI5N2MyZTQ2NTAiLCJpYXQiOjE2MjQ4ODMwMTAsImV4cCI6MTY0NjQ4MzAxMCwiYXVkIjoiYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIifQ.LmSGVrGdlArOdfpwMQGF9f7e4xgs44bjZ9ZdBXF_8iU","accessToken":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6InVzZXIiLCJpYXQiOjE2MjQ4ODMwMTAsImV4cCI6MTY1MDgwMzAxMCwiYXVkIjoiYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIifQ.SRgJvlVA_fggm6KX2D45v_S7Z1tW7h8g3uT4hEfiohw"}}
//                      {"success":false,"message":"کاربر در دسترس نمی باشد","data":{}}
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        val dataObject = response.getJSONObject("data")
                        if (success) {
                            MyApplication.prefManager.idToken = dataObject.getString("idToken")
                            MyApplication.prefManager.authorization =
                                dataObject.getString("accessToken")
                            GetAppInfo().callAppInfoAPI()
                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { login() }
                                .show()
                        }
                    } catch (e: JSONException) {
                        binding.vfLogin.displayedChild = 0
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { login() }
                            .show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
                    binding.vfLogin.displayedChild = 0
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { login() }
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }
}