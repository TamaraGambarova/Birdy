package com.example.birdyapp.features.searching_by_name.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.google.android.material.textfield.TextInputLayout
import com.google.protobuf.ByteString
import com.theartofdev.edmodo.cropper.CropImage
import io.grpc.Channel
import kotlinx.android.synthetic.main.fragment_find_bird_by_name.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.Exception

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

    private lateinit var birdImageFile: File

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
            Log.d("FINDBIRD", birdNameLayout.editText?.text.toString())
            //birdName.value?.let { name -> Repository(channel).findBirdByName(name) }
            if (validate(birdNameLayout.editText?.text.toString())) {
                try {
                    Repository(channel).findBirdByName(birdNameLayout.editText?.text.toString())
                } catch (e: Exception) {
                    toastManager.long("Something went wrong, try again")
                }
            } else {
                toastManager.long("Incorrect bird name, try again")
            }
            Log.d("FINDBIRD", "ready!")
        }
        uploadBtn.setOnClickListener {
            Log.d("test", "taking photo")

            cameraPermission.check(
                requireActivity(),
                this::toCapture
            ) { toastManager.short(R.string.grant_camera_permission) }
        }
    }

    private fun toCapture() {
        birdImageFile = createImageFile()

        val pictureIntent = Intent(
            MediaStore.ACTION_IMAGE_CAPTURE
        )
        if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            //Create a file to store the image
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "com.example.birdyapp.provider",
                birdImageFile
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
        if (requestCode == 404 && resultCode == Activity.RESULT_OK) {
            Log.d("testPh", birdImageFile.name)
            //ImageViewUtil.loadImageFromFile(testImg, avatarFile)
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "com.example.birdyapp.provider",
                birdImageFile
            )
            launchImageCrop(photoURI)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                ImageViewUtil.loadImage(
                    testImg,
                    result.uri.toString(),
                    resources.getDrawable(R.drawable.background_button)
                )
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
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
                        var fis: FileInputStream? = null
                        try {
                            fis = FileInputStream(birdImageFile)
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }

                        val bm: Bitmap = BitmapFactory.decodeStream(fis)
                        val baos = ByteArrayOutputStream()
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val b: ByteArray = baos.toByteArray()
                        Repository(channel).setBirdLocation(
                            photo = ByteString.copyFrom(b),
                            lat = currentLocation.latitude,
                            long = currentLocation.longitude
                        )
                    }
                }

        }) {
            toastManager.short(R.string.grant_location_permission)
        }
    }

    private fun validate(value: String): Boolean {
        return value.filter { it in 'A'..'Z' || it in 'a'..'z' || it == '-' ||
                it == ' ' || it in 'А'..'Я' || it in 'а'..'я'}.length == value.length
    }

    companion object {
        fun getInstance(channel: Channel) = SearchBirdByNameFragment(channel)
    }

}