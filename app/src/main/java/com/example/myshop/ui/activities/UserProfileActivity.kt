package com.example.myshop.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myshop.R
import com.example.myshop.databinding.ActivityUserProfileBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.User
import com.example.myshop.utils.Constants
import com.example.myshop.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var  mUserDetails:User
    private var mSelectedFileUri : Uri? = null
    private var mUserProfileImageURI:String =""
    private lateinit var binding: ActivityUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

    //    val userDetails = User()
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        binding.apply {
            etFirstName.setText( mUserDetails.firstName)
            etLastName.setText( mUserDetails.lastName)
            etEmail.isEnabled = false
            etEmail.setText( mUserDetails.uemail)

        }
    if (mUserDetails.profileCompleted==0){
        binding.tvTitle.text = resources.getString(R.string.title_complete_profile)
        binding.etFirstName.isEnabled = false
         binding.etLastName.isEnabled = false


    }else{
        setupActionBar()
        binding.apply {
            tvTitle.text = resources.getString(R.string.title_edit_profile)
            mUserDetails.image?.let { GlideLoader(this@UserProfileActivity).loadUserPicture(it,ivUserPhoto) }

//            binding.etFirstName.setText( mUserDetails.firstName)
//            binding.etLastName.setText( mUserDetails.lastName)

            if (mUserDetails.mobile != 0L){
            etMobileNumber.setText("${mUserDetails.mobile}") }
            if (mUserDetails.gender == Constants.MALE){
                       rbMale.isChecked = true
            } else {
                    rbFemale.isChecked = true
            }


        }
    }

        binding.ivUserPhoto.setOnClickListener(this@UserProfileActivity)
        binding.btnSubmit.setOnClickListener( this@UserProfileActivity )
     }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //   showErrorSnackBar("The storage permission granted .",false)
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                Toast.makeText(this@UserProfileActivity,
                        resources.getString(R.string.read_storage_permission_dened), Toast.LENGTH_LONG)
                        .show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            if (data != null) {
                try {
                    mSelectedFileUri = data.data!!;
                    //    binding.ivUserPhoto.setImageURI(Uri.parse(selectedImageFileUri.toString()))
                    //  binding.ivUserPhoto.setImageURI(selectedImageFileUri)
                    GlideLoader(this@UserProfileActivity).loadUserPicture(mSelectedFileUri!!,binding.ivUserPhoto)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun setupActionBar(){
        // val toolbar_settings_activity = findViewById<Toolbar>(R.id.toolbar_settings_activity)
        val toolbarSettingsActivity: Toolbar = binding.toolbarUserProfileActivity
        setSupportActionBar(toolbarSettingsActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbarSettingsActivity.setNavigationOnClickListener{ onBackPressed()}
    }

    override fun onClick(v: View?) {
      if (v != null){
          when(v.id){

              R.id.iv_user_photo -> {

                  if (ContextCompat.checkSelfPermission(this@UserProfileActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                          PackageManager.PERMISSION_GRANTED) {
                      //  showErrorSnackBar("You already have the storage permission.", false)
                      Constants.showImageChooser(this@UserProfileActivity)
                  } else {
                      // Requests permission
                      ActivityCompat.requestPermissions(this@UserProfileActivity,
                              arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                              Constants.READ_STORAGE_PERMISSION_CODE)
                  }


              }
              R.id.btn_submit -> {

                  if (validateUserProfileDetails()){

                      showProgressDialod(resources.getString(R.string.please_wait))
                      if (mSelectedFileUri != null)
                          FirestoreClass().uploadImageToCloudStorage(this,mSelectedFileUri,Constants.USER_PROFILE_IMAGE)
                      else{
                          updateUserProfileDetails()
                      }
                  }
              }


          }
      }
    }
    private  fun validateUserProfileDetails():Boolean {
        return when{
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
                false
            }
            else -> {
                true
            }


        }
    }

    fun imageUploadSuccess(imageURL:String){
        //   hideProgressDialog()
        mUserProfileImageURI = imageURL
        //   Toast.makeText(this, " your image uploaded successfully URL of your image $imageURL",
        //  Toast.LENGTH_LONG).show()
        updateUserProfileDetails()
    }

    private fun updateUserProfileDetails(){

        val userHashMap = HashMap<String,Any>()
        val firstName = binding.etFirstName.text.toString().trim{it <= ' '}
        if (firstName != mUserDetails.firstName ){
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        val  lastName = binding.etLastName.text.toString().trim{it <= ' '}
        if (firstName != mUserDetails.lastName ){
            userHashMap[Constants.LAST_NAME] = lastName
        }



        if(mUserProfileImageURI.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURI
        }
        val mobileNumber =  binding.etMobileNumber.text.toString().trim{it <= ' '}
        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        val gender = if (binding.rbMale.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }
        //( kay) Constants.gender = (value ) gender
        if (gender.isNotEmpty() && gender != mUserDetails.gender){
        userHashMap[Constants.GENDER]  = gender
        }
        userHashMap[Constants.GENDER]  = gender
        //  showProgressDialod(resources.getString(R.string.please_wait))
        userHashMap[Constants.COMPLETE_PROFILE] = 1
        FirestoreClass().updateUserProfile(this,userHashMap)


    }


    fun userProfileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(this,
                resources.getString(R.string.msg_mobile_update_success),
                Toast.LENGTH_SHORT).show()
        startActivity(Intent(this,DashboardActivity::class.java))
        finish()

    }
}
