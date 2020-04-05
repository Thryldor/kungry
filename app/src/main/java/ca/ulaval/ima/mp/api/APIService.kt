package ca.ulaval.ima.mp.api

import android.os.Handler
import android.os.Looper
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

    fun createAccount(model: CreateAccount, handler: ResponseHandler<TokenOutput>) {
        val jsonBody = gson.toJson(model)
        val body: RequestBody = RequestBody.create(JSON, jsonBody)
        val request: Request = Request.Builder()
            .url("$BASE_URL/account/")
            .post(body)
            .build()
        val type = object : TypeToken<TokenOutput>() {}.type
        executeRequest(request, object : ResponseHandler<TokenOutput> {
            override fun onResult(result: Result<TokenOutput>) {
                if (result.isSuccessful()) {
                    token = result.getResult()
                    logged = true
                    logging_timestamp = Date().time
                }
                handler.onResult(result);
            }

        }, type)
    }

    fun login(model: AccountLogin, handler: ResponseHandler<TokenOutput>) {
        val jsonBody = gson.toJson(model)
        val body: RequestBody = RequestBody.create(JSON, jsonBody)
        val request: Request = Request.Builder()
            .url("$BASE_URL/account/login/")
            .post(body)
            .build()
        val type = object : TypeToken<TokenOutput>() {}.type
        executeRequest(request, object : ResponseHandler<TokenOutput> {
            override fun onResult(result: Result<TokenOutput>) {
                if (result.isSuccessful()) {
                    token = result.getResult()
                    logged = true
                    logging_timestamp = Date().time
                }
                handler.onResult(result);
            }

        }, type)
    }

    fun me(handler: ResponseHandler<Account>) {
        executeAuthenticatedCall(object : AuthResponseHandler<Account>(handler) {
            override fun onAuthResult(
                result: Result<String>,
                customHandler: ResponseHandler<Account>
            ) {
                if (!result.isSuccessful())
                    return customHandler.onResult(Result(result))
                val authToken = result.getResult()
                val request: Request = Request.Builder()
                    .url("$BASE_URL/account/me/")
                    .addHeader("Authorization", "Bearer $authToken")
                    .get()
                    .build()

                val type = object : TypeToken<Account>() {}.type
                executeRequest(request, customHandler, type)
            }
        })
    }

    // Restaurant

    fun getRestaurants(
        model: RestaurantsGetRequest,
        handler: ResponseHandler<PaginationResult<RestaurantLight>>
    ) {
        val url = HttpUrl.Builder()
            .scheme(SCHEME)
            .host(HOST)
            .addPathSegment(BASE_PATH)
            .addPathSegment("restaurant")
            .addQueryParameter("page", model.page.toString())
            .addQueryParameter("page_size", model.page_size.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        val type = object : TypeToken<PaginationResult<RestaurantLight>>() {}.type
        executeRequest(request, handler, type)
    }

    fun searchRestaurant(
        model: RestaurantsSearchRequest,
        handler: ResponseHandler<PaginationResult<RestaurantLight>>
    ) {
        val url = HttpUrl.Builder()
            .scheme(SCHEME)
            .host(HOST)
            .addPathSegment(BASE_PATH)
            .addPathSegment("restaurant")
            .addPathSegment("search")
            .addQueryParameter("page", model.page.toString())
            .addQueryParameter("page_size", model.page_size.toString())
            .addQueryParameter("latitude", model.latitude.toString())
            .addQueryParameter("longitude", model.longitude.toString())
            .addQueryParameter("radius", model.radius.toString())
            .addQueryParameter("text", model.text.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        val type = object : TypeToken<PaginationResult<RestaurantLight>>() {}.type
        executeRequest(request, handler, type)
    }

    fun getRestaurantById(id: Int, handler: ResponseHandler<Restaurant>) {
        val url = HttpUrl.Builder()
            .scheme(SCHEME)
            .host(HOST)
            .addPathSegment(BASE_PATH)
            .addPathSegment("restaurant")
            .addPathSegment(id.toString())
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        val type = object : TypeToken<Restaurant>() {}.type
        executeRequest(request, handler, type)
    }

    fun getRestaurantReviews(id: Int, handler: ResponseHandler<PaginationResult<Review>>) {
        val url = HttpUrl.Builder()
            .scheme(SCHEME)
            .host(HOST)
            .addPathSegment(BASE_PATH)
            .addPathSegment("restaurant")
            .addPathSegment(id.toString())
            .addPathSegment("reviews")
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        val type = object : TypeToken<PaginationResult<Review>>() {}.type
        executeRequest(request, handler, type)
    }

    // Review

    fun createReview(model: CreateReview, handler: ResponseHandler<Review>) {
        executeAuthenticatedCall(object : AuthResponseHandler<Review>(handler) {
            override fun onAuthResult(
                result: Result<String>,
                customHandler: ResponseHandler<Review>
            ) {
                if (!result.isSuccessful())
                    return customHandler.onResult(Result(result))
                val authToken = result.getResult()
                val jsonBody = gson.toJson(model)
                val body: RequestBody = RequestBody.create(JSON, jsonBody)
                val request: Request = Request.Builder()
                    .url("$BASE_URL/review/")
                    .addHeader("Authorization", "Bearer $authToken")
                    .post(body)
                    .build()
                val type = object : TypeToken<Review>() {}.type
                executeRequest(request, customHandler, type)
            }
        })
    }


    fun getMyReviews(handler: ResponseHandler<PaginationResult<Review>>) {
        executeAuthenticatedCall(object : AuthResponseHandler<PaginationResult<Review>>(handler) {
            override fun onAuthResult(
                result: Result<String>,
                customHandler: ResponseHandler<PaginationResult<Review>>
            ) {
                if (!result.isSuccessful())
                    return customHandler.onResult(Result(result))
                val authToken = result.getResult()
                val request: Request = Request.Builder()
                    .url("$BASE_URL/review/mine/")
                    .addHeader("Authorization", "Bearer $authToken")
                    .get()
                    .build()

                val type = object : TypeToken<PaginationResult<Review>>() {}.type
                executeRequest(request, customHandler, type)
            }
        })
    }

    fun attachImageToReview() {
//    POST
//    /review/{id}/image/
//    review_image
    }

// APIService types

    abstract class AuthResponseHandler<T>(val handler: ResponseHandler<T>) {
        abstract fun onAuthResult(result: Result<String>, customHandler: ResponseHandler<T>)
    }

    interface ResponseHandler<T> {
        fun onResult(result: Result<T>)
    }

    class Result<T> {
        private var data: T? = null
        private var authenticationFailuredException: AuthenticationFailuredException? = null
        private var callFailuredException: CallFailureException? = null
        private var notLoggedException: NotLoggedException? = null

        constructor(data: T) {
            this.data = data
        }

        constructor(e: CallFailureException) {
            callFailuredException = e
        }

        constructor(e: AuthenticationFailuredException) {
            authenticationFailuredException = e
        }

        constructor(e: NotLoggedException) {
            notLoggedException = e
        }

        constructor(result: Result<*>) {
            notLoggedException = result.notLoggedException
            authenticationFailuredException = result.authenticationFailuredException
            callFailuredException = result.callFailuredException
        }

        fun isSuccessful(): Boolean {
            return data != null
        }

        fun getResult(): T {
            if (callFailuredException != null)
                throw callFailuredException!!
            if (notLoggedException != null)
                throw notLoggedException!!
            if (authenticationFailuredException != null)
                throw authenticationFailuredException!!
            return data!!
        }

    }

    class ResponseWrapper {
        var content: JsonElement? = null

        class Meta {
            var status_code = 0
        }

        var meta: Meta? = null

        class Error {
            var display: String? = null
            var details: List<JsonElement>? = null
        }

        var error: Error? = null
    }

    class NotLoggedException : Exception()
    class AuthenticationFailuredException(val e: IOException?, val wrapper: ResponseWrapper?) :
        Exception()

    class CallFailureException(val e: IOException?, val wrapper: ResponseWrapper?) : Exception()


// APIService utils

    private fun isTokenExpired(): Boolean {
        val date = Date()
        return date.time - token!!.expires_in!! / 2 > logging_timestamp!! + token!!.expires_in!!
    }

    private fun <T> executeOnMainThread(handler: ResponseHandler<T>, result: Result<T>) {
        val mainHandler = Handler(Looper.getMainLooper());

        val myRunnable = Runnable {
            handler.onResult(result)
        };
        mainHandler.post(myRunnable);
    }

    private fun <T> executeRequest(
        request: Request,
        handler: ResponseHandler<T>,
        type: Type
    ) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                executeOnMainThread(handler, Result(CallFailureException(e, null)))
            }

            override fun onResponse(call: Call?, response: Response) {
                val body: ResponseBody = response.body()
                val wrapper: ResponseWrapper
                wrapper = try {
                    gson.fromJson(body.string(), ResponseWrapper::class.java)
                } catch (e: IOException) {
                    executeOnMainThread(handler, Result(CallFailureException(e, null)))
                    return
                }
                if (!response.isSuccessful) {
                    executeOnMainThread(handler, Result(CallFailureException(null, wrapper)))
                } else {
                    val data: T = gson.fromJson(wrapper.content, type)
                    executeOnMainThread(handler, Result(data))
                }
            }
        })
    }

    private fun <T> executeAuthenticatedCall(handler: AuthResponseHandler<T>) {
        // result middleware transforming 401 callExceptions in authenticationFailureException
        val authCheck = object : ResponseHandler<T> {
            override fun onResult(result: Result<T>) {
                try {
                    result.getResult()
                } catch (e: CallFailureException) {
                    if (e.wrapper != null && e.wrapper.meta!!.status_code == 401)
                        return handler.handler.onResult(
                            Result(
                                AuthenticationFailuredException(
                                    e.e,
                                    e.wrapper
                                )
                            )
                        )
                }
                handler.handler.onResult(result)
            }
        }
        if (!logged)
            return handler.onAuthResult(Result(NotLoggedException()), handler.handler)
        if (!isTokenExpired())
            return handler.onAuthResult(Result(token!!.access_token!!), authCheck)
        val model = RefreshTokenInput(refresh_token = token!!.refresh_token)
        val jsonBody = gson.toJson(model)
        val body: RequestBody = RequestBody.create(JSON, jsonBody)
        val request: Request = Request.Builder()
            .url("$BASE_URL/account/refresh_token/")
            .post(body)
            .build()
        val type = object : TypeToken<TokenOutput>() {}.type
        executeRequest(request, object : ResponseHandler<TokenOutput> {
            override fun onResult(result: Result<TokenOutput>) {
                token = result.getResult()
                logging_timestamp = Date().time
                handler.onAuthResult(Result(token!!.access_token!!), authCheck)
            }

        }, type)
    }

}