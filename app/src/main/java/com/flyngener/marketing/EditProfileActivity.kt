package com.flyngener.marketing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.marketing.databinding.ActivityEditProfileBinding
import com.flyngener.marketing.databinding.ActivityMainBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    private val FILE_REQUEST_CODE = 100
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvChangeprofilePic.setOnClickListener {
            openGallery()
        }
        binding.ivEditProfileImage.setOnClickListener {
            openGallery()
        }
        binding.tvEdit.setOnClickListener {
            binding.tvSave.visibility = View.VISIBLE
            binding.tvEdit.visibility = View.GONE
            binding.tvChangeprofilePic.visibility = View.VISIBLE
            binding.tvToolbar.text = "Edit Profile"
            binding.etUsername.isFocusable = true
            binding.etUsername.isFocusableInTouchMode = true
            binding.etEmail.isFocusable = true
            binding.etEmail.isFocusableInTouchMode = true
            binding.etPassword.isFocusable = true
            binding.etPassword.isFocusableInTouchMode = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed to open the gallery
                    openGallery()
                } else {
                    // Permission denied, show a message or take appropriate action
                    Toast.makeText(this, "Permission denied. To use this feature, please grant the permission in App Settings.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openGallery() {
        // Check if permission is not granted
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 ){
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            }
            else {
                // Permission is already granted, open the gallery
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, FILE_REQUEST_CODE)
            }
        }
        else{
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                )
            }

            else {
                // Permission is already granted, open the gallery
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, FILE_REQUEST_CODE)
            }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri? = data?.data
            selectedImage?.let {
                imageUri = it
                Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.placeholderOf(R.drawable.profile_icon))
                    .into(binding.ivEditProfileImage)

            }
        }
    }
    private fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            cursor.moveToFirst()
            val imagePath = cursor.getString(column_index)
            cursor.close()
            return imagePath
        }
        return null
    }
}