package ir.food.kitchenAndroid.app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class PrefManager {
    private var _context: Context? = null
    private val prefName = MyApplication.context.applicationInfo.name
    private var sharedPreferences: SharedPreferences

    // Editor for Shared preferences
    private var editor: SharedPreferences.Editor

    constructor(context: Context) {
        this._context = context
        this.sharedPreferences = MyApplication.context.getSharedPreferences(prefName, 0)
        editor = this.sharedPreferences.edit()
    }

    private val PREF_NAME = MyApplication.context.applicationInfo.name
    private val KEY_KEY = "key"
    private val KEY_USER_CODE = "userCode"
    private val KEY_USER_NAME = "userName"
    private val KEY_PASSWORD = "password"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"
    private val PUSH_TOKEN = "pushToken"
    private val PUSH_ID = "pushID"
    private val ACCOUNT_NUMBER = "accountNumber"
    private val KEY_APP_STATUS = "AppStatus"
    private val AUTHORIZATION = "Authorization"
    private val ID_TOKEN = "id_token"
    private val REFRESH_TOKEN = "refreshToken"
    private val REPETITION_TIME = "repetitionTime"
    private val KEY_ACTIVATION_REMAINING_TIME = "activationRemainingTime"
    private val PRODUCTS = "products"
    private val ACTIVE_IN_QUEUE = "activeInQueue"
    private val PRODUCT_LIST = "productList"

    var productList: String?
        get() = this.sharedPreferences.getString(PRODUCT_LIST, "")
        set(productList) {
            editor.putString(PRODUCT_LIST, productList)
            editor.commit()
        }

    var activeInQueue: Boolean
        get() = this.sharedPreferences.getBoolean(ACTIVE_IN_QUEUE, false)
        set(activeInQueue) {
            editor.putBoolean(ACTIVE_IN_QUEUE, activeInQueue)
            editor.commit()
        }

    var authorization: String?
        get() = this.sharedPreferences.getString(AUTHORIZATION, "")
        set(value) {
            editor.putString(AUTHORIZATION, value)
            editor.commit()
        }
    var idToken: String?
        get() = this.sharedPreferences.getString(ID_TOKEN, "")
        set(idToken) {
            editor.putString(ID_TOKEN, idToken)
            editor.commit()
        }
    var refreshToken: String?
        get() = this.sharedPreferences.getString(REFRESH_TOKEN, "")
        set(refreshToken) {
            editor.putString(REFRESH_TOKEN, refreshToken)
            editor.commit()
        }
    var userCode: String?
        get() = this.sharedPreferences.getString(KEY_USER_CODE, "0")
        set(v) {
            editor.putString(KEY_USER_CODE, v)
            editor.commit()
        }
    var repetitionTime: Int
        get() = this.sharedPreferences.getInt(REPETITION_TIME, 0)
        set(repetitionTime) {
            editor.putInt(REPETITION_TIME, repetitionTime)
            editor.commit()
        }
    var pushToken: String?
        get() = this.sharedPreferences.getString(PUSH_TOKEN, "")
        set(v) {
            editor.putString(PUSH_TOKEN, v)
            editor.commit()
        }
    var pushId: Int
        get() = this.sharedPreferences.getInt(PUSH_ID, 5)
        set(v) {
            editor.putInt(PUSH_ID, v)
            editor.commit()
        }
    var accountNumber: String?
        get() = this.sharedPreferences.getString(ACCOUNT_NUMBER, "")
        set(accountNumber) {
            editor.putString(ACCOUNT_NUMBER, accountNumber)
            editor.commit()
        }
    var password: String?
        get() = this.sharedPreferences.getString(KEY_PASSWORD, "0")
        set(pass) {
            editor.putString(KEY_PASSWORD, pass)
            editor.commit()
        }
    var userName: String?
        get() = this.sharedPreferences.getString(KEY_USER_NAME, "0")
        set(userName) {
            editor.putString(KEY_USER_NAME, userName)
            editor.commit()
        }

    var activationRemainingTime: Long
        get() = this.sharedPreferences.getLong(
            KEY_ACTIVATION_REMAINING_TIME,
            repetitionTime.toLong()
        )
        set(v) {
            editor.putLong(KEY_ACTIVATION_REMAINING_TIME, v)
            editor.commit()
        }

    fun setProducts(products: String) {
        Log.d("LOG", "setProducts: $products")
        editor.putString(PRODUCTS, products)
        editor.commit()
    }

    val products: String?
        get() = this.sharedPreferences.getString(PRODUCTS, "")

    fun cleanPrefManger() {
        sharedPreferences.edit().clear().apply()
    }
}