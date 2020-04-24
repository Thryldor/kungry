package ca.ulaval.ima.mp.ui.review.preview

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import ca.ulaval.ima.mp.MiniProject
import ca.ulaval.ima.mp.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

import kotlinx.android.synthetic.main.review_comment_image_activity.*
import kotlinx.android.synthetic.main.review_list_item_image_fragment.view.*

class ReviewCommentImageActivity : AppCompatActivity() {

    companion object {
        val IMAGE_URL_KEY: String = "IMAGE_URL_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_comment_image_activity)
        val imageUrl = intent.getStringExtra(IMAGE_URL_KEY)
            ?: throw RuntimeException("No restaurant ID passed to review creation activity")
        Picasso.with(this)
            .load(imageUrl)
            .into(image, object : Callback {
                override fun onSuccess() {
                    progress.visibility = View.GONE
                }

                override fun onError() {
                    Toast.makeText(
                        parent,
                        "Can't load the comment picture",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        back_button.setOnClickListener { finish() }
    }

}
