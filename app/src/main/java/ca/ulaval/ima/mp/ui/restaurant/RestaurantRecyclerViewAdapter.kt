package ca.ulaval.ima.mp.ui.restaurant

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.model.RestaurantLight

import ca.ulaval.ima.mp.ui.restaurant.RestaurantListFragment.OnRestaurantListener
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.restaurant_list_item.view.*

class RestaurantRecyclerViewAdapter(
    private val mValues: List<RestaurantLight>,
    private val mListener: OnRestaurantListener?
) : RecyclerView.Adapter<RestaurantRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as RestaurantLight
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onRestaurantClick(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mNameView.text = item.name
        holder.mTypeView.text = "Snack/Food • Confort food"
        holder.mNbevalView.text = item.review_count.toString()
        holder.mDistView.text = "${item.distance}km"
        holder.mRatingBar.rating = item.review_average!!
        Picasso.with(holder.mLogoView.context)
            .load(item.image)
            .fit()
            .into(holder.mLogoView)
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mLogoView: ImageView = mView.restaurant_logo
        val mNameView: TextView = mView.restaurant_name
        val mTypeView: TextView = mView.restaurant_type
        val mNbevalView: TextView = mView.restaurant_review_count
        val mDistView: TextView = mView.restaurant_dist
        val mRatingBar: RatingBar = mView.restaurant_reviews
    }
}
