package ca.ulaval.ima.mp.ui.review.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import ca.ulaval.ima.mp.api.model.RestaurantGetRequest
import ca.ulaval.ima.mp.api.model.RestaurantGetReviewsRequest
import ca.ulaval.ima.mp.api.model.Review
import ca.ulaval.ima.mp.tools.PaginationScrollListener
import ca.ulaval.ima.mp.ui.review.creation.ReviewCreationActivity
import ca.ulaval.ima.mp.ui.review.creation.ReviewCreationPopupFragment
import kotlinx.android.synthetic.main.action_bar.view.*
import kotlinx.android.synthetic.main.review_list_activity.*
import kotlinx.android.synthetic.main.review_list_fragment.*


class ReviewListActivity : AppCompatActivity() {

    companion object {
        val RESTAURANT_ID_KEY: String = "RESTAURANT_ID_KEY"
    }

    private var restaurantId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_list_activity)
        setToolbar()
        restaurantId = intent.getStringExtra(ReviewCreationActivity.RESTAURANT_ID_KEY)
            ?: throw RuntimeException("No restaurant ID passed to review list activity")
        APIService.getRestaurantById(
            RestaurantGetRequest(null, null, restaurantId?.toInt()!!),
            createHandler { resp ->
                try {
                    val res = resp.getResult()
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.container,
                            ReviewListFragment.newInstance(restaurantId!!, res.review_count!!)
                        )
                        .commitNow();
                } catch (e: APIService.CallFailureException) {
                    Toast.makeText(
                        MiniProject.appContext,
                        e.wrapper?.error?.display,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        supportFragmentManager.beginTransaction()
            .replace(R.id.popup, ReviewCreationPopupFragment.newInstance(restaurantId!!.toInt()))
            .commitNow();
    }

    private fun setToolbar() {
        setSupportActionBar(action_bar.toolbar as Toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("RES", "ON ACTIVITY REVIEWLIST: " + requestCode.toString() + " - " + resultCode.toString() + " - " + data.toString())
        if (requestCode == Activity.RESULT_OK) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.popup, ReviewCreationPopupFragment.newInstance(restaurantId!!.toInt()))
                .commitNow();
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
