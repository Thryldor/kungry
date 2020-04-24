package ca.ulaval.ima.mp.ui.restaurant

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.model.RestaurantLight
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.restaurant_list_item.view.*

class RestaurantRecyclerViewAdapter(
    val context: Context,
    val controller: RestaurantListFragment.RestaurantListFragmentController
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val RESTAURANT = 0
    private val LOADER = 1

    private var isLoadingAdded = false
    private val restaurants: ArrayList<RestaurantLight> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            LOADER -> LoaderViewHolder(
                inflater.inflate(
                    R.layout.recyclerview_item_loading_fragment,
                    parent,
                    false
                )
            )
            RESTAURANT -> RestaurantViewHolder(
                inflater.inflate(
                    R.layout.restaurant_list_item,
                    parent,
                    false
                )
            )
            else -> throw RuntimeException("Invalid view holder type")
        }
    }

    override fun getItemCount(): Int = restaurants.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = restaurants[position]
        when (getItemViewType(position)) {
            RESTAURANT -> {
                val restaurantHolder = holder as RestaurantViewHolder
                restaurantHolder.mNameView.text = item.name
                restaurantHolder.mTypeView.text = "Snack/Food • Confort food"
                restaurantHolder.mNbevalView.text = item.review_count.toString()
                restaurantHolder.mDistView.text = "${item.distance}km"
                restaurantHolder.mRatingBar.rating = item.review_average!!
                Picasso.with(context)
                    .load(item.image)
                    .transform(
                        RoundedCornersTransformation(
                            context.resources.getDimension(R.dimen.image_corner_radius).toInt(),
                            0
                        )
                    )
                    .fit()
                    .into(restaurantHolder.mImageLayout.image, object : Callback {
                        override fun onSuccess() {
                            restaurantHolder.mImageLayout.progress.visibility = View.GONE
                        }

                        override fun onError() {
                            Toast.makeText(
                                context,
                                "Can't load the restaurant picture",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                with(holder.mView) {
                    setOnClickListener {
                        controller.onRestaurantClick(item)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == restaurants.size - 1 && isLoadingAdded)
            LOADER
        else
            RESTAURANT
    }

    fun addAll(newReviews: ArrayList<RestaurantLight>) {
        restaurants.addAll(newReviews)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        restaurants.add(
            RestaurantLight(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        )
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = restaurants.size - 1
        restaurants.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class RestaurantViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mImageLayout: FrameLayout = mView.image_layout
        val mNameView: TextView = mView.restaurant_name
        val mTypeView: TextView = mView.restaurant_type
        val mNbevalView: TextView = mView.restaurant_review_count
        val mDistView: TextView = mView.restaurant_dist
        val mRatingBar: RatingBar = mView.restaurant_reviews
    }

    inner class LoaderViewHolder(mView: View) : RecyclerView.ViewHolder(mView)

}
