package ca.ulaval.ima.mp.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.RestaurantLight
import ca.ulaval.ima.mp.api.model.RestaurantsSearchRequest
import ca.ulaval.ima.mp.ui.restaurant.RestaurantMapFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {
    private val LOCATION_FINE_CODE = 123;

    private lateinit var mLayoutInfo: FrameLayout

    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap

    private lateinit var mManager: FragmentManager
    private lateinit var mLocation: LocationManager

    private var mSelectedRestaurant: RestaurantLight? = null
    private var mRestaurants: ArrayList<RestaurantLight> = ArrayList()

    private var mMarker: Marker? = null
    private lateinit var mPin: Bitmap
    private lateinit var mSelectedPin: Bitmap

    companion object {
        fun newInstance() = MapFragment()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.map_fragment, container, false)
        mLayoutInfo = v.findViewById(R.id.fragment_container)
        setupLayoutClickable()
        mMapView = v.findViewById(R.id.map)
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()

        val pin = context!!.assets.open("pin.bmp")
        val pinBitmap = BitmapFactory.decodeStream(pin)
        mPin = Bitmap.createScaledBitmap(pinBitmap, 75, 90, false)

        val selectPin = context!!.assets.open("pin_selected.bmp")
        val selectPinBitmap = BitmapFactory.decodeStream(selectPin)
        mSelectedPin = Bitmap.createScaledBitmap(selectPinBitmap, 120, 150, false)

        mManager = activity!!.supportFragmentManager

        mManager.beginTransaction()
            .add(R.id.fragment_container, RestaurantInfoFragment())
            .commit()
        
        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView.getMapAsync(this)

        return v
    }

    private fun setupLayoutClickable() {
        mLayoutInfo.setOnClickListener {
            run {
                // TODO Changer de vue, le restaurant sélectionné est dispo dans mSelectedRestaurant
                if (mSelectedRestaurant != null) {
                    Log.d("APP", "Click sur le layout")
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
        mMap.setOnMarkerClickListener(this)

        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mLocation = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, this)

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
            mLocation = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, this)
        }
    }

    private fun updateRestaurantInfo(restaurantLight: RestaurantLight?) {
        val bundle = Bundle().apply {
            putString("title", restaurantLight?.name)
            putString("description", restaurantLight?.type)
            putString("imageLink", restaurantLight?.image)
            putString("distance", restaurantLight?.distance)
            putString("reviewAverage", restaurantLight?.review_average.toString())
            putInt("reviewCount", restaurantLight?.review_count!!)
        }

        val fragment = RestaurantInfoSelectFragment()
        fragment.arguments = bundle
        val transaction = mManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
        }
        transaction.commit()
    }

    private fun refreshMapMarker() {
        for (restaurant in mRestaurants) {
            val latLng = LatLng(restaurant.location!!.latitude!!, restaurant.location.longitude!!)

            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(mPin))
                    .title(restaurant.id.toString())
            )
        }
    }

    private fun fetchRestaurantsInProximity(latitude: Double, longitude: Double) {
        val searchRestaurantParam = RestaurantsSearchRequest(
            page = 1,
            page_size = 20,
            latitude = latitude,
            longitude = longitude,
            radius = 5,
            text = ""
        )

        APIService.searchRestaurant(searchRestaurantParam, createHandler { result ->
            try {
                mRestaurants = result.getResult().results
            } catch (e: APIService.CallFailureException) {
                e.printStackTrace()
            }
            refreshMapMarker()
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
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13f)
        mMap.animateCamera(cameraUpdate)
        mLocation.removeUpdates(this)
        fetchRestaurantsInProximity(46.829853, -71.254028)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        if (mMarker != null && p0!! != mMarker) {
            mMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(mPin))
            p0.setIcon(BitmapDescriptorFactory.fromBitmap(mSelectedPin))
            val restaurantLight = mRestaurants.find { restaurant -> restaurant.id.toString() == p0!!.title }
            mSelectedRestaurant = restaurantLight
            updateRestaurantInfo(restaurantLight)
            mMarker = p0
        } else {
            p0?.setIcon(BitmapDescriptorFactory.fromBitmap(mSelectedPin))
            val restaurantLight = mRestaurants.find { restaurant -> restaurant.id.toString() == p0!!.title }
            mSelectedRestaurant = restaurantLight
            updateRestaurantInfo(restaurantLight)
            mMarker = p0
        }
        return true
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

}
