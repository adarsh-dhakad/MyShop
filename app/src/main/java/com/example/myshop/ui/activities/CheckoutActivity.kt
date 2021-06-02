package com.example.myshop.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.myshop.R
import com.example.myshop.databinding.ActivityCheckoutBinding
import com.example.myshop.models.Address
import com.example.myshop.utils.Constants

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCheckoutBinding
    private var mAddressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if(mAddressDetails != null){
            binding.tvCheckoutAddressType.text = mAddressDetails?.type
            binding.tvCheckoutFullName.text = mAddressDetails?.full_name
            binding.tvCheckoutAddress.text = "${mAddressDetails!!.address} , ${mAddressDetails!!.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()){
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }
            binding.tvMobileNumber.text = mAddressDetails?.mobileNumber
            
        }

    }

    private fun setupActionBar(){
       val toolbarAddEditAddressActivity: Toolbar = binding.toolbarCheckoutActivity
        setSupportActionBar(toolbarAddEditAddressActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbarAddEditAddressActivity.setNavigationOnClickListener{ onBackPressed()}
    }
}