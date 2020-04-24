package ca.ulaval.ima.mp.ui.restaurant

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ca.ulaval.ima.mp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class RestaurantMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap

    private lateinit var mPin: Bitmap

    companion object {
        fun newInstance(latitude: String, longitude: String) = RestaurantMapFragment().apply {
            arguments = Bundle().apply {
                putString("latitude", latitude)
                putString("longitude",longitude)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_restaurant_map, container, false)

        mMapView = v.findViewById(R.id.restaurant_mapView)
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()

        val pin = context!!.assets.open("pin_selected.bmp")
        val selectPin = BitmapFactory.decodeStream(pin)
        mPin = Bitmap.createScaledBitmap(selectPin, 120, 150, false)

        mMapView.getMapAsync(this)
        return v
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))

        val latitude = arguments?.getString("latitude")!!.toDouble()
        val longitude = arguments?.getString("longitude")!!.toDouble()
        val latLng = LatLng(latitude, longitude)

        mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(mPin))
        )

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        mMap.animateCamera(cameraUpdate)
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

}
