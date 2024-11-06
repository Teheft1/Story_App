package com.teheft.storyapp.ui.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.teheft.storyapp.MainActivity
import com.teheft.storyapp.R
import com.teheft.storyapp.data.Result
import com.teheft.storyapp.data.pref.dataStore
import com.teheft.storyapp.databinding.ActivityUploadBinding
import com.teheft.storyapp.utils.UriToFile
import com.teheft.storyapp.utils.ViewModelFactory
import com.teheft.storyapp.utils.getImageUri
import com.teheft.storyapp.utils.reduceFileImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadActivity : AppCompatActivity() {

    private var currentImageUri: Uri? = null

    private lateinit var binding: ActivityUploadBinding

    private val uploadViewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnGallery.setOnClickListener{startGallery()}
        binding.btnCamera.setOnClickListener{startCamera()}
        binding.btnUpload.setOnClickListener{currentImageUri?.let { uri ->
                if(binding.edDescription.text.isNotEmpty()){
                    Log.d("uploadActivity", "$currentImageUri")
                    uploadImage(uri)
                }else{
                    Toast.makeText(this, "Please add your story description", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Please input or take a picture first", Toast.LENGTH_SHORT).show()
        }
        }
    }

    private fun uploadImage(uri: Uri) {
        if(uri != null){
            val imageFile = UriToFile(uri, this).reduceFileImage()
            val descriptionText = binding.edDescription.text.toString()

            val description = descriptionText.toRequestBody("plain/text".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            uploadViewModel.uploadStory(description,multipartBody).observe(this@UploadActivity){result ->
                Log.d("upload", "$result")
                when(result){
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d("uploadActivity", "result : ${result.error}")
                        AlertDialog.Builder(this@UploadActivity).apply {
                            setTitle("Upload Error")
                            setMessage(result.error.toString())
                            setPositiveButton("Tutup"){dialog,_ -> dialog.dismiss()}
                            create()
                            show()
                        }
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@UploadActivity, "Upload Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@UploadActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }else{
            Toast.makeText(this, "Please take a picture first or input it", Toast.LENGTH_SHORT).show()
        }

    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){isSuccess ->
        if (isSuccess){
            showImage()
        }else {
            currentImageUri = null
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){uri: Uri? ->
        if (uri != null){
            currentImageUri = uri
            showImage()
        }else{
            Log.d("photo Picker", "No Media Selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

}