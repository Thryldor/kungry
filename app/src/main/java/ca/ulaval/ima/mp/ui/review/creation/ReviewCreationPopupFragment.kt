package ca.ulaval.ima.mp.ui.review.creation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import kotlinx.android.synthetic.main.review_creation_popup_fragment.view.*


class ReviewCreationPopupFragment : Fragment() {

    companion object {

        val RESTAURANT_ID_KEY: String = "RESTAURANT_ID_KEY"

        fun newInstance(restaurantId: Int): ReviewCreationPopupFragment =
            ReviewCreationPopupFragment().apply {
                arguments = Bundle().apply {
                    putInt(RESTAURANT_ID_KEY, restaurantId)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View
        if (APIService.logged) {
            view = inflater.inflate(R.layout.review_creation_popup_fragment, container, false)
            bindViewToComment(view)
        } else {
            view = inflater.inflate(R.layout.review_creation_login_popup_fragment, container, false)
            bindViewToLogin(view)
        }
        return view
    }

    private fun bindViewToLogin(view: View) {
        view.button.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            activity!!.startActivityForResult(intent, LoginActivity.LOGIN_REQUEST)
        }

    }

    private fun bindViewToComment(view: View) {
        val restaurantId = arguments!!.getInt(RESTAURANT_ID_KEY)
        view.button.setOnClickListener {
            val intent = Intent(activity, ReviewCreationActivity::class.java).apply {
                putExtra(ReviewCreationActivity.RESTAURANT_ID_KEY, restaurantId.toString())
            }
            startActivity(intent)
        }

    }
}
