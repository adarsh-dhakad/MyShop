package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myshop.R
import com.example.myshop.databinding.ActivityProductDetailsBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.CartItem
import com.example.myshop.models.Product
import com.example.myshop.utils.Constants
import com.example.myshop.utils.GlideLoader


class ProductDetailsActivity : BaseActivity() ,View.OnClickListener {
    private var mProductId:String = ""
    private var productOwnerId: String = ""
    private lateinit var mProductDetails:Product
    private lateinit var binding:ActivityProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

       if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
           mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        //   Log.i("Product Id",mProductId)
       }
        if (intent.hasExtra(Constants.EXTRA_OWNER_ID)){
            productOwnerId = intent.getStringExtra(Constants.EXTRA_OWNER_ID)!!
            Log.i("User Id",mProductId)
        }

        if (FirestoreClass().getCurrentUserID() == productOwnerId) {
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
        } else {
           binding.btnAddToCart.visibility = View.VISIBLE
        }
       binding.btnAddToCart.setOnClickListener(this)
       binding.btnGoToCart.setOnClickListener(this)
        getProductDetails()
    }


    private fun getProductDetails(){
        showProgressDialod(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this,mProductId)
    }

    fun productExistsInCart(){
        hideProgressDialog()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

     fun productDetailsSuccess(product: Product){
         mProductDetails = product
        // hideProgressDialog()
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
      if (product.stock_quantity?.toInt() == 0){
          hideProgressDialog()
          binding.btnAddToCart.visibility = View.GONE
          binding.tvProductDetailsStockQuantity.setText(
              resources.getString(R.string.lbl_out_of_stock)
          )
          binding.tvProductDetailsStockQuantity.setTextColor(
              ContextCompat.getColor(this,
                  R.color.colorSnackBarError)
          )
      }else{
          if (FirestoreClass().getCurrentUserID() == product.user_id){
              hideProgressDialog()
          }else{
              FirestoreClass().checkIfItemExistInCart(this,mProductId)
          }
      }

     }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu!!.findItem(R.id.action_edit_product)
       if (productOwnerId  == FirestoreClass().getCurrentUserID()){
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
            val intent = Intent(this,AddProductActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT,mProductDetails)
                startActivity(intent)
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
    private fun addToCart(){
        val addToCard = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY,
        )

     showProgressDialod(resources.getString(R.string.please_wait))
     FirestoreClass().addCartItems(this,addToCard)
    }

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(this,resources.getString(R.string.success_message_item_added_to_cart),
        Toast.LENGTH_LONG)
            .show()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
      if (v != null){
         when(v.id) {
             R.id.btn_add_to_cart -> {
                     addToCart()
             }
             R.id.btn_go_to_cart ->{
                 val intent = Intent(this,CartListActivity::class.java)
                 startActivity(intent)
             }
         }
      }
    }


}