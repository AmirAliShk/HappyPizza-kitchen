package ir.food.kitchenAndroid.app

class EndPoints {
    companion object {
        /*TODO : check apis and ports before release*/
        const val IP = "http://happypizza.ir:3010"
        const val HAKWEYE_IP = "http://happypizza.ir:1890"
        const val WEBSERVICE_PATH = "$IP/api/kitchen/v1/"
        const val CHECKOUT = "$IP/api/kitchen/v1/checkout/"
        const val DELI_PATH = "$IP/api/kitchen/v1/delivery/"
        const val HAWKEYE_PATH = "$HAKWEYE_IP/api/user/v1/"
        const val HAWKEYE_LOGIN_PATH = "$HAKWEYE_IP/api/user/v1/login/phone/"

        /******************************** Base Api  *****************************************/
        const val APP_INFO = WEBSERVICE_PATH + "app/info"
        const val LOG_IN = WEBSERVICE_PATH + "login"
        const val REGISTER_CODE = WEBSERVICE_PATH + "verificationcode"
        const val LOGIN_CODE = "$LOG_IN/verificationcode"
        const val REGISTER = WEBSERVICE_PATH + "register"
        const val ORDER = WEBSERVICE_PATH + "order/"
        const val NOT_READY_ORDER = ORDER + "active"
        const val READY = ORDER + "ready"
        const val SENDING = ORDER + "ready/sending"
        const val HISTORY = ORDER + "finished"
        const val PACKED = ORDER + "pack"
        const val CANCEL_DELIVER = ORDER + "setFree"
        const val GET_PRODUCTS = WEBSERVICE_PATH + "product"
        const val EDIT_PRODUCTS = WEBSERVICE_PATH + "product"
        const val GET_PRODUCTS_TYPE = WEBSERVICE_PATH + "product/type"
        const val DELI_FINANCIAL = DELI_PATH + "financial"
        const val STATUS_DELI = DELI_PATH + "status"
        const val LOCATION_DELI = DELI_PATH + "location/"
        const val CHECKOUT_DELI = CHECKOUT + "delivery"

        /******************************** refresh token Api  *****************************************/
        const val REFRESH_TOKEN = HAWKEYE_PATH + "token"
        const val VERIFICATION = HAWKEYE_LOGIN_PATH + "verification"
        const val CHECK = HAWKEYE_LOGIN_PATH + "check"
    }
}