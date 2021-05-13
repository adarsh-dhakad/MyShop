package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Build

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast

import com.example.myshop.R
import com.example.myshop.utils.MSPTextViewBold
import androidx.appcompat.widget.Toolbar;
import com.example.myshop.databinding.ActivityRegisterBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.User
import com.example.myshop.utils.MSPEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity :BaseActivity() {
  private lateinit var binding:ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()
        val tv_login = findViewById<MSPTextViewBold>(R.id.tv_login)
        tv_login.setOnClickListener {
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

     val btn_register = findViewById<Button>(R.id.btn_register)
        btn_register.setOnClickListener {
            registerUser()
        }

    }


    private fun setupActionBar(){
    val toolbar_register_activity = findViewById<Toolbar>(R.id.toolbar_register_activity)
        setSupportActionBar(toolbar_register_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
      toolbar_register_activity.setNavigationOnClickListener{ onBackPressed()}
    }

    private fun validateRegisterDetails() : Boolean {
        val  et_first_name = findViewById<MSPEditText>(R.id.et_first_name)
        val  et_last_name = findViewById<MSPEditText>(R.id.et_last_name)
        val  et_email = findViewById<MSPEditText>(R.id.et_email)
        val  et_password = findViewById<MSPEditText>(R.id.et_password)
        val  et_confirm_password = findViewById<MSPEditText>(R.id.et_confirm_password)
        val  cb_terms_and_condition = findViewById<CheckBox>(R.id.cb_terms_and_condition)

        return when{
                TextUtils.isEmpty(et_first_name.text.toString().trim{it <= ' '}) || et_first_name.length() <= 3 -> {
                    showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name),true)
                    false
                }

            TextUtils.isEmpty(et_last_name.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name),true)
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim{it <= ' '}) -> {
                
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter__cnm_password),true)
                false
            }
            et_password.text.toString().trim { it <= ' '} != et_confirm_password.text.toString().trim{it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),true)
                false
            }
            !cb_terms_and_condition.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_term_and_condition),true)
                false
            }
            else ->{
               showErrorSnackBar(resources.getString(R.string.registry_successfull),false)
                true
            }

        }

    }

    private fun registerUser(){

        if (validateRegisterDetails()){

            showProgressDialod(resources.getString(R.string.please_wait))



            FirebaseAuth.getInstance().createUserWithEmailAndPassword( binding.etEmail.text.toString().trim { it <= ' ' }, binding.etPassword.text.toString().trim { it <= ' ' })
                    .addOnCompleteListener { task ->

                        //   hideProgressDialog()

                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            val user = User(
                                    firebaseUser.uid,
                                    binding.etFirstName.text.toString().trim{it<=' '},
                                    binding.etLastName.text.toString().trim{it<=' '},
                                    binding.etEmail.text.toString().trim { it <= ' ' },
                            )



                            FirestoreClass().registerUser(this,user)



                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)

                        }

                    } }

       }

    fun userRegistrationSuccess() {
        hideProgressDialog()
        startActivity(Intent(this,LoginActivity::class.java))

        // intent.putExtra(Constants.IFDOCTORS, "")
        finish()
        Toast.makeText(this,
                resources.getString(R.string.register_success),
                Toast.LENGTH_LONG
        ).show()
    }


}