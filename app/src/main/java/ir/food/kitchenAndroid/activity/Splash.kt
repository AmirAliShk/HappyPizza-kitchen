package ir.food.kitchenAndroid.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.app.MyApplication.context
import ir.food.kitchenAndroid.databinding.ActivitySplashBinding
import ir.food.kitchenAndroid.fragment.LogInFragment
import ir.food.kitchenAndroid.helper.AppVersionHelper
import ir.food.kitchenAndroid.helper.FragmentHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.okHttp.RequestHelper
import org.json.JSONException
import org.json.JSONObject

class Splash : AppCompatActivity() {
    var TAG = Splash::class.java
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        TypefaceUtil.overrideFonts(binding.root)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.white)
            window.navigationBarColor = this.resources.getColor(R.color.white)
        }

//        try {
//            if (MyApplication.prefManager.idToken.equals("")) {
//                FragmentHelper
//                    .toFragment(this, LogInFragment())
//                    .setAddToBackStack(false)
//                    .add()
//            } else {
//                appInfo()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace();
//        }

    }

    private fun appInfo() {
        RequestHelper.builder(EndPoints.APP_INFO)
            .addParam("versionCode", AppVersionHelper(context).verionCode)
            .addParam("os", "Android")
            .listener(appInfoCallBack)
            .post()
    }

    private val appInfoCallBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        val response = JSONObject(args[0].toString())
                        val success = response.getBoolean("success")
                        val message = response.getString("message")
                        if (success) {
//                            ContinueProcessing.runMainActivity()
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
//                                .secondButton("تلاش مجدد") { appInfo() }
//                                .show()
                        }
                    } catch (e: JSONException) {
//                        GeneralDialog()
//                            .message("خطایی پیش آمده دوباره امتحان کنید.")
//                            .firstButton("باشه") { GeneralDialog().dismiss() }
//                            .secondButton("تلاش مجدد") { appInfo() }
//                            .show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
//                    GeneralDialog()
//                        .message("خطایی پیش آمده دوباره امتحان کنید.")
//                        .firstButton("باشه") { GeneralDialog().dismiss() }
//                        .secondButton("تلاش مجدد") { appInfo() }
//                        .show()
                }
                super.onFailure(reCall, e)
            }
        }

    override fun onResume() {
        super.onResume()
        MyApplication.currentActivity = this
    }

    override fun onStart() {
        super.onStart()
        MyApplication.currentActivity = this
    }
}