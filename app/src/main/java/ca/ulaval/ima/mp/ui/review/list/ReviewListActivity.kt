package ca.ulaval.ima.mp.ui.review.list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ca.ulaval.ima.mp.MiniProject
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.PaginationResult
import ca.ulaval.ima.mp.api.model.RestaurantGetReviewsRequest
import ca.ulaval.ima.mp.api.model.Review
import ca.ulaval.ima.mp.tools.PaginationScrollListener
import ca.ulaval.ima.mp.ui.review.creation.ReviewCreationActivity
import kotlinx.android.synthetic.main.action_bar.view.*
import kotlinx.android.synthetic.main.review_list_activity.*
import kotlinx.android.synthetic.main.review_list_fragment.*


class ReviewListActivity : AppCompatActivity(),
    ReviewListFragment.ReviewListFragmentController {

    companion object {
        val RESTAURANT_ID_KEY: String = "RESTAURANT_ID_KEY"
    }

    private var restaurantId: String? = null
    private var isReviewsLoading = false
    private var currentResult: PaginationResult<Review>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_list_activity)
        setToolbar()
        restaurantId = intent.getStringExtra(ReviewCreationActivity.RESTAURANT_ID_KEY)
            ?: throw RuntimeException("No restaurant ID passed to review list activity")
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ReviewListFragment.newInstance())
            .commitNow();
    }

    private fun setToolbar() {
        setSupportActionBar(action_bar.toolbar as Toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun getPaginationScrollListener(adapter: ReviewRecyclerViewAdapter): PaginationScrollListener {
        return object : PaginationScrollListener() {

            override val currentPageCount: Int
                get() = currentResult?.results?.size?:0

            override val isLastPage: Boolean
                get() = currentResult != null && currentResult!!.next == null

            override val isLoading: Boolean
                get() = isReviewsLoading

            override fun loadMoreItems() {
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

    override fun initList(adapter: ReviewRecyclerViewAdapter) {
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
