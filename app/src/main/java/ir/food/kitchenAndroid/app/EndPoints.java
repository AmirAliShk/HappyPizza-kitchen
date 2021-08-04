package ir.food.kitchenAndroid.app;

import ir.food.kitchenAndroid.BuildConfig;

public class EndPoints {

    /*TODO : check apis and ports before release*/

//    http://192.168.1.145:3000/api/operator/*****
//    http://api.parsian.ir:1881/api/operator/
//    http://192.168.1.145/api/findway/citylatinname/address

    public static final String IP = (BuildConfig.DEBUG)
//          ? "http://192.168.1.145"
            ? "http://happypizza.ir"
            : "http://happypizza.ir";//todo
//          : "http://192.168.1.145";

    public static final String HAKWEYE_IP = (BuildConfig.DEBUG)
//          ? "http://192.168.1.145"
            ? "http://happypizza.ir"
            : "http://happypizza.ir";//todo
//          : "http://192.168.1.145";


    public static final String APIPort = (BuildConfig.DEBUG) ? "3010" : "1881";

    public static final String HAWKEYE_APIPort = (BuildConfig.DEBUG) ? "3010" : "1890";

    public static final String ACRA_PATH = "http://turbotaxi.ir:6061/api/v1/crashReport";

    public static final String WEBSERVICE_PATH = IP + ":" + APIPort + "/api/kitchen/v1/";

    public static final String HAWKEYE_PATH = HAKWEYE_IP + ":" + HAWKEYE_APIPort + "/api/user/v1/";
    public static final String HAWKEYE_LOGIN_PATH = HAKWEYE_IP + ":" + HAWKEYE_APIPort + "/api/user/v1/login/phone/";

    /******************************** Base Api *********************************/

    public static final String APP_INFO = WEBSERVICE_PATH + "app/info";
    public static final String LOG_IN = WEBSERVICE_PATH + "login";
    public static final String REGISTER_CODE = WEBSERVICE_PATH + "verificationcode";
    public static final String LOGIN_CODE = LOG_IN + "/" + "verificationcode";
    public static final String REGISTER = WEBSERVICE_PATH + "register";
    public static final String ORDER = WEBSERVICE_PATH + "order/";
    public static final String NOT_READY_ORDER = ORDER + "active";
    public static final String READY = ORDER + "ready";

    /******************************** refresh token Api *********************************/

    public static final String REFRESH_TOKEN = HAWKEYE_PATH + "token";
    public static final String VERIFICATION = HAWKEYE_LOGIN_PATH + "verification";
    public static final String CHECK = HAWKEYE_LOGIN_PATH + "check";

}
