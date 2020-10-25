package com.example.birdyapp.features.searching_by_name.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import com.example.birdyapp.R
import com.example.birdyapp.Repository
import com.example.birdyapp.databinding.FragmentFindBirdByNameBinding
import com.example.birdyapp.util.ImageViewUtil
import com.example.birdyapp.util.PermissionManager
import com.example.birdyapp.util.ScopedFragment
import com.example.birdyapp.util.ToastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.theartofdev.edmodo.cropper.CropImage
import io.grpc.Channel
import kotlinx.android.synthetic.main.fragment_find_bird_by_name.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.io.File

class SearchBirdByNameFragment(val channel: Channel) : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val toastManager: ToastManager by instance()

    private val cameraPermission = PermissionManager(Manifest.permission.CAMERA, 404)
    private val fineLocationPermission =
        PermissionManager(Manifest.permission.ACCESS_FINE_LOCATION, 2)
    private val coarseLocationPermission =
        PermissionManager(Manifest.permission.ACCESS_COARSE_LOCATION, 3)

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var avatarFile: File

    val birdName = MutableLiveData<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentFindBirdByNameBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            initButtons()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initButtons() {
        searchBtn.setOnClickListener {
            Log.d("FINDBIRD", "search started!")
            Repository(channel).findBirdByName("вор")
            Log.d("FINDBIRD", "ready!")
        }
        takePhotoBtn.setOnClickListener {
            Log.d("test", "taking photo")

            cameraPermission.check(
                requireActivity(),
                this::toCapture
            ) { toastManager.short(R.string.grant_camera_permission) }
        }
    }

    private fun toCapture() {
        avatarFile = createImageFile()

        val pictureIntent = Intent(
            MediaStore.ACTION_IMAGE_CAPTURE
        )
        if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            //Create a file to store the image
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "com.example.birdyapp.provider",
                avatarFile
            )
            pictureIntent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                photoURI
            )
            startActivityForResult(
                pictureIntent,
                404
            )
        }
    }

    private fun createImageFile(): File {

        val imageFileName =
            "${"birdy_picture"}.${System.currentTimeMillis()}"
        val storageDir =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED) {
            toastManager.short(R.string.canceled)
        }
        if (requestCode == 404) {
            Log.d("testPh", avatarFile.name)
            //ImageViewUtil.loadImageFromFile(testImg, avatarFile)
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "com.example.birdyapp.provider",
                avatarFile
            )
            launchImageCrop(photoURI)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if(resultCode == Activity.RESULT_OK){
                ImageViewUtil.loadImage(testImg, result.uri.toString(), resources.getDrawable(R.drawable.background_button))

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Log.e("", "Crop error: ${result.error}")
            }
            getLastKnownLocation()
        }
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .start(requireContext(), this)

    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation() {
        fineLocationPermission.check(requireActivity(), {}) {
            toastManager.short(R.string.grant_location_permission)
        }
        coarseLocationPermission.check(requireActivity(), {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        currentLocation = location
                        Log.d("lat", currentLocation.latitude.toString())
                        Log.d("long", currentLocation.longitude.toString())
                    }
                }

        }) {
            toastManager.short(R.string.grant_location_permission)
        }
    }

    companion object {
        fun getInstance(channel: Channel) = SearchBirdByNameFragment(channel)
    }

}