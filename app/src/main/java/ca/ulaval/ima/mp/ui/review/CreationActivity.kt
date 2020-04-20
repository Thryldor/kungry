package ca.ulaval.ima.mp.ui.review

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.action_bar.view.*
import kotlinx.android.synthetic.main.evaluation_creation_activity.*


class CreationActivity : AppCompatActivity() {

    companion object {
        val RESTAURANT_ID_KEY: String = "RESTAURANT_ID_KEY"
    }

    private var restaurantId: String? = null
    private var bitmaps: ArrayList<Bitmap> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.evaluation_creation_activity)
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
        add_image.setOnClickListener {
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
            Toast.makeText(MiniProject.appContext, "Review creation error : " + e.wrapper!!.error!!.display, Toast.LENGTH_LONG).show()
            return
        }
        if (bitmaps.size > 0)
            attachBitmaps(review)
        finish()
    }

    private fun attachBitmaps(review: Review) {
        for (bitmap in bitmaps) {
            APIService.attachImageToReview(Upload(
                review.id!!,
                bitmap
            ), createHandler {res ->
                try {
                    res.getResult()
                } catch (e: APIService.CallFailureException) {
                    Toast.makeText(MiniProject.appContext,  "Image upload error : " + e.wrapper!!.error!!.display, Toast.LENGTH_LONG).show()
                }
            })
        }
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
        val bitmap = UploadService.extractBitmapFromResult(requestCode, resultCode, data)
        if (bitmap != null)
            bitmaps.add(bitmap)
    }

}
