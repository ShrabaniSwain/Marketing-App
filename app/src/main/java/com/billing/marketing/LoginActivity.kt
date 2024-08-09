package com.billing.marketing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import com.billing.marketing.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val emailId = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            loginUser(emailId,password)
        }
        binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.toggleVisibilityButton.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.toggleVisibilityButton.setImageResource(R.drawable.baseline_visibility_24) // Change the icon to open eye
            } else {
                // Password is hidden
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.toggleVisibilityButton.setImageResource(R.drawable.baseline_visibility_off_24) // Change the icon to closed eye
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }
    }

    private fun loginUser(email_id: String, password: String) {
        val loginApiService = RetrofitClient.retrofit.create(MarketApi::class.java)
        val loginData = LoginData(email_id,password)
        val call = loginApiService.logIn(loginData)

        call.enqueue(object : Callback<LoginApiResponse> {
            override fun onResponse(call: Call<LoginApiResponse>, response: Response<LoginApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        if (it.isSuccess) {
                            binding.progressBar.visibility = View.GONE
                            val sharedPrefHelper = SharedPreferenceHelper(this@LoginActivity)
                            sharedPrefHelper.setLoggedIn(true)

                            sharedPrefHelper.saveLoginData(
                                it.id,
                                it.name,
                                it.mobile_number,
                                it.email_id,
                                it.Profile_image,
                                it.warehouse_id,
                                it.Password
                            )

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            binding.progressBar.visibility = View.GONE
                            showToast("Login Failed: ${it.message}")
                        }
                    }
                } else {
                    showToast("Login Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginApiResponse>, t: Throwable) {
                showToast("Login Failed: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}