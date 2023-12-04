package com.flyngener.marketing


data class LoginData(val email_id: String, val password: String) : java.io.Serializable

data class LoginApiResponse(
    val id: String,
    val name: String,
    val mobile_number: String,
    val email_id: String,
    val code: String,
    val Profile_image: String,
    val warehouse_id: String,
    val Password: String,
    val message: String,
    val isSuccess: Boolean
)

data class ProfileImageResponse(
    val message: String?, val profile_image: String, val isSuccess: Boolean
)

data class ProfileDetailsModel(
    val sales_executive_id: String,
    val sales_executive_name: String,
    val email_id:String ,
    val login_password: String
)

data class ProfileDetailsResponse(
    val message: String?, val isSuccess: Boolean
)

data class CustomerDetails(
    val customer_id:String,
    val customer_name: String,
    val mobile_number: String,
    val gender: String,
    val email_id: String,
    val city: String,
    val customer_code: String,
    val pincode: String,
    val full_address: String,
    val warehouse_id: String,
    val sales_executive_id: String,
    val join_date: String,
    val due_balance: String,
    val add_date: String,
    val state: String
)

data class EnquiryDetails(
    val enquiry_id: String,
    val customer_name: String,
    val mobile_number: String,
    val location: String,
    val details: String,
    val warehouse_id: String,
    val sales_executive_id: String
)
data class EnquiryDetailsResponse(val result: List<EnquiryDetails>)

data class CustomerDetailsResponse(val result: List<CustomerDetails>)

data class ProductDetails(
    val product_id: String,
    val product_name: String,
    val product_category_name: String,
    val product_unit: String,
    val product_code: String,
    val product_model: String,
    val selling_price_per_unit: String,
    val sell_tax_type: String,
    val hsn: String,
    val tax_rate: String,
    val in_stock: String,
    val transfered: String,
    val stock_returned: String,
    val stock_out: String?
)

data class ProductDetailsResponse(val result: List<ProductDetails>)

data class DashboardBoxData(
    val total_sale: String,
    val total_received: String,
    val total_due: String,
    val total_stock: String,
    val total_stock_return: String
)

data class DashboardBoxResponse(val result: List<DashboardBoxData>)

data class SaleResponse(
    val result: List<SaleItem>
)

data class SaleItem(
    val sale_id: String,
    val customer_name: String,
    val invoice_no: String,
    val amount: String,
    val paid: String,
    val balance: String,
    val qnt: String,
    val sale_date:String,
    val mobile_number:String,
    val description:String,
    val payment_type:String,
    val state:String,
    val customer_id: String
    )

data class SaleTableData(
    val warehouse_id: String,
    val sales_executive_id: String,
    val customer_id: String,
    val customer_name: String,
    val mobile_number: String,
    val invoice_no: String,
    val tax_type: String,
    val sales_amount: String,
    val paid_amount: String,
    val due_amount: String,
    val state: String,
    val sale_date: String,
    val pay_method: String,
    val tax_amount: String,
    val description: String,
    val productArray: List<ProductData>
)

data class ProductData(
    val product_id: String,
    val details: String,
    val quantity: String,
    val unit: String,
    val sale_price: String,
    val dis: String,
    val dis_amt: String,
    val tax: String,
    val tax_amount2: String,
    val amount: String,
    val tax_type2: String
)

data class ReturnRequest(
    val warehouse_id: String,
    val sales_executive_id: String,
    val total_product: String,
    val return_date: String,
    val return_amount: String,
    val product_info: List<ProductInfo>,
)
data class ProductInfo(
    val product_id: String,
    val return_qty: String
)
data class PaymentDueData(
    val sale_id: String,
    val sales_executive_id: String,
    val pay_method: String,
    val paid_amount: String,
    val pay_date: String,
    val warehouse_id: String
)
data class Attendance(
    val warehouse_id: String,
    val sales_executive_id: String,
    val attend_date: String,
    val attendance_time: String
)
data class SaleReportItem(
    val customer_name: String,
    val invoice_no: String,
    val amount: String,
    val paid: String,
    val balance: String,
    val qnt: String,
    val date: String,
    val sale_id: String
)
data class SaleReportResponse(val result: List<SaleReportItem>)

data class StockReportItem(
    val product_name: String,
    val sale_price: String,
    val in_stock: String,
    val credit: String,
    val stock_returned: String,
    val sold: String
)

data class StockReportResponse(val result: List<StockReportItem>)
data class ReceivedReportItem(
    val customer_name: String,
    val invoice_no: String,
    val paid: String,
    val date: String,
    val sale_id: String
)

data class ReceivedReportResponse(val result: List<ReceivedReportItem>)

data class EditCustomerInfo(
    val customer_name: String,
    val mobile_number: String,
    val gender: String,
    val email_id: String,
    val city: String,
    val pincode: String,
    val full_address: String,
    val customer_id: String,
    val state: String
)
data class EditEnquiryDetails(
    val customer_name: String,
    val mobile_number: String,
    val location: String,
    val details: String,
    val enquiry_id: String
)

data class ReturnResponse(
    val result: List<ReturnItem>
)

data class ReturnItem(
    val status: String,
    val return_date: String,
    val return_id: String,
    val product_id: String,
    val product_name: String,
    val product_code: String,
    val product_model: String,
    val return_qty: String,
    val return_amount: String
)

data class DueItem(
    val customer_name: String,
    val invoice_no: String,
    val due_amount: String,
    val date: String,
    val sale_id: String,
    val paid_amount: String,
    val total_amount: String,
    val qty: String,
)

data class DueResponse(
    val result: List<DueItem>
)
data class SaleProductList(
    val product_id: String,
    val product_name: String,
    val product_code: String,
    val product_model: String,
    val hsn: String,
    val unit: String,
    val quantity: String,
    val sale_price: String,
    val dis_amt: String,
    val tax_amount: String,
    val amount: String,
    val tax_type: String,
    val dis_percentage: String,
    val tax_percentage: String,
)
data class SaleProductResponse(
    val result: List<SaleProductList>
)

data class EditSaleModel(
    val sale_id: String,
    val sale_date: String,
    val warehouse_id: String,
    val sales_executive_id: String,
    val customer_id: String,
    val customer_name: String,
    val mobile_number: String,
    val tax_type: String,
    val sales_amount: String,
    val paid_amount: String,
    val due_amount: String,
    val state: String,
    val pay_method: String,
    val tax_amount: String,
    val description: String,
    val productArray: List<SaleProductList>
)

data class EditProductModel(
    val product_id: String,
    val details: String,
    val pro_qty: String,
    val unit: String,
    val price: String,
    val dis_percentage: String,
    val dis_amt: String,
    val tax: String,
    val tax_amount2: String,
    val amount: String,
    val tax_type2: String
)












