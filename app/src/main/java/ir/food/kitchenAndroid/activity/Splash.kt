package ir.food.kitchenAndroid.activity

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ActivitySplashBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.helper.KeyBoardHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.webServices.GetAppInfo
import org.acra.ACRA

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
            window?.statusBarColor = ContextCompat.getColor(MyApplication.context, R.color.darkGray)
            window?.navigationBarColor =
                ContextCompat.getColor(MyApplication.context, R.color.background)
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        ACRA.getErrorReporter().putCustomData(
            "LineCode",
            MyApplication.prefManager.userCode.toString()
        )
        ACRA.getErrorReporter()
            .putCustomData("projectId", MyApplication.prefManager.pushId.toString())
        MyApplication.handler.postDelayed(
            GetAppInfo()::callAppInfoAPI, 500
        )
    }

    override fun onBackPressed() {
        GeneralDialog()
            .message("آیا از خروج خود اطمینان دارید؟")
            .firstButton("بله") {
                finish()
            }
            .secondButton("خیر") {}
            .show()
    }

    override fun onPause() {
        super.onPause()
        KeyBoardHelper.hideKeyboard()
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