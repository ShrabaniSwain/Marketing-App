package com.flyngener.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.flyngener.marketing.databinding.ActivityAddItemSaleBinding
import com.flyngener.marketing.databinding.ActivityReturnBinding

class AddItemSaleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemSaleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}