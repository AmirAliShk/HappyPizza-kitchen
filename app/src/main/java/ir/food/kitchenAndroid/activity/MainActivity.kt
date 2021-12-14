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
import ir.food.kitchenAndroid.fragment.GetProductsFragment
import ir.food.kitchenAndroid.fragment.NotReadyOrdersFragment
import ir.food.kitchenAndroid.fragment.OrdersHistoryFragment
import ir.food.kitchenAndroid.fragment.ReadyOrdersFragment
import ir.food.kitchenAndroid.helper.FragmentHelper
import ir.food.kitchenAndroid.helper.KeyBoardHelper
import ir.food.kitchenAndroid.helper.TypefaceUtil

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

        binding.btnNotReady.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, NotReadyOrdersFragment())
                .add()
        }

        binding.btnProductsList.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, GetProductsFragment())
                .add()
        }

        binding.imgLogout.setOnClickListener {
            GeneralDialog().message("ایا از خروج از حساب کاربری خود اطمینان دارید؟")
                .firstButton("بله") {
                    MyApplication.prefManager.authorization = ""
                    MyApplication.currentActivity.startActivity(
                        Intent(
                            MyApplication.currentActivity,
                            Splash::class.java
                        )
                    )
                    MyApplication.currentActivity.finish()
                }.secondButton("خیر") {}
                .show()
        }
        binding.btnReady.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, ReadyOrdersFragment())
                .add()
        }

        binding.btnHistory.setOnClickListener {
            FragmentHelper
                .toFragment(MyApplication.currentActivity, OrdersHistoryFragment())
                .add()
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