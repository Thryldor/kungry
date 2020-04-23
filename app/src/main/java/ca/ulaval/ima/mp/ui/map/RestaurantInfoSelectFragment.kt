package ca.ulaval.ima.mp.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView

import ca.ulaval.ima.mp.R
import com.squareup.picasso.Picasso

class RestaurantInfoSelectFragment : Fragment() {
    private lateinit var mImage: ImageView
    private lateinit var mTitle: TextView
    private lateinit var mDescription: TextView
    private lateinit var mRating: RatingBar
    private lateinit var mReviewCount: TextView
    private lateinit var mDistance: TextView

    companion object {
        fun newInstance() = RestaurantInfoSelectFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =  inflater.inflate(R.layout.fragment_restaurant_info_select, container, false)
        mImage = v.findViewById(R.id.snackbar_restaurant_logo)
        mTitle = v.findViewById(R.id.snackbar_restaurant_name)
        mDescription = v.findViewById(R.id.snackbar_restaurant_type)
        mRating = v.findViewById(R.id.snackbar_restaurant_reviews)
        mReviewCount = v.findViewById(R.id.snackbar_restaurant_review_count)
        mDistance = v.findViewById(R.id.snackbar_restaurant_dist)

        Picasso.with(context!!)
            .load(arguments?.getString("imageLink"))
            .fit()
            .into(mImage)
        mTitle.text = arguments?.getString("title")
        mDescription.text = arguments?.getString("description")
        mRating.rating = arguments?.getString("reviewAverage")!!.toFloat()
        val reviewCount = arguments?.getInt("reviewCount").toString()
        mReviewCount.text = "($reviewCount)"
        val distance = arguments?.getString("distance")
        mDistance.text = "$distance km"
        return v
    }
}
