package ca.ulaval.ima.mp.ui.restaurant

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.MiniProject
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.PaginationResult
import ca.ulaval.ima.mp.api.model.RestaurantLight
import ca.ulaval.ima.mp.api.model.RestaurantsSearchRequest
import ca.ulaval.ima.mp.tools.PaginationScrollListener
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.restaurant_list_fragment.*
import kotlinx.android.synthetic.main.review_list_fragment.view.*

class RestaurantListFragment : Fragment() {

    private val LOCATION_FINE_CODE = 123;

    private var currentLocation: LatLng? = null
    private var isRestaurantLoading = false
    private var currentResult: PaginationResult<RestaurantLight>? = null

    private lateinit var activityCtx: Context
    private lateinit var controller: RestaurantListFragmentController
    private lateinit var restaurantAdapter: RestaurantRecyclerViewAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.restaurant_list_fragment, container, false)
        restaurantAdapter = RestaurantRecyclerViewAdapter(activityCtx, controller)
        if (view.recycler is RecyclerView) {
            with(view.recycler) {
                val listener = getPaginationScrollListener()
                val manager = LinearLayoutManager(context)
                listener.layoutManager = manager
                addOnScrollListener(listener)
                layoutManager = manager
                adapter = restaurantAdapter
            }
        }
        initList()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RestaurantListFragmentController) {
            activityCtx = context
            controller = context
        } else {
            throw RuntimeException("$context must implement RestaurantListFragmentController")
        }
    }

    private fun initList() {
        if (!getLocation() || currentLocation == null)
            return
        APIService.searchRestaurant(
            RestaurantsSearchRequest(
                latitude = currentLocation!!.latitude,
                longitude = currentLocation!!.longitude,
                page = 1,
                page_size = 5,
                radius = 20,
                text = ""
            ), createHandler { resp ->
                try {
                    val result = resp.getResult();
                    progress.visibility = View.GONE
                    restaurantAdapter.addAll(result.results)
                    currentResult = result
                    if (result.next != null) restaurantAdapter.addLoadingFooter()
                } catch (e: APIService.CallFailureException) {
                    Toast.makeText(
                        MiniProject.appContext,
                        e.wrapper?.error?.display,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

    }


    private fun fetchLocation(location: Location?) {
        if (location != null)
            currentLocation = LatLng(location.latitude, location.longitude)
        // TODO COMMENTER AU RENDU
        currentLocation = LatLng(46.829853, -71.254028)
    }

    private fun getLocation(): Boolean {
        if (ContextCompat.checkSelfPermission(
                activityCtx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location =
                (activityCtx.getSystemService(Context.LOCATION_SERVICE) as LocationManager).getLastKnownLocation(
                    LocationManager.GPS_PROVIDER
                )
            fetchLocation(location)
            return true
        } else {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(
                activityCtx as Activity,
                permissions,
                LOCATION_FINE_CODE
            )
            return false
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_FINE_CODE) {
            return
        }

        if (ContextCompat.checkSelfPermission(
                activityCtx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location =
                (activityCtx.getSystemService(Context.LOCATION_SERVICE) as LocationManager).getLastKnownLocation(
                    LocationManager.GPS_PROVIDER
                )
            fetchLocation(location)
        }
    }


    private fun getPaginationScrollListener(): PaginationScrollListener {
        return object : PaginationScrollListener() {

            override val currentPageCount: Int
                get() = currentResult?.results?.size ?: 0

            override val isLastPage: Boolean
                get() = currentResult != null && currentResult!!.next == null

            override val isLoading: Boolean
                get() = isRestaurantLoading

            override fun loadMoreItems() {
                if (currentResult == null || currentLocation == null)
                    return
                isRestaurantLoading = true

                APIService.searchRestaurant(
                    RestaurantsSearchRequest(
                        latitude = currentLocation!!.latitude,
                        longitude = currentLocation!!.longitude,
                        page = currentResult!!.next,
                        page_size = 5,
                        radius = 20,
                        text = ""
                    ), createHandler { resp ->
                        try {
                            val result = resp.getResult();
                            progress.visibility = View.GONE

                            restaurantAdapter.removeLoadingFooter()
                            isRestaurantLoading = false

                            restaurantAdapter.addAll(result.results)
                            currentResult = result
                            if (result.next != null) restaurantAdapter.addLoadingFooter()

                        } catch (e: APIService.CallFailureException) {
                            Toast.makeText(
                                MiniProject.appContext,
                                e.wrapper?.error?.display,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
    }

    interface RestaurantListFragmentController {
        fun onRestaurantClick(restaurant: RestaurantLight)
    }
}
