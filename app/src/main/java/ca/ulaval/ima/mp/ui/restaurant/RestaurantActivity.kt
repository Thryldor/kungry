package ca.ulaval.ima.mp.ui.restaurant

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ca.ulaval.ima.mp.MiniProject
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.*
import ca.ulaval.ima.mp.tools.TypeConverter
import ca.ulaval.ima.mp.ui.review.creation.LoginActivity
import ca.ulaval.ima.mp.ui.review.creation.ReviewCreationActivity
import ca.ulaval.ima.mp.ui.review.creation.ReviewCreationPopupFragment
import ca.ulaval.ima.mp.ui.review.list.ReviewListActivity
import ca.ulaval.ima.mp.ui.review.preview.ReviewCommentImageActivity
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.restaurant_activity.*
import kotlinx.android.synthetic.main.restaurant_activity.view.*
import kotlinx.android.synthetic.main.review_creation_popup_fragment.view.*
import kotlinx.android.synthetic.main.review_list_item_fragment.view.*
import java.text.SimpleDateFormat
import java.util.*


class RestaurantActivity : AppCompatActivity() {


    private val LOCATION_FINE_CODE = 123;

    private var currentLocation: LatLng? = null

    companion object {
        val RESTAURANT_ID_KEY: String = "RESTAURANT_ID_KEY"
    }

