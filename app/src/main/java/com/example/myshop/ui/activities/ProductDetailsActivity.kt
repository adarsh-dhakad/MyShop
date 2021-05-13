package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.example.myshop.R
import com.example.myshop.databinding.ActivityProductDetailsBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.Product
import com.example.myshop.utils.Constants
import com.example.myshop.utils.GlideLoader


class ProductDetailsActivity : BaseActivity() {
    private var mProductId:String = ""
    private var mUserId:String = ""
    private lateinit var binding:ActivityProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

       if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
           mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
           Log.i("Product Id",mProductId)
       }
        if (intent.hasExtra(Constants.EXTRA_USER_ID)){
          mUserId = intent.getStringExtra(Constants.EXTRA_USER_ID)!!
            Log.i("User Id",mProductId)
        }


        getProductDetails()
    }


    private fun getProductDetails(){
        showProgressDialod(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this,mProductId)
    }
     fun productDetailsSuccess(product: Product){
         hideProgressDialog()
         GlideLoader(this@ProductDetailsActivity).loadUserPicture(
             product.image!!,
             binding.ivProductDetailImage
         )
         binding.apply {
             tvProductDetailsTitle.text = product.title
             tvProductDetailsPrice.text = "₹${product.price}"
             tvProductDetailsDescription.text = product.description
             tvProductDetailsStockQuantity.text = product.stock_quantity

         }
     }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu!!.findItem(R.id.action_edit_product)
       if (mUserId  == FirestoreClass().getCurrentUserID()){
           item.isEnabled = true;
           item.icon.alpha = 255;
       }else{
           item.isEnabled = false;
           item.icon.alpha = 130;
       }
        return super.onPrepareOptionsMenu(menu)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.product_details_edit_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_edit_product -> {
               // newGame()
                startActivity(Intent(this,AddProductActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun setupActionBar(){
        val toolbarProductDetailsActivity = binding.toolbarProductDetailsActivity

        setSupportActionBar(toolbarProductDetailsActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbarProductDetailsActivity.setNavigationOnClickListener{ onBackPressed()}
    }



}