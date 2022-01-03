package ir.food.kitchenAndroid.app

class EndPoints {
    companion object {
        /*TODO : check apis and ports before release*/
        const val IP =
            "http://happypizza.ir"
//      "http://192.168.1.145"

        const val HAKWEYE_IP =
            "http://happypizza.ir"
//      "http://192.168.1.145"

        const val APIPort = "3010"

        const val HAWKEYE_APIPort = "1890"

        val PUSH_ADDRESS = "http://turbotaxi.ir:6060"
        const val ACRA_PATH = "http://turbotaxi.ir:6061/api/v1/crashReport"
        const val WEBSERVICE_PATH = "$IP:$APIPort/api/kitchen/v1/"
        const val DELI_PATH = "$IP:$APIPort/api/kitchen/v1/delivery/"
        const val HAWKEYE_PATH = "$HAKWEYE_IP:$HAWKEYE_APIPort/api/user/v1/"
        const val HAWKEYE_LOGIN_PATH = "$HAKWEYE_IP:$HAWKEYE_APIPort/api/user/v1/login/phone/"

        /******************************** Base Api  *****************************************/
        const val APP_INFO = WEBSERVICE_PATH + "app/info"
        const val LOG_IN = WEBSERVICE_PATH + "login"
        const val REGISTER_CODE = WEBSERVICE_PATH + "verificationcode"
        const val LOGIN_CODE = "$LOG_IN/verificationcode"
        const val REGISTER = WEBSERVICE_PATH + "register"
        const val ORDER = WEBSERVICE_PATH + "order/"
        const val NOT_READY_ORDER = ORDER + "active"
        const val READY = ORDER + "ready"
        const val HISTORY = ORDER + "finished"
        const val GET_PRODUCTS = WEBSERVICE_PATH + "product"
        const val EDIT_PRODUCTS = WEBSERVICE_PATH + "product"
        const val GET_PRODUCTS_TYPE = WEBSERVICE_PATH + "product/type"
        const val DELI_FINANCIAL = DELI_PATH + "financial"

        /******************************** refresh token Api  *****************************************/
        const val REFRESH_TOKEN = HAWKEYE_PATH + "token"
        const val VERIFICATION = HAWKEYE_LOGIN_PATH + "verification"
        const val CHECK = HAWKEYE_LOGIN_PATH + "check"
    }
}