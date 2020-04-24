package ca.ulaval.ima.mp.ui.review.creation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.ui.review.list.ReviewListFragment
import kotlinx.android.synthetic.main.review_creation_popup_fragment.view.*

class ReviewCreationPopupFragment : Fragment() {

    companion object {

        val RESTAURANT_ID_KEY: String = "RESTAURANT_ID_KEY"

        fun newInstance(restaurantId: Int): ReviewCreationPopupFragment = ReviewCreationPopupFragment().apply {
            arguments = Bundle().apply {
                putInt(RESTAURANT_ID_KEY, restaurantId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review_creation_popup_fragment, container, false)
        val restaurantId = arguments!!.getInt(RESTAURANT_ID_KEY)
        view.button.setOnClickListener {
            val intent = Intent(activity, ReviewCreationActivity::class.java).apply {
                putExtra(ReviewCreationActivity.RESTAURANT_ID_KEY, restaurantId.toString())
            }
            startActivity(intent)
        }
        return view
    }
}
