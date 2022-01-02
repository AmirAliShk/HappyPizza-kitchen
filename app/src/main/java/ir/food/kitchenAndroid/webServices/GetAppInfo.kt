package ir.food.kitchenAndroid.webServices

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import ir.food.kitchenAndroid.activity.MainActivity
import ir.food.kitchenAndroid.app.EndPoints
import ir.food.kitchenAndroid.app.MyApplication
import ir.food.kitchenAndroid.dialog.GeneralDialog
import ir.food.kitchenAndroid.fragment.VerificationFragment
import ir.food.kitchenAndroid.helper.AppVersionHelper
import ir.food.kitchenAndroid.helper.FragmentHelper
import ir.food.kitchenAndroid.helper.ScreenHelper
import ir.food.kitchenAndroid.okHttp.RequestHelper
import ir.food.kitchenAndroid.push.AvaCrashReporter
import org.json.JSONException
import org.json.JSONObject

class GetAppInfo {

    @SuppressLint("HardwareIds")
    fun callAppInfoAPI() {
        try {
            if (MyApplication.prefManager.authorization == "") {
                FragmentHelper
                    .toFragment(MyApplication.currentActivity, VerificationFragment())
                    .setAddToBackStack(false)
                    .add()
            } else {
                val android_id = Settings.Secure.getString(
                    MyApplication.currentActivity.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                val deviceInfo: JSONObject? = null
                deviceInfo?.put("MODEL", Build.MODEL);
                deviceInfo?.put("HARDWARE", Build.HARDWARE);
                deviceInfo?.put("BRAND", Build.BRAND);
                deviceInfo?.put("DISPLAY", Build.DISPLAY);
                deviceInfo?.put("BOARD", Build.BOARD);
                deviceInfo?.put("SDK_INT", Build.VERSION.SDK_INT);
                deviceInfo?.put("BOOTLOADER", Build.BOOTLOADER);
                deviceInfo?.put("DEVICE", Build.DEVICE);
                deviceInfo?.put(
                    "DISPLAY_HEIGHT",
                    ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).height
                )
                deviceInfo?.put(
                    "DISPLAY_WIDTH",
                    ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).width
                )
                deviceInfo?.put(
                    "DISPLAY_SIZE",
                    ScreenHelper.getScreenSize(MyApplication.currentActivity)
                )
                deviceInfo?.put("ANDROID_ID", android_id)

                RequestHelper.builder(EndPoints.APP_INFO)
                    .addParam("versionCode", AppVersionHelper(MyApplication.context).versionCode)
                    .addParam("os", "Android")
                    .addParam("deviceInfo", deviceInfo)
                    .listener(appInfoCallBack)
                    .post()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            AvaCrashReporter.send(e, "GetAppInfo class, callAppInfoAPI method")
        }
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
                            val data: JSONObject = response.getJSONObject("data")
                            val statusMessage = data.getString("statusMessage")

                            if (data.getInt("userStatus") == 0) { // it means every thing is ok

                                MyApplication.prefManager.pushId = data.getInt("pushId")
                                MyApplication.prefManager.userCode = data.getString("userId")
                                MyApplication.prefManager.pushToken = data.getString("pushToken")
                                val updateAvailable = data.getBoolean("update")
                                val forceUpdate = data.getBoolean("isForce")
                                val updateUrl = data.getString("updateUrl")

                                if (updateAvailable) {
                                    updatePart(forceUpdate, updateUrl)
                                    return@post
                                }
                                MyApplication.currentActivity.startActivity(
                                    Intent(
                                        MyApplication.currentActivity,
                                        MainActivity::class.java
                                    )
                                )
                                MyApplication.currentActivity.finish()

                            } else if (data.getInt("userStatus") == 1 || data.getInt("userStatus") == 4) { // 1 = means use deleted so we logout..., and 4 = means the job changed.
                                GeneralDialog()
                                    .message(statusMessage)
                                    .secondButton("بستن") {
                                        MyApplication.prefManager.cleanPrefManger()
                                        MyApplication.currentActivity.finish()
                                    }
                                    .show()
                            } else {
                                GeneralDialog()
                                    .message(statusMessage)
                                    .secondButton("بستن") { MyApplication.currentActivity.finish() }
                                    .show()
                            }

                        } else {
                            GeneralDialog()
                                .message(message)
                                .firstButton("باشه") { GeneralDialog().dismiss() }
                                .secondButton("تلاش مجدد") { callAppInfoAPI() }
                                .show()
                        }
                    } catch (e: JSONException) {
                        GeneralDialog()
                            .message("خطایی پیش آمده دوباره امتحان کنید.")
                            .firstButton("باشه") { GeneralDialog().dismiss() }
                            .secondButton("تلاش مجدد") { callAppInfoAPI() }
                            .show()
                        e.printStackTrace()
                        AvaCrashReporter.send(e, "GetAppInfo class, appInfoCallBack")
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: Exception?) {
                MyApplication.handler.post {
                    GeneralDialog()
                        .message("خطایی پیش آمده دوباره امتحان کنید.")
                        .firstButton("باشه") { GeneralDialog().dismiss() }
                        .secondButton("تلاش مجدد") { callAppInfoAPI() }
                        .show()
                }
                super.onFailure(reCall, e)
            }
        }

    private fun updatePart(isForce: Boolean, url: String) {
        val generalDialog = GeneralDialog()
        if (isForce) {
            generalDialog.title("به روز رسانی")
            generalDialog.cancelable(false)
            generalDialog.message("برای برنامه نسخه جدیدی موجود است لطفا برنامه را به روز رسانی کنید")
            generalDialog.firstButton("به روز رسانی") {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                MyApplication.currentActivity.startActivity(i)
                MyApplication.currentActivity.finish()
            }
            generalDialog.secondButton("بستن برنامه") { MyApplication.currentActivity.finish() }
            generalDialog.show()
        } else {
            generalDialog.title("به روز رسانی")
            generalDialog.cancelable(false)
            generalDialog.message("برای برنامه نسخه جدیدی موجود است در صورت تمایل میتوانید برنامه را به روز رسانی کنید")
            generalDialog.firstButton("به روز رسانی") {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                MyApplication.currentActivity.startActivity(i)
                MyApplication.currentActivity.finish()
            }
            generalDialog.secondButton("فعلا نه") {
                MyApplication.currentActivity.startActivity(
                    Intent(
                        MyApplication.currentActivity,
                        MainActivity::class.java
                    )
                )
                MyApplication.currentActivity.finish()
            }
            generalDialog.show()
        }
    }
}