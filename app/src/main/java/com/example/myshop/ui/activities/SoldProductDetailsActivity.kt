package com.example.myshop.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import com.example.myshop.R
import com.example.myshop.databinding.ActivitySoldProductDetailsBinding
import com.example.myshop.models.SoldProduct
import com.example.myshop.utils.Constants
import com.example.myshop.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class SoldProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySoldProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var productDetails: SoldProduct = SoldProduct()

        if(intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)){

            productDetails = intent.getParcelableExtra<SoldProduct>(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!

            }


        setupActionBar()

        setupUI(productDetails)


    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(findViewById(R.id.toolbar_sold_product_details_activity))

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        findViewById<Toolbar>(R.id.toolbar_sold_product_details_activity).setNavigationOnClickListener { onBackPressed() }
    }


    private fun setupUI(productDetails: SoldProduct) {

        binding.tvSoldProductDetailsId.text = productDetails.order_id

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        binding.tvSoldProductDetailsDate.text = formatter.format(calendar.time)

        GlideLoader(this@SoldProductDetailsActivity).loadProductPicture(
            productDetails.image,
            binding.ivProductItemImage
        )
        binding.tvProductItemName.text = productDetails.title
        binding.tvProductItemPrice.text ="$${productDetails.price}"
       binding.tvSoldProductQuantity.text = productDetails.sold_quantity

        binding.tvSoldDetailsAddressType.text = productDetails.address.type
        binding.tvSoldDetailsFullName.text = productDetails.address.address
        binding.tvSoldDetailsAddress.text =
            "${productDetails.address.address}, ${productDetails.address.zipCode}"
       binding.tvSoldDetailsAdditionalNote.text = productDetails.address.additionalNote

        if (productDetails.address.otherDetails!!.isNotEmpty()) {
           binding.tvSoldDetailsOtherDetails.visibility = View.VISIBLE
            binding.tvSoldDetailsOtherDetails.text = productDetails.address.otherDetails
        } else {
            binding.tvSoldDetailsOtherDetails.visibility = View.GONE
        }
        binding.tvSoldDetailsMobileNumber.text = productDetails.address.mobileNumber

        binding.tvSoldProductSubTotal.text = productDetails.sub_total_amount
        binding.tvSoldProductShippingCharge.text = productDetails.shipping_charge
        binding.tvSoldProductTotalAmount.text = productDetails.total_amount
    }
}