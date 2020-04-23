package ca.ulaval.ima.mp.ui.review.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.tools.PaginationScrollListener
import kotlinx.android.synthetic.main.review_list_fragment.view.*


class ReviewListFragment : Fragment() {

    private lateinit var controller: ReviewListFragmentController
    private lateinit var reviewAdapter: ReviewRecyclerViewAdapter

    companion object {
        fun newInstance(): ReviewListFragment {
            return ReviewListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review_list_fragment, container, false)
        reviewAdapter = ReviewRecyclerViewAdapter()
        if (view.recycler is RecyclerView) {
            with(view.recycler) {
                val listener = controller.getPaginationScrollListener(reviewAdapter)
                val manager = LinearLayoutManager(context)
                listener.layoutManager = manager
                addOnScrollListener(listener)
                layoutManager = manager
                adapter = reviewAdapter
            }
        }
        controller.initList(reviewAdapter)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ReviewListFragmentController) {
            controller = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    interface ReviewListFragmentController {
        fun getPaginationScrollListener(adapter: ReviewRecyclerViewAdapter): PaginationScrollListener
        fun initList(adapter: ReviewRecyclerViewAdapter)
    }
}
