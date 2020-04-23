package ca.ulaval.ima.mp.ui.restaurant

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.RestaurantLight
import ca.ulaval.ima.mp.api.model.RestaurantsSearchRequest
import java.lang.Exception

class RestaurantListFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnRestaurantListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.restaurant_list_fragment, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                APIService.searchRestaurant(
                    RestaurantsSearchRequest(
                        latitude = 40.0,
                        longitude = -70.0,
                        page = 1,
                        page_size = 20,
                        radius = 5000,
                        text = ""), createHandler { result ->
                        try {
                            val list: ArrayList<RestaurantLight> = result.getResult().results
                            Log.d("MP", "RESULT: " + list.toString())
                            adapter = RestaurantRecyclerViewAdapter(list, listener)
                        }
                        catch (e: Exception) {
                            when(e) {
                                is APIService.CallFailureException -> {
                                    Log.d("MP", "Error: " + e.wrapper?.error?.display)
                                }
                            }
                            Log.d("MP", "Error : " + e.toString())
                        }
                    })
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRestaurantListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnRestaurantListener {
        fun onRestaurantClick(item: RestaurantLight?)
    }
}
