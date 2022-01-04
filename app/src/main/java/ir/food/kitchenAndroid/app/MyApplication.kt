package ir.food.kitchenAndroid.app

import org.acra.annotation.AcraHttpSender
import org.acra.sender.HttpSender
import android.app.Application
import org.acra.ACRA
import android.graphics.Typeface
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import ir.food.kitchenAndroid.R
import ir.food.kitchenAndroid.helper.TypefaceUtil
import android.widget.TextView
import android.widget.Toast
import android.view.Gravity
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.util.*

@AcraHttpSender(
    uri = "http://turbotaxi.ir:6061/api/crashReport",
    httpMethod = HttpSender.Method.POST
)
class MyApplication : Application() {

    companion object {
        lateinit var context: Context
        lateinit var currentActivity: Activity
        lateinit var handler: Handler
        lateinit var prefManager: PrefManager
        val IRANSANS = "fonts/IRANSans.otf"
        val IRANSANS_BOLD = "fonts/IRANSANSMOBILE_BOLD.TTF"
        val IRANSANS_MEDUME = "fonts/IRANSANSMOBILE_MEDIUM.TTF"
        val IRANSANS_LIGHT = "fonts/IRANSANSMOBILE_LIGHT.TTF"
        val SOUND = "android.resource://ir.food.kitchenAndroid/"
        lateinit var iranSance: Typeface
        lateinit var IraSanSMedume: Typeface
        lateinit var IraSanSBold: Typeface
        lateinit var IraSanSLight: Typeface

        fun showSnackBar(text: String) {
            val coordinatorLayout = currentActivity.findViewById(android.R.id.content) as View

            val snackBar =
                Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).setAction("مشاهده") {

                }
            snackBar.setActionTextColor(Color.WHITE)
            val snackBarView = snackBar.view
            snackBarView.setBackgroundColor(currentActivity.resources.getColor(R.color.purple_700)) // ToDO change color
            val textView =
                snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.setTextColor(Color.WHITE)
            textView.text = text
            textView.gravity = Gravity.RIGHT
            textView.textSize = 20f
            TypefaceUtil.overrideFonts(snackBarView)
            snackBar.show()
        }

        fun Toast(message: String?, duration: Int) {
            handler.post {
                val layoutInflater = LayoutInflater.from(currentActivity)
                val v = layoutInflater.inflate(R.layout.item_toast, null)
                TypefaceUtil.overrideFonts(v)
                val text = v.findViewById<View>(R.id.text) as TextView
                text.text = message
                val toast = Toast(currentActivity)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.duration = duration
                toast.view = v
                toast.show()
            }
        }

        fun avaStart() {
//            if (prefManager.getAvaPID() == 0) return
//            if (prefManager.getAvaToken() == null) return
//            AvaFactory.getInstance(context)
//                .setUserID(prefManager.getDriverId().toString())
//                .setProjectID(prefManager.getAvaPID())
//                .setToken(prefManager.getAvaToken())
//                .setAddress(EndPoint.PUSH_ADDRESS)
//                .start();
        }

    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        handler = Handler()
        initTypeface()

        prefManager = PrefManager(context)

        val languageToLoad = "fa_IR"
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        avaStart()
        initACRA()

    }

    private fun initACRA() {
//        Map<String, String> authHeaderMap = new HashMap<>();
//        authHeaderMap.put("Authorization", MyApplication.prefManager.getAuthorization());
//        authHeaderMap.put("id_token", MyApplication.prefManager.getIdToken());
//
//        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this)
//                .setBuildConfigClass(BuildConfig.class)
//                .setReportFormat(StringFormat.JSON);
//
//        HttpSenderConfigurationBuilder httpPluginConfigBuilder
//                = builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder.class)
//                .setUri(EndPoints.ACRA_PATH)
//                .setHttpMethod(HttpSender.Method.POST)
//                .setHttpHeaders(authHeaderMap)
//                .setEnabled(true);
//        if (!BuildConfig.DEBUG)
        ACRA.init(this)
    }

    private fun initTypeface() {
        iranSance = Typeface.createFromAsset(assets, IRANSANS)
        IraSanSMedume = Typeface.createFromAsset(assets, IRANSANS_MEDUME)
        IraSanSLight = Typeface.createFromAsset(assets, IRANSANS_LIGHT)
        IraSanSBold = Typeface.createFromAsset(assets, IRANSANS_BOLD)
    }
}

