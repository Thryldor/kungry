package ca.ulaval.ima.mp.ui.restaurant

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import androidx.core.content.ContextCompat
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.RestaurantLight
import ca.ulaval.ima.mp.api.model.RestaurantsSearchRequest
import java.lang.Exception

class RestaurantListFragment : Fragment() {

    private val LOCATION_FINE_CODE = 123;

    private var listener: OnRestaurantListener? = null
    private lateinit var mLocation: LocationManager
    private lateinit var currentLocation: Location

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.restaurant_list_fragment, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()

        if (view is RecyclerView) {
            getLocation()
            with(view) {
                layoutManager = LinearLayoutManager(context)
                APIService.searchRestaurant(
                    RestaurantsSearchRequest(
                        latitude = currentLocation.latitude,
                        longitude = currentLocation.longitude,
                        page = 1,
                        page_size = 5,
                        radius = 20,
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

    fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocation = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
       //     mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, locationListener)
            currentLocation = mLocation.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        } else {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissions, LOCATION_FINE_CODE)
        }
    }

    /*private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }*/

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