    private var restaurantId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_activity)
        restaurantId = intent.getStringExtra(RESTAURANT_ID_KEY)
            ?: throw RuntimeException("No restaurant ID passed to restaurant activity")

        supportFragmentManager.beginTransaction()
            .replace(R.id.popup, ReviewCreationPopupFragment.newInstance(restaurantId!!.toInt()))
            .commitNow();

        back_button.setOnClickListener { finish() }
        see_more.setOnClickListener {
            val intent = Intent(this, ReviewListActivity::class.java).apply {
                putExtra(ReviewListActivity.RESTAURANT_ID_KEY, restaurantId)
            }
            startActivity(intent)
        }

        getLocation()
        handleResult()
        fetchReviews()
    }

    private fun handleResult() {
        APIService.getRestaurantById(
            RestaurantGetRequest(
                currentLocation?.latitude,
                currentLocation?.longitude,
                restaurantId!!.toInt()
            ),
            createHandler { response ->
                try {
                    val result = response.getResult()
                    setView(result)
                    supportFragmentManager.beginTransaction()
                        .add(
                            R.id.map,
                            RestaurantMapFragment.newInstance(
                                result.location?.latitude.toString(),
                                result.location?.longitude.toString()
                            )
                        )
                        .commit()
                } catch (e: APIService.CallFailureException) {
                    Toast.makeText(
                        MiniProject.appContext,
                        "Error getting the restaurant: " + e.wrapper!!.error!!.display,
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        )
    }

    private fun setView(restaurant: Restaurant) {
        val phone = restaurant.phone_number!!
        restaurant_name.text = restaurant.name
        restaurant_dist.text = "${restaurant.distance}km"
        restaurant_review_count.text = "(${restaurant.review_count})"
        review_number.text = "(${restaurant.review_count})"
        restaurant_phone.text =
            "(${phone.substring(0, 3)}) ${phone.substring(3, 6)}-${phone.substring(
                6,
                phone.length
            )}"
        restaurant_phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL);
            intent.data = Uri.parse("tel:${restaurant.phone_number}");
            startActivity(intent);

        }
        restaurant_website.text = restaurant.website
        restaurant_website.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(restaurant.website);
            startActivity(i);

        }
        restaurant_rate.rating = restaurant.review_average!!
        restaurant_type.text = TypeConverter.convert(restaurant.type!!)
        setDay(restaurant.opening_hours)
        Picasso.with(this)
            .load(restaurant.image)
            .fit()
            .into(image_layout.image, object : Callback {
                override fun onSuccess() {
                    image_layout.progress.visibility = View.GONE
                }

                override fun onError() {
                    Toast.makeText(
                        parent,
                        "Can't load the restaurant picture",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setDay(openingHours: ArrayList<OpeningHour>?) {
        val parse = SimpleDateFormat("hh:mm:ss");
        val format = SimpleDateFormat("hh:mm");
        for (hours in openingHours!!.iterator()) {
            val view = when (hours.day) {
                "SUN" -> sunday
                "MON" -> monday
                "TUE" -> tuesday
                "WED" -> wednesday
                "THU" -> thursday
                "FRI" -> friday
                "SAT" -> saturday
                else -> null
            }
                ?: continue
            view.text = "${format.format(parse.parse(hours.opening_hour))} à ${format.format(
                parse.parse(hours.closing_hour)
            )}"
        }
    }

    private fun fetchReviews() {
        APIService.getRestaurantReviews(
            RestaurantGetReviewsRequest(
                restaurantId?.toInt()!!,
                1,
                5
            ), createHandler { resp ->
                try {
                    val result = resp.getResult();
                    reviews.reviews_progress.visibility = View.GONE
                    for (review in result.results) {
                        createReview(review)

                    }
                } catch (e: APIService.CallFailureException) {
                    Toast.makeText(
                        MiniProject.appContext,
                        e.wrapper?.error?.display,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun createReview(review: Review) {
        if (review.image == null) {
            val view = layoutInflater.inflate(R.layout.review_list_item_fragment, reviews, true)
            val parser = SimpleDateFormat("yyyy-MM-dd")
            val date = parser.parse(review.date!!)
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.CANADA_FRENCH)
            view.fullname.text =
                "${review.creator!!.first_name.toString()} ${review.creator!!.last_name}"
            view.date.text = formatter.format(date)
            view.comment.text = review.comment
            view.rate.rating = review.stars?.toFloat()!!
        } else {
            val view =
                layoutInflater.inflate(R.layout.review_list_item_image_fragment, reviews, true)
            val parser = SimpleDateFormat("yyyy-MM-dd")
            val date = parser.parse(review.date!!)
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.CANADA_FRENCH)
            view.fullname.text =
                "${review.creator!!.first_name.toString()} ${review.creator!!.last_name}"
            view.date.text = formatter.format(date)
            view.comment.text = review.comment
            view.rate.rating = review.stars?.toFloat()!!
            view.image_layout.image.setOnClickListener {
                val intent = Intent(this, ReviewCommentImageActivity::class.java).apply {
                    putExtra(ReviewCommentImageActivity.IMAGE_URL_KEY, review.image)
                }
                startActivity(intent)
            }
            Picasso.with(this)
                .load(review.image)
                .transform(
                    RoundedCornersTransformation(
                        resources.getDimension(R.dimen.image_corner_radius).toInt(),
                        0
                    )
                )
                .fit()
                .into(view.image_layout.image, object : Callback {
                    override fun onSuccess() {
                        view.image_layout.progress.visibility = View.GONE
                    }

                    override fun onError() {
                        Toast.makeText(
                            MiniProject.appContext,
                            "Can't load the comment picture",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        }
    }

    private fun fetchLocation(location: Location?) {
        if (location != null)
            currentLocation = LatLng(location.latitude, location.longitude)
        //currentLocation = LatLng(46.829853, -71.254028)
    }

    private fun getLocation(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location =
                (getSystemService(Context.LOCATION_SERVICE) as LocationManager).getLastKnownLocation(
                    LocationManager.GPS_PROVIDER
                )
            fetchLocation(location)
            return true
        } else {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(
                this,
                permissions,
                LOCATION_FINE_CODE
            )
            return false
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_FINE_CODE) {
            return
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location =
                (getSystemService(Context.LOCATION_SERVICE) as LocationManager).getLastKnownLocation(
                    LocationManager.GPS_PROVIDER
                )
            fetchLocation(location)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoginActivity.LOGIN_REQUEST && resultCode == Activity.RESULT_OK) {

            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.popup,
                    ReviewCreationPopupFragment.newInstance(restaurantId!!.toInt())
                )
                .commitNow();
        }
    }

}
