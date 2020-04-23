package ca.ulaval.ima.mp.ui.review.creation

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ca.ulaval.ima.mp.MiniProject
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.UploadService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.CreateReview
import ca.ulaval.ima.mp.api.model.Review
import ca.ulaval.ima.mp.api.model.Upload
import ca.ulaval.ima.mp.tools.RoundCornerDrawable
import kotlinx.android.synthetic.main.action_bar.view.*
import kotlinx.android.synthetic.main.review_creation_activity.*
import kotlin.math.roundToInt


class CreationActivity : AppCompatActivity() {

    companion object {
        val RESTAURANT_ID_KEY: String = "RESTAURANT_ID_KEY"
    }

    private var restaurantId: String? = null
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_creation_activity)
        setSupportActionBar(action_bar.toolbar as Toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (!APIService.logged)
            throw RuntimeException("Creation activity need an authenticated access")
        restaurantId = intent.getStringExtra(RESTAURANT_ID_KEY)
            ?: throw RuntimeException("No restaurant ID passed to review creation activity")
        submit.setOnClickListener {
            sendForm()
        }
        card_view.setOnClickListener {
            UploadService.choose(this)
        }
    }


    private fun sendForm() {
        val reqId = restaurantId!!.toInt()
        val reqRating = rate.rating.toInt()
        var reqComment: String? = comment.text.toString()
        if (reqComment!!.isEmpty())
            reqComment = null
        APIService.createReview(CreateReview(
            reqId,
            reqRating,
            reqComment
        ), createHandler { res ->
            handleFormResult(res)
        })
    }

    private fun handleFormResult(res: APIService.Result<Review>) {
        val review: Review
        try {
            review = res.getResult()
        } catch (e: APIService.CallFailureException) {
            Toast.makeText(
                MiniProject.appContext,
                "Review creation error : " + e.wrapper!!.error!!.display,
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (bitmap != null)
            attachBitmaps(review)
        else
            finish()
    }

    private fun attachBitmaps(review: Review) {
        APIService.attachImageToReview(Upload(
            review.id!!,
            bitmap!!
        ), createHandler { res ->
            try {
                res.getResult()
            } catch (e: APIService.CallFailureException) {
                Toast.makeText(
                    this,
                    "Image upload error : " + e.wrapper!!.error!!.display,
                    Toast.LENGTH_LONG
                ).show()
            }
            finish();
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bitmap = UploadService.extractBitmapFromResult(requestCode, resultCode, data)
        if (bitmap != null) {
            val round = RoundCornerDrawable(
                Bitmap.createScaledBitmap(
                    bitmap!!,
                    resources.getDimension(R.dimen.form_card_view_dim).roundToInt(),
                    resources.getDimension(R.dimen.form_card_view_dim).roundToInt(),
                    false
                ),
                resources.getDimension(R.dimen.image_corner_radius)
            )
            image_view.setImageDrawable(round)
        }
    }

}
