package ca.ulaval.ima.mp.ui.restaurant

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ca.ulaval.ima.mp.MiniProject
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.Restaurant

class RestaurantActivity : AppCompatActivity() {

    companion object {
        val RESTAURANT_ID_KEY: String = "RESTAURANT_ID_KEY"
    }
    private var restaurantId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        restaurantId = intent.getStringExtra(RESTAURANT_ID_KEY)
            ?: throw RuntimeException("No restaurant ID passed to restaurant activity")

        APIService.getRestaurantById(
            restaurantId!!.toInt(),
            createHandler { result ->
                handleResult(result)
            }
        )
    }

    private fun handleResult(result: APIService.Result<Restaurant>) {
        val restaurant: Restaurant
        try {
            restaurant = result.getResult()
        }
        catch (e:  APIService.CallFailureException) {
            Toast.makeText(
                MiniProject.appContext,
                "Error getting the restaurant: " + e.wrapper!!.error!!.display,
                Toast.LENGTH_LONG
            ).show()
            return
        }
        setView(restaurant)
    }

    private fun setView(restaurant: Restaurant) {
        val imageView: ImageView = findViewById(R.id.restaurant_logo)
    }
}
