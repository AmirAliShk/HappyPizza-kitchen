package ir.food.kitchenAndroid.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.activity.MainActivity
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.FragmentLoginBinding
import ir.food.kitchenAndroid.helper.FragmentHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject

class LogInFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.activity?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window?.statusBarColor = this.resources.getColor(R.color.white)
            window?.navigationBarColor = this.resources.getColor(R.color.white)
        }
        TypefaceUtil.overrideFonts(binding.root)

        return binding.root
    }

    private fun login() {
//        binding.vfLogIn.displayedChild = 1
        RequestHelper.builder(EndPoints.LOG_IN)
//            .addParam("mobileOrEmail", binding.edtMobileOrEmail.text.toString())
//            .addParam("password", binding.edtPassword.text.toString())
            .listener(loginCallBack)
            .post()
    }

    private val loginCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
//                        binding.vfLogIn.displayedChild = 0
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
                            MyApplication.currentActivity.startActivity(
                                Intent(
                                    MyApplication.currentActivity,
                                    MainActivity::class.java
                                )
                            )
                            MyApplication.currentActivity.finish()
                        } else {
//                            GeneralDialog()
//                                .message(message)
//                                .firstButton("باشه") { GeneralDialog().dismiss() }
//                                .secondButton("تلاش مجدد") { login() }
//                                .show()
                        }
                    } catch (e: JSONException) {
//                        binding.vfLogIn.displayedChild = 0
//                        GeneralDialog()
//                            .message("خطایی پیش آمده دوباره امتحان کنید.")
//                            .firstButton("باشه") { GeneralDialog().dismiss() }
//                            .secondButton("تلاش مجدد") { login() }
//                            .show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
//                    binding.vfLogIn.displayedChild = 0
//                    GeneralDialog()
//                        .message("خطایی پیش آمده دوباره امتحان کنید.")
//                        .firstButton("باشه") { GeneralDialog().dismiss() }
//                        .secondButton("تلاش مجدد") { login() }
//                        .show()
                }
                super.onFailure(reCall, e)
            }
        }
}