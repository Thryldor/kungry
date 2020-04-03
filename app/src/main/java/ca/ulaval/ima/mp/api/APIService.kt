package ca.ulaval.ima.mp.api

import ca.ulaval.ima.mp.api.model.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Type
import java.util.*


object APIService {

    private val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")
    private const val SCHEME = "https"
    private const val HOST = "kungry.ca"
    private const val BASE_PATH = "/api/v1/"
    private val BASE_URL: HttpUrl =
        HttpUrl.Builder().scheme(SCHEME).host(HOST).addPathSegments(BASE_PATH).build()
    private val client: OkHttpClient = OkHttpClient();
    private val gson: Gson = Gson()
    private var logging_timestamp: Long? = null
    private var token: TokenOutput? = null
    private var logged = false

    // Account

    fun createAccount(model: CreateAccount, handler: AuthenticationCallback) {
        val jsonBody = gson.toJson(model)
        val body: RequestBody = RequestBody.create(JSON, jsonBody)
        val request: Request = Request.Builder()
            .url("$BASE_URL/account/")
            .post(body)
            .build()
        val type = object : TypeToken<TokenOutput>() {}.type
        executeRequest(request, object : ResponseHandler<TokenOutput>() {
            override fun onResult(data: TokenOutput) {
                token = data
                logged = true
                logging_timestamp = Date().time
                handler.onAuthenticationSuccess();
            }

            override fun onError(e: IOException?, wrapper: ResponseWrapper?) {
                throw AuthenticationFailure(e, wrapper);
            }
        }, type)
    }

    fun login(model: AccountLogin, handler: AuthenticationCallback) {
        val jsonBody = gson.toJson(model)
        val body: RequestBody = RequestBody.create(JSON, jsonBody)
        val request: Request = Request.Builder()
            .url("$BASE_URL/account/login/")
            .post(body)
            .build()
        val type = object : TypeToken<TokenOutput>() {}.type
        executeRequest(request, object : ResponseHandler<TokenOutput>() {
            override fun onResult(data: TokenOutput) {
                token = data
                logged = true
                logging_timestamp = Date().time
                handler.onAuthenticationSuccess();
            }

            override fun onError(e: IOException?, wrapper: ResponseWrapper?) {
                throw AuthenticationFailure(e, wrapper);
            }
        }, type)
    }

    private fun ensureLogged(handler: AuthenticationEnforcerCallback) {
        if (!isTokenExpired())
            return handler.onAuthenticationSuccess(token!!.access_token!!)
        val model = RefreshTokenInput(refresh_token=token!!.refresh_token)
        val jsonBody = gson.toJson(model)
        val body: RequestBody = RequestBody.create(JSON, jsonBody)
        val request: Request = Request.Builder()
            .url("$BASE_URL/account/refresh_token/")
            .post(body)
            .build()
        val type = object : TypeToken<TokenOutput>() {}.type
        executeRequest(request, object : ResponseHandler<TokenOutput>() {
            override fun onResult(data: TokenOutput) {
                token = data
                logged = true
                logging_timestamp = Date().time
                handler.onAuthenticationSuccess(data.access_token!!)
            }

            override fun onError(e: IOException?, wrapper: ResponseWrapper?) {
                throw AuthenticationFailure(e, wrapper);
            }
        }, type)
    }

    fun me(handler: ResponseHandler<Account>) {
        ensureLogged(object: AuthenticationEnforcerCallback {
            override fun onAuthenticationSuccess(token: String) {
                val request: Request = Request.Builder()
                    .url("$BASE_URL/account/me/")
                    .addHeader("Authorization", "Bearer ${APIService.token}")
                    .get()
                    .build()

                val type = object : TypeToken<Account>() {}.type
                executeRequest(request, handler, type)
            }
        })
    }

    // Restaurant

    fun getRestaurants() {
//    GET
//    /restaurant/
//    restaurant_list
    }

    fun searchRestaurant() {
//    GET
//    /restaurant/search/
//    restaurant_search
    }

    fun getRestaurantById() {
//    GET
//    /restaurant/{id}/
//    restaurant_read
    }

    fun getRestaurantReviews() {
//    GET
//    /restaurant/{id}/reviews/
//    restaurant_reviews
    }

    // Review

    fun createReview() {
//    POST
//    /review/
//    review_create
    }

    fun getMyReviews() {
//    GET
//    /review/mine/
//    review_mine
    }

    fun attachImageToReview() {
//    POST
//    /review/{id}/image/
//    review_image
    }

    // APIService types

    interface AuthenticationCallback {
        fun onAuthenticationSuccess()
    }

    private interface AuthenticationEnforcerCallback {
        fun onAuthenticationSuccess(token: String)
    }

    abstract class ResponseHandler<T> {
        abstract fun onResult(data: T)
        abstract fun onError(e: IOException?, wrapper: ResponseWrapper?)
    }

    class ResponseWrapper {
        var content: JsonElement? = null

        public class Meta {
            var status_code = 0
        }

        var meta: Meta? = null

        public class Error {
            var display: String? = null
            var details: List<JsonElement>? = null
        }

        var error: Error? = null
    }

    class NotLoggedException : Exception()
    class AuthenticationFailure(e: IOException?, wrapper: ResponseWrapper?) : Exception()


    // APIService utils

    private fun isTokenExpired(): Boolean {
        if (!logged)
            throw NotLoggedException();
        val date = Date()
        return date.time > logging_timestamp!! + token!!.expires_in!!
    }

    private fun <T> executeRequest(
        request: Request,
        handler: ResponseHandler<T>,
        type: Type
    ) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                handler.onError(e, null)
            }

            override fun onResponse(call: Call?, response: Response) {
                val body: ResponseBody = response.body()
                val wrapper: ResponseWrapper
                wrapper = try {
                    gson.fromJson(body.string(), ResponseWrapper::class.java)
                } catch (e: IOException) {
                    handler.onError(e, null)
                    return
                }
                if (!response.isSuccessful) {
                    handler.onError(null, wrapper)
                } else {
                    val data: T = gson.fromJson(wrapper.content, type)
                    handler.onResult(data)
                }
            }
        })
    }

}