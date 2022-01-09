package com.example.tasktrack.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tasktrack.R
import com.example.tasktrack.constants.Constants.Companion.ACTION_PAUSE_SERVICE
import com.example.tasktrack.constants.Constants.Companion.ACTION_START_OR_RESUME_SERVICE
import com.example.tasktrack.constants.Constants.Companion.MAP_VIEW_BUNDLE_KEY
import com.example.tasktrack.constants.Constants.Companion.MAP_ZOOM
import com.example.tasktrack.constants.Constants.Companion.POLYLINE_COLOR
import com.example.tasktrack.constants.Constants.Companion.POLYLINE_WIDTH
import com.example.tasktrack.constants.Constants.Companion.REQUEST_CODE_LOCATION_PERMISSION
import com.example.tasktrack.constants.TrackingUtility
import com.example.tasktrack.database.Track
import com.example.tasktrack.databinding.FragmentTrackBinding
import com.example.tasktrack.services.TrackingService
import com.example.tasktrack.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Math.round
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

@AndroidEntryPoint
class TrackFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val mainViewModel: MainViewModel by viewModels()
    private var map: GoogleMap? = null

    //    private var pathPoints = mutableListOf<MutableList<LatLng>>()
    private var pathPoints = mutableListOf<MutableList<LatLng>>()
    private var isTracking = false
    private var curTimeInMillis = 0L
    lateinit var binding: FragmentTrackBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_track, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        val mapViewBundle = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        binding.mapView.onCreate(mapViewBundle)
        binding.btnToggleRun.setOnClickListener {
            //Toast.makeText(context,"yyyyyyyy",Toast.LENGTH_SHORT).show()
            toggleRun()
        }
        binding.btnFinishRun.setOnClickListener {
            zoomBoundTrack()
            SaveToDB()
        }
        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { updateTracking(it) })



        TrackingService.pathPoints.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(it, true)
            binding.tvTimer.text = formattedTime
        })
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addLatestPolyline() {
        // only add polyline if we have at least two elements in the last polyline
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && curTimeInMillis > 0L) {
            binding.btnToggleRun.text = getString(R.string.resume_text)
            binding.btnFinishRun.visibility = View.VISIBLE
        } else if (isTracking) {
            binding.btnToggleRun.text = getString(R.string.stop_text)
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    @SuppressLint("MissingPermission")
    private fun toggleRun() {
        if (isTracking) {
            pauseTrackingService()
        } else {
            startOrResumeTrackingService()
        }
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        binding.mapView?.onSaveInstanceState(mapViewBundle)
    }

    private fun pauseTrackingService() =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = ACTION_PAUSE_SERVICE
            requireContext().startService(it)
        }

    private fun startOrResumeTrackingService() =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = ACTION_START_OR_RESUME_SERVICE
            requireContext().startService(it)
        }

    private fun zoomBoundTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (point in polyline) {
                bounds.include(point)
            }
        }
        val width = binding.mapView.width
        val height = binding.mapView.height
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                width,
                height,
                (height * 0.05f).toInt()
            )
        )
    }

    private fun SaveToDB() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val timestamp = Calendar.getInstance().timeInMillis
            val track =
                Track(
                    0, bmp, timestamp, avgSpeed, distanceInMeters, curTimeInMillis
                )
            mainViewModel.insertTrack(track)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run saved successfully.",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun stopRun() {
        //Timber.d("STOPPING RUN")
        binding.tvTimer.text = "00:00:00:00"
        pauseTrackingService()
        findNavController().navigate(R.id.action_trackFragment2_to_historyFragment)
    }

    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permission to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setThemeResId(R.style.AlertDialogTheme).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}