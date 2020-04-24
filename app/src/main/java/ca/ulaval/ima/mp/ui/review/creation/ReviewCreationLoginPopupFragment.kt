package ca.ulaval.ima.mp.ui.review.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.ui.review.list.ReviewListFragment
import kotlinx.android.synthetic.main.review_creation_popup_fragment.view.*

class ReviewCreationLoginPopupFragment : Fragment() {

    companion object {

        fun newInstance(): ReviewCreationLoginPopupFragment = ReviewCreationLoginPopupFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review_creation_login_popup_fragment, container, false)
        view.button.setOnClickListener {
            // TODO CALL LOGIN
        }
        return view
    }
}
