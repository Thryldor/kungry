package ca.ulaval.ima.mp.ui.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ca.ulaval.ima.mp.R

class RestaurantInfoSelectFragment : Fragment() {

    companion object {
        fun newInstance() = RestaurantInfoSelectFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restaurant_info_select, container, false)
    }
}
