package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.example.myshop.R
import com.example.myshop.databinding.ActivitySettingsBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.User
import com.example.myshop.utils.Constants
import com.example.myshop.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails:User
    private lateinit var binding:ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
     binding.btnLogout.setOnClickListener(this)
     binding.tvEdit.setOnClickListener(this)
     binding.llAddress.setOnClickListener(this)
    }

    private fun setupActionBar(){
       // val toolbar_settings_activity = findViewById<Toolbar>(R.id.toolbar_settings_activity)
        val toolbarSettingsActivity:Toolbar = binding.toolbarSettingsActivity
        setSupportActionBar(toolbarSettingsActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbarSettingsActivity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)

    }
    fun userDetailsSuccess(user: User){
        mUserDetails = user
          hideProgressDialog()
         GlideLoader(this).loadUserPicture( user.image!!,binding.ivUserPhoto)

        binding.apply {
           tvName.text = "${user.firstName}  ${user.lastName}"
           tvGender.text = user.gender
           tvEmail.text = user.uemail
           tvMobileNumber.text = "${user.mobile}"
        }
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
      if (v != null){
           when(v.id){
               R.id.tv_edit ->{
                   val intent = Intent(this@SettingsActivity,UserProfileActivity::class.java)
                   intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                   startActivity(intent)
                //   finish()
               }
               R.id.btn_logout ->{
                   FirebaseAuth.getInstance().signOut()
                   val intent = Intent(this@SettingsActivity,LoginActivity::class.java)
                   intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                   startActivity(intent)
                   finish()
               }
             R.id.ll_address ->{
                 val intent = Intent(this,AddressListActivity::class.java)
                 startActivity(intent)
             }

           }

      }
    }
}