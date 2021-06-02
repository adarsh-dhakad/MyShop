package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.myshop.R
import com.example.myshop.databinding.ActivityLoginBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.User
import com.example.myshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() , View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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


        binding.btnLogin.setOnClickListener (this)
        binding.tvForgotPassword.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)



    }

    private  fun logInRegisteredUser(){
        if(validateLoginDetails()){
            // show the progress dialog
            showProgressDialod(resources.getString(R.string.please_wait))

            val email = binding.etEmail.text.toString().trim{it <= ' '}
            val password = binding.etPassword.text.toString().trim{it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                        task ->


                    if(task.isSuccessful){


                        FirestoreClass().getUserDetails(this@LoginActivity)


                    }else{

                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)

                    }


                }

        }
    }

    private fun validateLoginDetails(): Boolean {
        return when{

            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }

            else -> {
                //  showErrorSnackBar( "Your Details are valid.",false)
                true
            }
        }

    }


    override fun onClick(v: View?) {
       if (v != null) {
            when(v.id){

                R.id.btn_login-> {
                    // validateLoginDetails()
                    logInRegisteredUser()
                }
                    R.id.tv_forgot_password ->{
                    val i = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(i)
                     }
                    R.id.tv_register ->{
                    val i = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(i)
                }

            }
        }
    }


    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        //    user.firstName?.let { Log.i("First Name", it) }
        //     user.lastName?.let { Log.i("last Name", it) }
        //     user.uemail?.let { Log.i("Email", it) }

        if (user.profileCompleted == 0){
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }else {
           startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
          //  startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
        finish()
    }



}