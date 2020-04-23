package ca.ulaval.ima.mp.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.RestaurantLight
import ca.ulaval.ima.mp.api.model.RestaurantsSearchRequest
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {
    private val LOCATION_FINE_CODE = 123;

    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap

    private lateinit var mManager: FragmentManager
    private lateinit var mLocation: LocationManager

    private lateinit var mRestaurants: ArrayList<RestaurantLight>

    companion object {
        fun newInstance() = MapFragment()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.map_fragment, container, false)
        mMapView = v.findViewById(R.id.map)
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()

        mManager = activity!!.supportFragmentManager

        mManager.beginTransaction()
            .add(R.id.restaurant_info, RestaurantInfoFragment())
            .commit()

        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mLocation = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mMapView.getMapAsync(this)
        mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, this)
        return v
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))

        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissions, LOCATION_FINE_CODE)
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
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        }
    }

    fun refreshMapMarker() {
        for (restaurant in mRestaurants) {
            Log.d("APP", restaurant.toString())
/*
            val latLng = LatLng(restaurant.location?.get(0)!!.toDouble(), restaurant.location[1].toDouble())
            mMap.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                .position(latLng)
            )
*/
        }
    }

    fun fetchRestaurantsInProximity(latitude: Double, longitude: Double) {
        val searchRestaurantParam = RestaurantsSearchRequest(
            page = 1,
            page_size = 20,
            latitude = latitude,
            longitude = longitude,
            radius = 10,
            text = ""
        )

        APIService.searchRestaurant(searchRestaurantParam, createHandler { result ->
            Log.d("App", result.toString())
            try {
                mRestaurants = result.getResult().results
            } catch (e: APIService.CallFailureException) {

            }
/*
            mRestaurants = result.getResult().results
            refreshMapMarker()
*/
        })
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onLocationChanged(location: Location?) {
        // TODO Décommenter pour rendu
        // val latLng = LatLng(location!!.latitude, location.longitude)
        val latLng = LatLng(46.829853, -71.254028)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        mMap.animateCamera(cameraUpdate)
        mLocation.removeUpdates(this)
        fetchRestaurantsInProximity(46.829853, -71.254028)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        // TODO Not yet implement
        return false
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // TODO Not yet implement
    }

    override fun onProviderEnabled(provider: String?) {
        // TODO Not yet implement
    }

    override fun onProviderDisabled(provider: String?) {
        // TODO Not yet implement
    }

}
