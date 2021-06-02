package com.example.myshop.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.myshop.R
import com.example.myshop.databinding.ActivityAddEditAddressBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.Address
import com.example.myshop.utils.Constants

class AddEditAddressActivity : BaseActivity(){
    private lateinit var binding: ActivityAddEditAddressBinding
    private var mAddressDetails: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)

        }

        if(mAddressDetails != null){
            if(mAddressDetails!!.id?.isNotEmpty() == true){
              binding.apply {
                  tvTitle.text = resources.getString(R.string.title_edit_address)
                  etFullName.setText(mAddressDetails?.full_name)
                  etPhoneNumber.setText(mAddressDetails?.mobileNumber)
                  etZipCode.setText(mAddressDetails?.zipCode)
                  etAddress.setText(mAddressDetails?.address)
                  etAdditionalNote.setText(mAddressDetails?.additionalNote)

              }

                when(mAddressDetails?.type){
                    Constants.HOME ->{
                        binding.rbHome.isChecked = true
                    }
                    Constants.OFFICE ->{
                      binding.rbOffice.isChecked = true
                     }
                    else ->{
                        binding.apply {
                            rbOther.isChecked = true
                            tilOtherDetails.visibility = View.VISIBLE
                            etOtherDetails.setText(mAddressDetails?.otherDetails)
                        }

                    }

                }
            }
        }

        binding.btnSubmitAddress.setOnClickListener { saveAddressToFirestore() }
        binding.rgType.setOnCheckedChangeListener {
                _, checkedId ->
            if(checkedId == R.id.rb_other){
                binding.tilOtherDetails.visibility = View.VISIBLE
            }else{
                binding.tilOtherDetails.visibility = View.GONE
            }

        }
    }

    private fun saveAddressToFirestore(){
        val fullName:String = binding.etFullName.text.toString().trim { it <= ' ' }
        val phoneNumber:String = binding.etPhoneNumber.text.toString().trim { it <= ' ' }
        val address:String = binding.etAddress.text.toString().trim { it <= ' ' }
        val zipCode:String = binding.etZipCode.text.toString().trim { it <= ' '}
        val additionalNote:String = binding.etAdditionalNote.text.toString().trim { it <= ' ' }
        val otherDetails:String = binding.etOtherDetails.text.toString().trim { it <= ' ' }

        if(validateData()){
            showProgressDialod(resources.getString(R.string.please_wait))

            val addressType:String = when{
                binding.rbHome.isChecked ->{
                    Constants.HOME
                }
                binding.rbOffice.isChecked ->{
                    Constants.OFFICE
                }
                else ->{
                    Constants.OTHER
                }

            }

            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                fullName,
               phoneNumber,
               address,
               zipCode,
               additionalNote,
               addressType,
               otherDetails
            )
        if(mAddressDetails != null && mAddressDetails!!.id!!.isNotEmpty()){
            FirestoreClass().updateAddress(this,addressModel,mAddressDetails!!.id!!)
        }else {
            FirestoreClass().addAddress(this, addressModel)
        }
        }

    }

    fun addUpdateAddressSuccess(){
       hideProgressDialog()
        val notifySuccessMessage:String = if(mAddressDetails != null && mAddressDetails!!.id!!.isNotEmpty()){
            resources.getString(R.string.msg_your_address_updated_successfully)
        }else{
            resources.getString(R.string.msg_your_address_added_successfully)
        }
        Toast.makeText(this@AddEditAddressActivity,notifySuccessMessage,
        Toast.LENGTH_SHORT)
            .show()
        setResult(RESULT_OK)
        finish()
    }

    private fun validateData():Boolean{
        return when{
            TextUtils.isEmpty(binding.etFullName.text.toString().trim { it <= ' ' }) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_full_name) ,
                    true)
                false
            }

            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim { it <= ' ' }) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_phone_number),
                true)
                false
            }

            TextUtils.isEmpty(binding.etAddress.text.toString().trim { it <= ' ' }) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address),
                    true)
                false
            }

            TextUtils.isEmpty(binding.etZipCode.text.toString().trim { it <= ' ' }) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code),
                    true)
                false
            }

            binding.rbOther.isChecked &&  TextUtils.isEmpty(binding.etOtherDetails.text.toString().trim { it <= ' ' }) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_other_details),
                    true)
                false
            }

           else ->{
               true
           }
        }
    }



    private fun setupActionBar(){
        // val toolbar_settings_activity = findViewById<Toolbar>(R.id.toolbar_settings_activity)
        val toolbarAddEditAddressActivity: Toolbar = binding.toolbarAddEditAddressActivity
        setSupportActionBar(toolbarAddEditAddressActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbarAddEditAddressActivity.setNavigationOnClickListener{ onBackPressed()}
    }
}