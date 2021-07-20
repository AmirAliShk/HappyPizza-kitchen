package ir.food.kitchenAndroid.webServices;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;

import ir.food.kitchenAndroid.R;
import ir.food.kitchenAndroid.app.EndPoints;
import ir.food.kitchenAndroid.app.MyApplication;
import ir.food.kitchenAndroid.dialog.GeneralDialog;
import ir.food.kitchenAndroid.fragment.LogInFragment;
import ir.food.kitchenAndroid.helper.AppVersionHelper;
import ir.food.kitchenAndroid.helper.FragmentHelper;
import ir.food.kitchenAndroid.okHttp.RequestHelper;

class GetAppIno {

    public void callAppInfoAP() {
        try {
            if (MyApplication.prefManager.getRefreshToken().equals("")) {
                FragmentHelper
                        .toFragment(MyApplication.currentActivity, new LogInFragment())
                        .setStatusBarColor(MyApplication.currentActivity.getResources().getColor(R.color.black))
                        .setAddToBackStack(false)
                        .add();
            } else {
//                JSONObject deviceInfo = new JSONObject();
//                @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(MyApplication.currentActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
//                deviceInfo.put("MODEL", Build.MODEL);
//                deviceInfo.put("HARDWARE", Build.HARDWARE);
//                deviceInfo.put("BRAND", Build.BRAND);
//                deviceInfo.put("DISPLAY", Build.DISPLAY);
//                deviceInfo.put("BOARD", Build.BOARD);
//                deviceInfo.put("SDK_INT", Build.VERSION.SDK_INT);
//                deviceInfo.put("BOOTLOADER", Build.BOOTLOADER);
//                deviceInfo.put("DEVICE", Build.DEVICE);
//                deviceInfo.put("DISPLAY_HEIGHT", ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).getHeight());
//                deviceInfo.put("DISPLAY_WIDTH", ScreenHelper.getRealDeviceSizeInPixels(MyApplication.currentActivity).getWidth());
//                deviceInfo.put("DISPLAY_SIZE", ScreenHelper.getScreenSize(MyApplication.currentActivity));
//                deviceInfo.put("ANDROID_ID", android_id);

                RequestHelper.builder(EndPoints.APP_INFO)
                        .addParam("versionCode", new AppVersionHelper(MyApplication.context).getVersionCode())
                        .addParam("os", "Android")
                        .listener(appInfoCallBack)
                        .post();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    RequestHelper.Callback appInfoCallBack = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            MyApplication.handler.post(() -> {
                try {
                    JSONObject object = new JSONObject(args[0].toString());
                    int block = object.getInt("isBlock");
                    int accessDriverSupport = object.getInt("accessDriverSupport");
                    int updateAvailable = object.getInt("updateAvailable");
                    int forceUpdate = object.getInt("forceUpdate");
                    String updateUrl = object.getString("updateUrl");
                    int changePass = object.getInt("changePassword");
                    int countRequest = object.getInt("countRequest");
                    int sipNumber = object.getInt("sipNumber");
                    String sipServer = object.getString("sipServer");
                    String sipPassword = object.getString("sipPassword");
                    String sheba = object.getString("sheba");
                    int userId = object.getInt("userId");
                    String cardNumber = object.getString("cardNumber");
                    String accountNumber = object.getString("accountNumber");
                    String monthScore = object.getString("monthScore");
                    String dayScore = object.getString("dayScore");
                    int accessInsertService = object.getInt("accessInsertService");
                    int accessStationDeterminationPage = object.getInt("accessStationDeterminationPage");
                    int balance = object.getInt("balance");
                    String typeService = object.getString("typeService");
                    String queue = object.getString("queue");
                    String city = object.getString("city");
                    int pushId = object.getInt("pushId");
                    String pushToken = object.getString("pushToken");
                    String complaintType = object.getString("ComplaintType");
                    String objectsType = object.getString("objectsType");
                    String ReasonsLock = object.getString("ReasonsLock");
                    String serviceCountToday = object.getString("serviceCountToday");
                    String serviceCountMonth = object.getString("serviceCountMonth");
                    int activeInQueue = object.getInt("activeInQueue");
                    int customerSupport = object.getInt("customerSupport");
                    int accessComplaint = object.getInt("accessComplaint");
                    String name = object.getString("name");
                    String family = object.getString("family");

//                    MyApplication.prefManager.setOperatorName(name + " " + family);
//                    MyApplication.prefManager.setCustomerSupport(customerSupport);
//                    MyApplication.prefManager.setAccessComplaint(accessComplaint);

                    MyApplication.prefManager.setUserCode(userId);
                    MyApplication.prefManager.setComplaint(complaintType);
//                    MyApplication.prefManager.setObjectsType(objectsType);
//                    MyApplication.prefManager.setReasonsLock(ReasonsLock);
//                    MyApplication.prefManager.setDailyScore(dayScore);
//                    MyApplication.prefManager.setMonthScore(monthScore);
//                    MyApplication.prefManager.setServiceCountMonth(serviceCountMonth);
//                    MyApplication.prefManager.setServiceCountToday(serviceCountToday);

                    if (block == 1) {
                        new GeneralDialog()
                                .title("هشدار")
                                .message("اکانت شما توسط سیستم مسدود شده است")
                                .firstButton("خروج از برنامه", () -> MyApplication.currentActivity.finish())
                                .show();
                        return;
                    }

                    if (changePass == 1) {
                        FragmentHelper
                                .toFragment(MyApplication.currentActivity, new LogInFragment())
                                .setStatusBarColor(MyApplication.currentActivity.getResources().getColor(R.color.black))
                                .setAddToBackStack(false)
                                .replace();
                        return;
                    }

                    if (updateAvailable == 1) {
                        updatePart(forceUpdate, updateUrl);
                        return;
                    }

                    MyApplication.prefManager.setPushId(pushId);
                    MyApplication.prefManager.setPushToken(pushToken);
//                    MyApplication.prefManager.setSheba(sheba);
//                    MyApplication.prefManager.setCardNumber(cardNumber);
                    MyApplication.prefManager.setAccountNumber(accountNumber);
//                    MyApplication.prefManager.setBalance(balance);

                    NotificationManager notificationManager = (NotificationManager) MyApplication.currentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
//                    notificationManager.cancel(Constant.USER_STATUS_NOTIFICATION_ID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {
            MyApplication.handler.post(() -> {
            });
        }
    };

    private void updatePart(int isForce, final String url) {
        GeneralDialog generalDialog = new GeneralDialog();
        if (isForce == 1) {
            generalDialog.title("به روز رسانی");
            generalDialog.cancelable(false);
            generalDialog.message("برای برنامه نسخه جدیدی موجود است لطفا برنامه را به روز رسانی کنید");
            generalDialog.firstButton("به روز رسانی", () -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                MyApplication.currentActivity.startActivity(i);
                MyApplication.currentActivity.finish();
            });
            generalDialog.secondButton("بستن برنامه", () -> MyApplication.currentActivity.finish());
            generalDialog.show();
        } else {
            generalDialog.title("به روز رسانی");
            generalDialog.cancelable(false);
            generalDialog.message("برای برنامه نسخه جدیدی موجود است در صورت تمایل میتوانید برنامه را به روز رسانی کنید");
            generalDialog.firstButton("به روز رسانی", () -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                MyApplication.currentActivity.startActivity(i);
                MyApplication.currentActivity.finish();
            });
            generalDialog.secondButton("فعلا نه", /*() ->*/ null/*startVoipService()*/);//todo
            generalDialog.show();
        }
    }

}
