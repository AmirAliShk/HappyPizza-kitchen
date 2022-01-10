package ir.food.kitchenAndroid.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.databinding.ActivityMainBinding
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.fragment.*
import ir.food.kitchenAndroid.helper.FragmentHelper
import ir.food.kitchenAndroid.helper.KeyBoardHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil
import ir.food.kitchenAndroid.push.AvaCrashReporter

class MainActivity : AppCompatActivity() {
    var TAG = MainActivity::class.java
    private lateinit var binding: ActivityMainBinding
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
        binding.txtAccName?.text = MyApplication.prefManager.userName
        binding.btnNotReady.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, NotReadyOrdersFragment())
                .add()
        }

        binding.btnReady.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, ReadyOrdersFragment())
                .add()
        }

        binding.btnHistory.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, AllOrdersFragment())
                .add()
        }

        binding.btnProductsList.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, GetProductsFragment())
                .add()
        }

        binding.btnSending?.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, SendingFragment())
                .add()
        }

        binding.btnCheckoutDeli?.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, CheckoutDeliFragment())
                .add()
        }

        binding.imgLogout.setOnClickListener {
            GeneralDialog().message("ایا از خروج از حساب کاربری خود اطمینان دارید؟")
                .firstButton("بله") {
                    MyApplication.currentActivity.finish()
                    MyApplication.prefManager.cleanPrefManger()
                    MyApplication.currentActivity.startActivity(
                        Intent(
                            MyApplication.currentActivity,
                            Splash::class.java
                        )
                    )
                }
                .secondButton("خیر") {}
                .show()
        }
    }

    override fun onBackPressed() {
        try {
            KeyBoardHelper.hideKeyboard()
            if (fragmentManager.backStackEntryCount > 0 || supportFragmentManager.backStackEntryCount > 0) {
                super.onBackPressed()
            } else {
                if (doubleBackToExitPressedOnce) {
                    finish()
                } else {
                    doubleBackToExitPressedOnce = true
                    MyApplication.Toast(
                        getString(R.string.txt_please_for_exit_reenter_back),
                        Toast.LENGTH_SHORT
                    )
                    Handler().postDelayed({
                        doubleBackToExitPressedOnce = false
                    }, 1500)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "$TAG class, onBackPressed method")
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