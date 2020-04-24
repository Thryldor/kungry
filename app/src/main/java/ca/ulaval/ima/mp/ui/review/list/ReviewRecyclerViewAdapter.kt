package ca.ulaval.ima.mp.ui.review.list


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.model.Review
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.review_list_item_fragment.view.comment
import kotlinx.android.synthetic.main.review_list_item_fragment.view.date
import kotlinx.android.synthetic.main.review_list_item_fragment.view.fullname
import kotlinx.android.synthetic.main.review_list_item_fragment.view.item_content
import kotlinx.android.synthetic.main.review_list_item_fragment.view.rate
import kotlinx.android.synthetic.main.review_list_item_header_fragment.view.*
import kotlinx.android.synthetic.main.review_list_item_image_fragment.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ReviewRecyclerViewAdapter(val context: Context, private val maxReviews: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val HEADER = 0
    private val REVIEW_WITH_IMAGE = 1
    private val REVIEW_WITHOUT_IMAGE = 2
    private val LOADER = 3

    private var isLoadingAdded = false
    private val reviews: ArrayList<Review> = ArrayList()

    init {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> HeaderViewHolder(
                inflater.inflate(
                    R.layout.review_list_item_header_fragment,
                    parent,
                    false
                )
            )
            LOADER -> LoaderViewHolder(
                inflater.inflate(
                    R.layout.review_list_item_loading_fragment,
                    parent,
                    false
                )
            )
            REVIEW_WITHOUT_IMAGE -> SimpleViewHolder(
                inflater.inflate(
                    R.layout.review_list_item_fragment,
                    parent,
                    false
                )
            )
            REVIEW_WITH_IMAGE -> ImageViewHolder(
                inflater.inflate(
                    R.layout.review_list_item_image_fragment,
                    parent,
                    false
                )
            )
            else -> throw RuntimeException("Invalid view holder type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = reviews[position]
        when (getItemViewType(position)) {
            HEADER -> {
                val header = holder as HeaderViewHolder
                header.number.text = "(${maxReviews})"
            }
            REVIEW_WITHOUT_IMAGE -> {
                val simpleHolder = holder as SimpleViewHolder
                val parser = SimpleDateFormat("yyyy-MM-dd")
                val date = parser.parse(item.date!!)
                val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.CANADA_FRENCH)
                simpleHolder.username.text =
                    "${item.creator!!.first_name.toString()} ${item.creator!!.last_name}"
                simpleHolder.date.text = formatter.format(date)
                simpleHolder.comment.text = item.comment
                simpleHolder.rate.rating = item.stars?.toFloat()!!
            }
            REVIEW_WITH_IMAGE -> {
                val imageHolder = holder as ImageViewHolder
                val parser = SimpleDateFormat("yyyy-MM-dd")
                val date = parser.parse(item.date!!)
                val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.CANADA_FRENCH)
                imageHolder.username.text =
                    "${item.creator!!.first_name.toString()} ${item.creator!!.last_name}"
                imageHolder.date.text = formatter.format(date)
                imageHolder.comment.text = item.comment
                imageHolder.rate.rating = item.stars?.toFloat()!!
                Picasso.with(context)
                    .load(item.image)
                    .transform(
                        RoundedCornersTransformation(
                            context.resources.getDimension(R.dimen.image_corner_radius).toInt(),
                            0
                        )
                    )
                    .fit()
                    .into(imageHolder.image_layout.image, object : Callback {
                        override fun onSuccess() {
                            imageHolder.image_layout.progress.visibility = View.GONE
                        }

                        override fun onError() {
                            Toast.makeText(
                                context,
                                "Can't load the comment picture",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
            }
        }
    }

    override fun getItemCount(): Int = reviews.size


    override fun getItemViewType(position: Int): Int {
        return if (position == 0)
            HEADER
        else if (position == reviews.size - 1 && isLoadingAdded)
            LOADER
        else if (reviews[position].image == null)
            REVIEW_WITHOUT_IMAGE
        else
            REVIEW_WITH_IMAGE
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

    inner class HeaderViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val number = mView.review_number
    }

    inner class SimpleViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val username = mView.item_content.fullname
        val date = mView.item_content.date
        val comment = mView.item_content.comment
        val rate = mView.item_content.rate
    }

    inner class ImageViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val username = mView.item_content.fullname
        val date = mView.item_content.date
        val comment = mView.item_content.comment
        val rate = mView.item_content.rate
        val image_layout = mView.item_content.image_layout
    }

    inner class LoaderViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
    }

}
