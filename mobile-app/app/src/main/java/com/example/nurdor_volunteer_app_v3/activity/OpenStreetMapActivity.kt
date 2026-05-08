package com.example.nurdor_volunteer_app_v3.activity

import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nurdor_volunteer_app_v3.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class OpenStreetMapActivity : AppCompatActivity() {

    private lateinit var marker: Marker
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.rgb(0, 191, 51)))
        setContentView(R.layout.activity_open_street_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val leftPadding = maxOf(bars.left, cutout.left)
            val rightPadding = maxOf(bars.right, cutout.right)
            v.setPadding(leftPadding, bars.top, rightPadding, bars.bottom)
            insets
        }

        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        val toolbar: MaterialToolbar = findViewById(R.id.toolbarMap)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        var pinTitle = "PMF Novi Sad"
        val startingPoint = savedInstanceState?.let {
            GeoPoint(
                it.getDouble("latitude"),
                it.getDouble("longitude")
            )
        } ?: if(intent.extras?.isEmpty == false) {
            pinTitle = intent.getStringExtra("title") ?: pinTitle
            GeoPoint(
                intent.getDoubleExtra("latitude", 0.0),
                intent.getDoubleExtra("longitude", 0.0)
            )
        } else { GeoPoint(45.2454010, 19.8524717) }

        mapView = findViewById(R.id.osmMapView)
        mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)
            controller.zoomTo(savedInstanceState?.getDouble("zoom") ?: 15.0)
            controller.animateTo(startingPoint)
            Log.i("pinOsm", "${startingPoint.latitude} ${startingPoint.longitude}")
        }

        marker = Marker(mapView)
        marker.apply {
            title = savedInstanceState?.getString("title") ?: pinTitle
            position = startingPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            isDraggable = true
            setOnMarkerDragListener(object: Marker.OnMarkerDragListener {
                override fun onMarkerDrag(marker: Marker?) { }
                override fun onMarkerDragEnd(marker: Marker?) { doOnMarkerDragEnd(marker) }
                override fun onMarkerDragStart(marker: Marker?) { }
            })
        }
        mapView.overlays.add(marker)

        val fabFinish: FloatingActionButton = findViewById(R.id.fabFinish)
        fabFinish.setOnClickListener {
            val result = Intent().apply {
                putExtra("latitude", marker.position.latitude)
                putExtra("longitude", marker.position.longitude)
                putExtra("title", marker.title)
            }
            setResult(RESULT_OK, result)
            finish()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("latitude", marker.position.latitude)
        outState.putDouble("longitude", marker.position.longitude)
        outState.putString("title", marker.title)
        outState.putDouble("zoom", mapView.zoomLevelDouble)
    }

    private fun doOnMarkerDragEnd(marker: Marker?) {
        marker?.let {
            val latitude = marker.position.latitude
            val longitude = marker.position.longitude
            val geocoder = Geocoder(this@OpenStreetMapActivity, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(p0: List<Address?>) {
                        updateMarkerTitle(marker, p0)
                    }
                    override fun onError(errorMessage: String?) {
                        super.onError("ERROR: during pin dragging: $errorMessage")
                    }
                })
            } else {
                Thread {
                    try {
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        updateMarkerTitle(marker, addresses)
                    } catch (e: Exception) {
                        Log.i("pinOsm", "EXCEPTION: during pin dragging: ${e.message}")
                    }
                }.start()
            }
        }
    }

    private fun updateMarkerTitle(marker: Marker, addresses: List<Address?>?) {
        val title = addresses?.firstOrNull()?.getAddressLine(0)
        runOnUiThread {
            Log.i("pinOsm", "Success: $title")
            marker.title = title ?: ""
        }
    }
}