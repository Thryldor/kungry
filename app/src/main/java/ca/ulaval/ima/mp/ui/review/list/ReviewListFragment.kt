package ca.ulaval.ima.mp.ui.review.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.MiniProject
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.PaginationResult
import ca.ulaval.ima.mp.api.model.RestaurantGetReviewsRequest
import ca.ulaval.ima.mp.api.model.Review
import ca.ulaval.ima.mp.tools.PaginationScrollListener
import kotlinx.android.synthetic.main.review_list_fragment.*
import kotlinx.android.synthetic.main.review_list_fragment.view.*


class ReviewListFragment : Fragment() {

    private var restaurantId: String? = null
    private var isReviewsLoading = false
    private var currentResult: PaginationResult<Review>? = null

    private lateinit var reviewAdapter: ReviewRecyclerViewAdapter

    companion object {
        fun newInstance(restaurantId: String, maxReviews: Int): ReviewListFragment = ReviewListFragment().apply {
            arguments = Bundle().apply {
                putInt("MAX_REVIEWS", maxReviews)
                putString("RESTAURANT_ID", restaurantId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review_list_fragment, container, false)
        restaurantId = arguments!!.getString("RESTAURANT_ID")
        reviewAdapter = ReviewRecyclerViewAdapter(activity!!, arguments!!.getInt("MAX_REVIEWS"))
        if (view.recycler is RecyclerView) {
            with(view.recycler) {
                val listener = getPaginationScrollListener(reviewAdapter)
                val manager = LinearLayoutManager(context)
                listener.layoutManager = manager
                addOnScrollListener(listener)
                layoutManager = manager
                adapter = reviewAdapter
            }
        }
        initList(reviewAdapter)
        return view
    }

    private fun getPaginationScrollListener(adapter: ReviewRecyclerViewAdapter): PaginationScrollListener {
        return object : PaginationScrollListener() {

            override val currentPageCount: Int
                get() = currentResult?.results?.size?:0

            override val isLastPage: Boolean
                get() = currentResult != null && currentResult!!.next == null

            override val isLoading: Boolean
                get() = isReviewsLoading

            override fun loadMoreItems() {
                if (currentResult == null)
                    return
                isReviewsLoading = true

                APIService.getRestaurantReviews(RestaurantGetReviewsRequest(
                    restaurantId?.toInt()!!,
                    currentResult!!.next,
                    5
                ), createHandler { resp ->
                    try {
                        val result = resp.getResult();
                        progress.visibility = View.GONE

                        adapter.removeLoadingFooter()
                        isReviewsLoading = false

                        adapter.addAll(result.results)
                        currentResult = result
                        if (result.next != null) adapter.addLoadingFooter()

                    } catch (e: APIService.CallFailureException) {
                        Toast.makeText(
                            MiniProject.appContext,
                            e.wrapper?.error?.display,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    fun initList(adapter: ReviewRecyclerViewAdapter) {
        APIService.getRestaurantReviews(RestaurantGetReviewsRequest(
            restaurantId?.toInt()!!,
            1,
            5
        ), createHandler { resp ->
            try {
                val result = resp.getResult();
                progress.visibility = View.GONE
                adapter.addAll(result.results)
                currentResult = result
                if (result.next != null) adapter.addLoadingFooter()
            } catch (e: APIService.CallFailureException) {
                Toast.makeText(
                    MiniProject.appContext,
                    e.wrapper?.error?.display,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}
