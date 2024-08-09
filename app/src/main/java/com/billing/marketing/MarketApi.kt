package com.billing.marketing

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface MarketApi {

    @POST("login.php")
    fun logIn(
        @Body loginData: LoginData,
    ): Call<LoginApiResponse>

    @Multipart
    @POST("update_profile_image.php")
    fun profileImage(
        @Part("sales_executive_id") sales_executive_id: RequestBody,
        @Part profile_image: MultipartBody.Part,
    ): Call<ProfileImageResponse>

    @POST("update_profile.php")
    fun updateProfile(
        @Body profileDetailsModel: ProfileDetailsModel,
    ): Call<ProfileDetailsResponse>

    @POST("add_customer.php")
    fun addcustomer(
        @Body customerDetails: CustomerDetails,
    ): Call<ProfileDetailsResponse>
    @POST("add_enquiry.php")
    fun addEnquiry(
        @Body enquiryDetails: EnquiryDetails,
    ): Call<ProfileDetailsResponse>

    @GET("fetch_customer.php")
    suspend fun getCustomerDetails(@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<CustomerDetailsResponse>

    @GET("fetch_enquiry_list.php")
    suspend fun getEnquiryDetails(@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<EnquiryDetailsResponse>

    @GET("fetch_product.php")
    suspend fun getProductItem(@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<ProductDetailsResponse>

    @GET("dashboard.php")
    suspend fun getDashboardBox(@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<DashboardBoxResponse>
    @GET("dashboard_sale_list.php")
    suspend fun getSaleList(@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<SaleResponse>
    @GET("report_sale_list.php")
    suspend fun getSaleReport(@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<SaleReportResponse>
    @GET("report_stock.php")
    suspend fun getStockReport(@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<StockReportResponse>
    @GET("report_stock.php")
    suspend fun getStockDateChnage(@Query("warehouse_id") warehouse_id: String,
                                   @Query("sales_executive_id") sales_executive_id: String,
                                   @Query("from_date") from_date: String,
                                   @Query("to_date") to_date: String): Response<StockReportResponse>
    @GET("report_receive.php")
    suspend fun getReceivedReport(@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<ReceivedReportResponse>
    @GET("return_report.php")
    suspend fun getReturnReport(@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<ReturnResponse>
    @GET("report_due.php")
    suspend fun getDueReport(@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<DueResponse>
    @GET("product_sale_list.php")
    suspend fun getProductSaleList(@Query("sale_id") sale_id: String,@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<SaleProductResponse>
    @GET("delete_sale.php")
    suspend fun getDeleteSaleList(@Query("sale_id") sale_id: String,@Query("warehouse_id") warehouse_id: String, @Query("sales_executive_id") sales_executive_id: String): Response<ProfileDetailsResponse>
    @GET("invoice.php")
    fun getPrintSaleList(@Query("sale_id") sale_id: String):Call<ResponseBody>

    @POST("add_sale.php")
    fun sendSaleData(
        @Body saleRequest: SaleTableData,
    ): Call<ProfileDetailsResponse>
    @POST("return_product.php")
    fun sendReturnData(
        @Body returnRequest: ReturnRequest,
    ): Call<ProfileDetailsResponse>
    @POST("pay_sale_due.php")
    fun paySaleDue(
        @Body paymentDueData: PaymentDueData,
    ): Call<ProfileDetailsResponse>
    @POST("add_attendance.php")
    fun addAttendance(
        @Body attendence: Attendance,
    ): Call<ProfileDetailsResponse>
    @POST("edit_customer.php")
    fun editCustomerDetails(
        @Body editCustomerInfo: EditCustomerInfo,
    ): Call<ProfileDetailsResponse>
    @POST("edit_enquiry.php")
    fun editEnquiryDetails(
        @Body editEnquiryDetails: EditEnquiryDetails,
    ): Call<ProfileDetailsResponse>
    @POST("edit_sale.php")
    fun editSale(
        @Body editSaleModel: EditSaleModel,
    ): Call<ProfileDetailsResponse>



}