package com.flyngener.marketing

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flyngener.marketing.databinding.ActivityMainBinding
import com.flyngener.marketing.databinding.HeaderLayoutBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var headerBinding: HeaderLayoutBinding
    private lateinit var enquireListingAdapter: MarketDetailsAdapter
    private var isLoggedOut = false
    private var saleItems: List<SaleItem> = mutableListOf()
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefHelper = SharedPreferenceHelper(this)
        if (sharedPrefHelper.isLoggedIn()) {
            Constant.customer_id = sharedPrefHelper.getCustomerId(this)
            Constant.customer_name = sharedPrefHelper.getCustomerName(this)
            Constant.customer_mobilenumber = sharedPrefHelper.getCustomerMobileNumber(this)
            Constant.customer_EmialId = sharedPrefHelper.getCustomerEmailId(this)
            Constant.customer_ProfileImage = sharedPrefHelper.getCustomerProfleiImage(this)
            Constant.customer_wirehouse_id = sharedPrefHelper.getCustomerWirehouse_id(this)
        }

//        if (!sharedPrefHelper.isLoggedIn()) {
//            clearSessionData()
//        }

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
                R.id.nav_logout -> {
                    showLogoutConfirmationDialog()
                }
                R.id.nav_recharge_wallet -> {
                    share()
                }

            }

            true
        }

        binding.toolbar.ivShare.setOnClickListener{
            share()
        }
        headerBinding = HeaderLayoutBinding.bind(binding.navView.getHeaderView(0))
        headerBinding.tvProfileName.text = Constant.customer_name
        Glide.with(this)
            .load(Constant.customer_ProfileImage)
            .apply(RequestOptions.placeholderOf(R.drawable.profile_icon))
            .into(headerBinding.profileImageView)
        binding.toolbar.menuButton.setOnClickListener{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getDashboardBox(Constant.customer_wirehouse_id, Constant.customer_id)
            }

            if (response.isSuccessful) {
                val dashboardResponse = response.body()
                dashboardResponse?.let { handleDashboardResponse(it.result.firstOrNull()) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getSaleList(Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                val lotteryResponse = response.body()
                lotteryResponse?.let {
                    saleItems = it.result
                    enquireListingAdapter = MarketDetailsAdapter(applicationContext,saleItems)
                    binding.rvMarketDetails.adapter = enquireListingAdapter
                    binding.rvMarketDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = p0.toString()

                val filteredList = saleItems.filter { marketDetail ->
                    marketDetail.customer_name.contains(searchText, ignoreCase = true)
                }

                enquireListingAdapter.updateList(filteredList)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


        binding.toolbar.ivPresent.setOnClickListener { view ->
            val context = view.context
            val dialogBuilder = android.app.AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context).inflate(R.layout.dialog_attendance, null)
            dialogBuilder.setView(inflater)
            val etDate = inflater.findViewById<EditText>(R.id.etDate)
            val etTime = inflater.findViewById<TextView>(R.id.etTime)
            val btnSubmit = inflater.findViewById<Button>(R.id.btnSubmit)
            val progressBarDialog = inflater.findViewById<ProgressBar>(R.id.progressBar)
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
            etTime.text = currentTime

            val dialog = dialogBuilder.create()

            val calendar = Calendar.getInstance()

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            val editableDate = Editable.Factory.getInstance().newEditable(currentDate)
            etDate.text = editableDate
            dialog.show()

            val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val lastAttendanceDate = sharedPref.getString("lastAttendanceDate", "")
            if (lastAttendanceDate != currentDate) {
                binding.toolbar.ivPresent.visibility = View.VISIBLE
            }

            btnSubmit.setOnClickListener {
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setMessage("Are you sure you want to give attendance?")
                    alertDialogBuilder.setPositiveButton("OK") { dialog, which ->
                            progressBarDialog.visibility = View.VISIBLE
                            Handler().postDelayed({
                                val duePayment = Attendance(
                                    Constant.customer_wirehouse_id,
                                    Constant.customer_id,etDate.text.toString(),etTime.text.toString())
                                Log.i("TAG", "bind: " + duePayment)
                                val call = RetrofitClient.api.addAttendance(duePayment)
                                call.enqueue(object : Callback<ProfileDetailsResponse> {
                                    override fun onResponse(
                                        call: Call<ProfileDetailsResponse>,
                                        response: Response<ProfileDetailsResponse>
                                    ) {
                                        progressBarDialog.visibility = View.GONE
                                        Log.i("TAG", "onResponse: " + response.body())
                                        if (response.isSuccessful) {
                                            val updateProfileResponse = response.body()
                                            updateProfileResponse?.let {
                                                if (it.isSuccess) {
                                                    Toast.makeText(
                                                        context,
                                                        "Attendance send successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    with(sharedPref.edit()) {
                                                        putString("lastAttendanceDate", currentDate)
                                                        apply()
                                                    }
                                                    binding.toolbar.ivPresent.visibility = View.GONE
                                                    dialog.dismiss()
                                                    val intent = Intent(context, MainActivity::class.java)
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    context.startActivity(intent)
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "You have already submitted attendance for today.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        } else {
                                            Log.e("API", "API call failed with code ${response.code()}")
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<ProfileDetailsResponse>,
                                        t: Throwable
                                    ) {
                                        progressBarDialog.visibility = View.GONE
                                        Log.e("API", "API call failed with exception: ${t.message}")
                                        Toast.makeText(
                                            context,
                                            "Failed to send attendance. Please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                            }, 2000)
                    }
                    alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()

            }
        }

    }

    private fun handleEnquiryDetailsResponse(result: List<SaleItem>) {
        val enquireListingAdapter = MarketDetailsAdapter(applicationContext,result)
        binding.rvMarketDetails.adapter = enquireListingAdapter
        binding.rvMarketDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }

    private fun handleDashboardResponse(result: DashboardBoxData?) {
        val totalSaleAmount = result?.total_sale?.toDouble() ?: 0.0
        val totalReceivedAmount = result?.total_received?.toDouble() ?: 0.0
        val totalStockAmount = result?.total_stock?.toInt() ?: 0
        val totalStockReturnAmount = result?.total_stock_return?.toInt() ?: 0
        val totalDueAmount = result?.total_due?.toDouble() ?: 0.0

        val formattedTotalSaleAmount = String.format("%.2f", totalSaleAmount)
        val formattedTotalReceivedAmount = String.format("%.2f", totalReceivedAmount)
        val formattedTotalStockAmount = totalStockAmount
        val formattedTotalStockReturnAmount = totalStockReturnAmount
        val formattedTotalDueAmount = String.format("%.2f", totalDueAmount)

        binding.tvTotalSaleAmount.text = if (totalSaleAmount == 0.0) "₹0.0" else "₹$formattedTotalSaleAmount"
        binding.tvTotalReceivedAmount.text = if (totalReceivedAmount == 0.0) "₹0.0" else "₹$formattedTotalReceivedAmount"
        binding.tvTotalStockAmount.text = formattedTotalStockAmount.toString()
        binding.tvTotalStockReturnAmount.text = formattedTotalStockReturnAmount.toString()
        binding.tvTotalDueAmount.text = if (totalDueAmount == 0.0) "₹0.0" else "₹$formattedTotalDueAmount"

        if (totalStockReturnAmount != 0) {
            binding.stockReturnCard.setOnClickListener {
                val intent = Intent(this@MainActivity, StockReturnDetailsActivity::class.java)
                startActivity(intent)
            }
        }

        if (totalStockAmount != 0) {
            binding.stockCard.setOnClickListener {
                val intent = Intent(this@MainActivity, StockDetails::class.java)
                startActivity(intent)
            }
        }
        if (totalSaleAmount != 0.0) {
            binding.saleCard.setOnClickListener {
                val intent = Intent(this@MainActivity, SaleDeatilsActivity::class.java)
                startActivity(intent)
            }
        }
        if (totalReceivedAmount != 0.0) {
            binding.receivedCard.setOnClickListener {
                val intent = Intent(this@MainActivity, ReceivedDeatilsActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.logout_dialog_box, null)
        val dialogOk = dialogView.findViewById<TextView>(R.id.btnOk)
        val dialogCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogOk.setOnClickListener {
            clearSessionData()
            navigateToLoginScreen()
            alertDialog.dismiss()

            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
        }

        dialogCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun clearSessionData() {
        val sharedPrefHelper = SharedPreferenceHelper(this)
        sharedPrefHelper.clearSession()
    }

    private fun navigateToLoginScreen() {
        isLoggedOut = true
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (isLoggedOut) {
            navigateToLoginScreen()
        }
    }

    private fun share() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, "${resources?.getString(R.string.take_a_look_at_lakhpati)} ${Constant.REFER_URL}")
        applicationContext.startActivity(Intent.createChooser(share, null).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}