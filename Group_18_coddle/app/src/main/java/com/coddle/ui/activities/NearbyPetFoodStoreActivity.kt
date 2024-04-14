package com.coddle.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import com.coddle.databinding.ActivityNearbyPetFoodStoreBinding
import com.coddle.util.AppUtil.Companion.shortToast
import android.location.LocationManager
import android.os.Looper
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.util.*

class NearbyPetFoodStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNearbyPetFoodStoreBinding
    private var myUrl = "https://www.google.com/maps/search/pet+food+stores+near+"
    private var location = ""
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private lateinit var startResolutionLauncher: ActivityResultLauncher<IntentSenderRequest>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearbyPetFoodStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startResolutionLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                geLastKnownLocation()
            } else shortToast("Please turn on location")
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val it = p0.lastLocation
                val geocoder = Geocoder(this@NearbyPetFoodStoreActivity, Locale.getDefault())
                val loc = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                location = if (loc != null && loc.isNotEmpty())
                    loc[0].locality ?: "me"
                else
                    "me"
                loadWebView()
            }
        }


        geLastKnownLocation()

        binding.btnRetry.setOnClickListener { loadWebView() }

        binding.imgBack.setOnClickListener { onBackPressed() }


    }

    @SuppressLint("MissingPermission")
    private fun geLastKnownLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location == null) {
                if (!isLocationEnabled()) {
                    showDialog()
                } else {
                    fusedLocationProviderClient.requestLocationUpdates(
                        LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                        locationCallback!!,
                        Looper.myLooper()!!
                    )
                }
            } else {
                val geocoder = Geocoder(this@NearbyPetFoodStoreActivity, Locale.getDefault())
                val loc = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                this.location = if (loc != null && loc.isNotEmpty())
                    loc[0].locality ?: "me"
                else
                    "me"
                loadWebView()
            }
        }
    }

    private fun showDialog() {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder()
            .addAllLocationRequests(listOf(locationRequest))

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnFailureListener {
            try {
                val resolvable = it as ResolvableApiException
                startResolutionLauncher.launch(
                    IntentSenderRequest.Builder(resolvable.resolution).build()
                )
            } catch (e: Exception) {
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView() {
        binding.progressCircular.visibility = View.VISIBLE
        binding.btnRetry.visibility = View.INVISIBLE
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            loadUrl(myUrl + location)
            webViewClient = object : WebViewClient() {
                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    binding.webView.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.INVISIBLE
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    binding.webView.visibility = View.INVISIBLE
                    binding.progressCircular.visibility = View.INVISIBLE
                    binding.btnRetry.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback!!)
    }
}