package ca.ulaval.ima.mp.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import ca.ulaval.ima.mp.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_restaurant_info_select.*
import kotlinx.android.synthetic.main.review_list_item_image_fragment.view.*

class RestaurantInfoSelectFragment : Fragment() {
    private lateinit var mImageLayout: FrameLayout
    private lateinit var mTitle: TextView
    private lateinit var mDescription: TextView
    private lateinit var mRating: RatingBar
    private lateinit var mReviewCount: TextView
    private lateinit var mDistance: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =  inflater.inflate(R.layout.fragment_restaurant_info_select, container, false)
        mImageLayout = v.findViewById(R.id.image_layout)
        mTitle = v.findViewById(R.id.snackbar_restaurant_name)
        mDescription = v.findViewById(R.id.snackbar_restaurant_type)
        mRating = v.findViewById(R.id.snackbar_restaurant_reviews)
        mReviewCount = v.findViewById(R.id.snackbar_restaurant_review_count)
        mDistance = v.findViewById(R.id.snackbar_restaurant_dist)

        Picasso.with(context)
            .load(arguments?.getString("imageLink"))
            .transform(
                RoundedCornersTransformation(
                    activity!!.resources.getDimension(R.dimen.image_corner_radius).toInt(),
                    0
                )
            )
            .fit()
            .into(mImageLayout.image, object : Callback {
                override fun onSuccess() {
                    mImageLayout.progress.visibility = View.GONE
                }

                override fun onError() {
                    Toast.makeText(
                        context,
                        "Can't load the restaurant picture",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
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
