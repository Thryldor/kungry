package ca.ulaval.ima.mp.ui.review.list


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.model.Review


class ReviewRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val REVIEW_WITH_IMAGE = 0
    private val REVIEW_WITHOUT_IMAGE = 1
    private val LOADER = 2

    private var isLoadingAdded = false
    private val reviews: ArrayList<Review> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            LOADER ->  LoaderViewHolder(inflater.inflate(R.layout.review_list_loading_item_fragment, parent, false))
            REVIEW_WITHOUT_IMAGE -> SimpleViewHolder(inflater.inflate(R.layout.review_list_item_fragment, parent, false))
            else -> throw RuntimeException("Invalid view holder type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = reviews[position]
        if (getItemViewType(position) == REVIEW_WITHOUT_IMAGE)
            (holder as SimpleViewHolder).username.text = item.creator!!.first_name
    }

    override fun getItemCount(): Int = reviews.size


    override fun getItemViewType(position: Int): Int {
        return if (position == reviews.size - 1 && isLoadingAdded) LOADER else REVIEW_WITHOUT_IMAGE
    }

    fun addAll(newReviews: ArrayList<Review>) {
        reviews.addAll(newReviews)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        reviews.add(
            Review(
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
        val position = reviews.size - 1
        reviews.removeAt(position)
        notifyItemRemoved(position)
    }


    inner class SimpleViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val username: TextView = mView.findViewById(R.id.username)
    }

    inner class ImageViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
    }

    inner class LoaderViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
    }

}
