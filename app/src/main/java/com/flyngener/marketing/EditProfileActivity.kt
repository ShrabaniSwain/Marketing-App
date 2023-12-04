package com.flyngener.marketing

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.marketing.databinding.ActivityEditProfileBinding
import com.flyngener.marketing.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

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
            binding.ivEditProfileImage.isClickable = true
            binding.ivEditProfileImage.isFocusable = true

            binding.ivEditProfileImage.setOnClickListener {
                openGallery()
            }
        }
        binding.tvProfileName.text = Constant.customer_name
        val editableText = Editable.Factory.getInstance().newEditable(Constant.customer_name)
        val editableEmail = Editable.Factory.getInstance().newEditable(Constant.customer_EmialId)
        val editablePassword = Editable.Factory.getInstance().newEditable(Constant.customer_Code)
        binding.etUsername.text = editableText
        binding.etEmail.text = editableEmail
        binding.etPassword.text = editablePassword
        Glide.with(this)
            .load(Constant.customer_ProfileImage)
            .apply(RequestOptions.placeholderOf(R.drawable.profile_icon))
            .into(binding.ivEditProfileImage)

        binding.tvSave.setOnClickListener {
            val customerId = Constant.customer_id
            val userName = binding.etUsername.text.toString()
            val etEmail = binding.etEmail.text.toString()
            val etPassword = binding.etPassword.text.toString()

            // Check if any of the fields is empty
            if (userName.isEmpty() || etEmail.isEmpty() || etPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isProfilePhotoUpdated = ::imageUri.isInitialized
            val isProfileDetailsUpdated = isProfileDetailsUpdated(userName, etEmail, etPassword)

            if (isProfilePhotoUpdated || isProfileDetailsUpdated) {
                if (isProfilePhotoUpdated) {
                    uploadProfileImage(customerId, imageUri)
                    val sharedPrefHelper = SharedPreferenceHelper(this)
                    sharedPrefHelper.profileImage(imageUri.toString())

//                    val resultIntent = Intent()
//                    setResult(Activity.RESULT_OK, resultIntent)
                }
                binding.progressBar.visibility = View.VISIBLE
                updateProfileDetails(userName, etEmail, etPassword)
                val sharedPrefHelper = SharedPreferenceHelper(this)
                sharedPrefHelper.saveCustomerMpin(userName, etEmail, etPassword)

                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)

                val intent =
                    Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(applicationContext, "Profile details updated successfully!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show()
            }
        }



    }
    private fun isProfileDetailsUpdated(userName: String, etEmail: String, etPassword: String): Boolean {
        return userName != Constant.customer_name || etEmail != Constant.customer_EmialId || etPassword != Constant.customer_Code
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

    private fun uploadProfileImage(customerId: String, imageUri: Uri?) {
        val customerId1: RequestBody = customerId.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val imageName = File(getPath(imageUri!!).toString()).name
        val imageFile = File(getPath(imageUri).toString())

        val requestFile: RequestBody = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("profile_image", imageFile.name, requestFile)

        val apiService = RetrofitClient.retrofit.create(MarketApi::class.java)

        binding.progressBar.visibility = View.VISIBLE
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            binding.progressBar.visibility = View.GONE
        }, 8000)

        val call = apiService.profileImage(customerId1, multipartBody)

        call.enqueue(object : Callback<ProfileImageResponse> {
            override fun onResponse(
                call: Call<ProfileImageResponse>,
                response: Response<ProfileImageResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.i("TAG", "onResponse: "+ response.body())
                    apiResponse?.let {
                        if (it.isSuccess) {
                            binding.progressBar.visibility = View.GONE
                            Constant.customer_ProfileImage = it.profile_image
//                            Toast.makeText(
//                                this@EditProfileActivity,
//                                "Profile pic upload successful!",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            val intent =
//                                Intent(applicationContext, MainActivity::class.java)
//                            startActivity(intent)
//                            finish()
                        } else {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "Profile pic upload failed: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Profile pic upload failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ProfileImageResponse>, t: Throwable) {
                Toast.makeText(
                    this@EditProfileActivity,
                    "Profile pic upload failed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun updateProfileDetails(etUsername: String, emailId:String, password:String) {
        val customerId = Constant.customer_id
        val updateProfile = ProfileDetailsModel(customerId,etUsername,emailId,password)

        val call = RetrofitClient.api.updateProfile(updateProfile)
        call.enqueue(object : Callback<ProfileDetailsResponse> {
            override fun onResponse(call: Call<ProfileDetailsResponse>, response: Response<ProfileDetailsResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            binding.progressBar.visibility = View.GONE
                            binding.etUsername.text = Editable.Factory.getInstance().newEditable(etUsername)
                            binding.etEmail.text = Editable.Factory.getInstance().newEditable(emailId)
                            binding.etPassword.text = Editable.Factory.getInstance().newEditable(password)
                            Constant.customer_name = etUsername
                            Constant.customer_EmialId = emailId
                            Constant.customer_Code = password

//                            val intent =
//                                Intent(applicationContext, MainActivity::class.java)
//                            startActivity(intent)
//                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Failed to update profile details. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "Failed to update  profile details. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }



}