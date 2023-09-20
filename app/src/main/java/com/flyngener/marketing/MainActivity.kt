package com.flyngener.marketing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityMainBinding
import com.flyngener.marketing.databinding.HeaderLayoutBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: HeaderLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvAddCustomer.setOnClickListener {
            val intent = Intent(this@MainActivity, CustomertListing::class.java)
            startActivity(intent)
        }

        binding.tvSales.setOnClickListener {
            val intent = Intent(this@MainActivity, SaleActivity::class.java)
            startActivity(intent)
        }

        binding.tvReturn.setOnClickListener {
            val intent = Intent(this@MainActivity, ReturnActivity::class.java)
            startActivity(intent)
        }

        binding.stockCard.setOnClickListener {
            val intent = Intent(this@MainActivity, StockDetails::class.java)
            startActivity(intent)
        }

        binding.saleCard.setOnClickListener {
            val intent = Intent(this@MainActivity, SaleDeatilsActivity::class.java)
            startActivity(intent)
        }

        binding.stockReturnCard.setOnClickListener {
            val intent = Intent(this@MainActivity, StockReturnDetailsActivity::class.java)
            startActivity(intent)
        }

        binding.receivedCard.setOnClickListener {
            val intent = Intent(this@MainActivity, ReceivedDeatilsActivity::class.java)
            startActivity(intent)
        }

        binding.reportsCard.setOnClickListener {
            val intent = Intent(this@MainActivity, ReportsActivity::class.java)
            startActivity(intent)
        }

        binding.tvAddSurvey.setOnClickListener {
            val intent = Intent(this@MainActivity, EnquiryListing::class.java)
            startActivity(intent)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            for (i in 0 until binding.navView.menu.size()) {
                val item = binding.navView.menu.getItem(i)
                item.isChecked = false
            }
            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()

            when (menuItem.itemId) {

                R.id.nav_transaction_history -> {
                    val intent = Intent(this@MainActivity, EditProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_recharge_wallet -> {

                }

            }

            true
        }

        headerBinding = HeaderLayoutBinding.bind(binding.navView.getHeaderView(0))

        binding.toolbar.menuButton.setOnClickListener{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        val marketDetailsAdapter = MarketDetailsAdapter(applicationContext)
        binding.rvMarketDetails.adapter = marketDetailsAdapter
        binding.rvMarketDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}