package com.example.myshop.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.R
import com.example.myshop.databinding.ActivityMyOrderDetailsBinding
import com.example.myshop.models.Order
import com.example.myshop.ui.adapters.MyCartListAdapter
import com.example.myshop.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMyOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
      var myOrderDetails: Order = Order()
        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)){
          myOrderDetails = intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!

        }

      setupUI(myOrderDetails)
    }

    private fun setupActionBar(){

        setSupportActionBar(binding.toolbarMyOrderDetailsActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarMyOrderDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupUI(orderDetails:Order){
     binding.tvOrderDetailsId.text = orderDetails.title
     val dateFormat = "dd mmm yyyy HH:mm"
     val formatter  = SimpleDateFormat(dateFormat, Locale.getDefault())
     val calendar : Calendar = Calendar.getInstance()
      calendar.timeInMillis = orderDetails.order_datetime
      val orderDateTime = formatter.format(calendar.time)
      binding.tvOrderDetailsDate.text = orderDateTime

     val diffInMilliSeconds:Long = System.currentTimeMillis() - orderDetails.order_datetime
     val diffInHours:Long = TimeUnit.MICROSECONDS.toHours(diffInMilliSeconds)
     Log.e("Difference in Hours","$diffInHours")

     when{

         diffInHours < 1 ->{
             binding.tvOrderStatus.text = resources.getString(R.string.order_status_in_pending)
             binding.tvOrderStatus.setTextColor(
                 ContextCompat.getColor(
                     this@MyOrderDetailsActivity,
                     R.color.colorAccent
                 )
             )
         }
         diffInHours < 2 -> {
             binding.tvOrderStatus.text = resources.getString(R.string.order_status_in_process)
             binding.tvOrderStatus.setTextColor(
                 ContextCompat.getColor(
                     this@MyOrderDetailsActivity,
                     R.color.colorOrderStatusInProcess
                 )
             )
         }
         else -> {
             binding.tvOrderStatus.text = resources.getString(R.string.order_status_delivered)
             binding.tvOrderStatus.setTextColor(
                 ContextCompat.getColor(
                     this,
                     R.color.colorOrderStatusDelivered
                 )
             )
         }


     }

     binding.rvMyOrderItemsList.layoutManager = LinearLayoutManager(this)
     binding.rvMyOrderItemsList.setHasFixedSize(true)

     val cartListAdapter = MyCartListAdapter(this,orderDetails.items,false)
     binding.rvMyOrderItemsList.adapter = cartListAdapter

     binding.apply {
         tvMyOrderDetailsAddressType.text = orderDetails.address.type
         tvMyOrderDetailsFullName.text = orderDetails.address.full_name
         tvMyOrderDetailsAddress.text  = "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
         tvMyOrderDetailsAdditionalNote.text = orderDetails.address.additionalNote

         if(orderDetails.address.otherDetails!!.isNotEmpty()){
             tvMyOrderDetailsOtherDetails.visibility = View.VISIBLE
             tvMyOrderDetailsOtherDetails.text = orderDetails.address.otherDetails
         }else{
             tvMyOrderDetailsOtherDetails.visibility = View.GONE
         }

         tvMyOrderDetailsMobileNumber.text = orderDetails.address.mobileNumber
         tvOrderDetailsSubTotal.text = orderDetails.sub_total_amount
         tvOrderDetailsTotalAmount.text = orderDetails.total_amount
         if (orderDetails.payment_id.toString() !=  resources.getString(R.string.lbl_cash_on_delivery)){
             tvOrderDetailsPaymentMode.text =  resources.getString(R.string.rb_lbl_online_pay)
             tvOrderDetailsPaymentId.visibility = View.VISIBLE
             tvOrderDetailsPaymentId.text   = "Payment Id : ${orderDetails.payment_id}"
         }
     }

    }
}