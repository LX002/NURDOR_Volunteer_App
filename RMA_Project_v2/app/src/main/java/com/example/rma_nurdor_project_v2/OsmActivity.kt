package com.example.rma_nurdor_project_v2

import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rma_nurdor_project_v2.utils.PreferenceHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration
import java.util.Locale

class OsmActivity : AppCompatActivity() {

    private lateinit var marker: Marker
    private var isFirstLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.osm_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        setStatusBarColor()

        val toolbar: MaterialToolbar = findViewById(R.id.toolbarMap)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val mapView: MapView = findViewById(R.id.osmMapView)
        val fabFinish: FloatingActionButton = findViewById(R.id.fabFinish)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(15.0)
        val startPoint = if(intent.hasExtra("latitude") && intent.hasExtra("longitude") && isFirstLaunch) {
            GeoPoint(intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0))
        } else {
            savedInstanceState?.let {
                GeoPoint(savedInstanceState.getDouble("aLatitude"),savedInstanceState.getDouble("aLongitude"))
            } ?: GeoPoint(45.2384459,19.8855424)
        }
        mapController.setCenter(startPoint)

        marker = Marker(mapView)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.isDraggable = true
        mapView.overlays.add(marker)

        fabFinish.setOnClickListener {
            val markerIntent = Intent()
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(marker.position.latitude, marker.position.longitude, 1)
            marker.title = if(!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                null
            }
            markerIntent.putExtra("latitude", marker.position.latitude)
            markerIntent.putExtra("longitude", marker.position.longitude)
            markerIntent.putExtra("title", marker.title)
            setResult(RESULT_OK, markerIntent)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isFirstLaunch = false
        Log.i("isFirstLaunchTracker", "$isFirstLaunch")
        outState.putDouble("aLatitude", marker.position.latitude)
        outState.putDouble("aLongitude", marker.position.longitude)
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 (API 31) and above
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            window.statusBarColor = ContextCompat.getColor(window.context, R.color.nurdor_green_2)
        } else {
            window.statusBarColor = ContextCompat.getColor(window.context, R.color.nurdor_green_2)
        }
    }
}