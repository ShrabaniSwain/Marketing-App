package com.billing.marketing

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.billing.marketing.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefHelper = SharedPreferenceHelper(this)
        if (sharedPrefHelper.isLoggedIn()) {

            Constant.customer_id = sharedPrefHelper.getCustomerId(this)
            Constant.customer_name = sharedPrefHelper.getCustomerName(this)
            Constant.customer_mobilenumber = sharedPrefHelper.getCustomerMobileNumber(this)
            Constant.customer_EmialId = sharedPrefHelper.getCustomerEmailId(this)
            Constant.customer_Code = sharedPrefHelper.getCustomerCode(this)
            Constant.customer_ProfileImage = sharedPrefHelper.getCustomerProfleiImage(this)
            Constant.customer_wirehouse_id = sharedPrefHelper.getCustomerWirehouse_id(this)

            loadMainActivityWithDelay()
        }
        else {
            loadSignUpActivityWithDelay()
        }
    }

    private fun loadMainActivityWithDelay() {
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun loadSignUpActivityWithDelay() {
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}