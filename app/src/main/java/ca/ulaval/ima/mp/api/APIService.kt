package ca.ulaval.ima.mp.api

import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient


object APIService {

    private val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")
    private const val SCHEME = "http"
    private const val HOST = "68.183.207.74"
    private const val BASE_PATH = "/api/v1/"
    private val BASE_URL: HttpUrl =
        HttpUrl.Builder().scheme(SCHEME).host(HOST).addPathSegments(BASE_PATH).build()
    private val client: OkHttpClient = OkHttpClient();
    private val gson: Gson = Gson()
    private var token: String? = ""
    private var logged = false

    // Account

    fun createAccount() {
//        POST
//        /account/
//        account_create
    }

    fun login() {
//        POST
//        /account/login/
//        account_login
    }

    fun me() {
//        GET
//        /account/me/
//        account_me
    }

    fun refreshToken() {
//    POST
//    /account/refresh_token/
//    account_refresh_token
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
}