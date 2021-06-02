package com.example.myshop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.R
import com.example.myshop.databinding.ActivityCartListBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.CartItem
import com.example.myshop.models.Product
import com.example.myshop.ui.adapters.MyCartListAdapter
import com.example.myshop.utils.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CartListActivity : BaseActivity() {
    private lateinit var mCartListItems:ArrayList<CartItem>
    private lateinit var mProductsList:ArrayList<Product>
    private lateinit var binding:ActivityCartListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

       binding.btnCheckout.setOnClickListener {
          val intent = Intent(this@CartListActivity,AddressListActivity::class.java)
          intent.putExtra(Constants.EXTRA_SELECT_ADDRESS,true)
          startActivity(intent)
       }
    }


    private fun getCartItemList(){
      FirestoreClass().getCartList(this@CartListActivity)
    }
    
    private fun getProductList(){
        showProgressDialod(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductList(this@CartListActivity)
    }

    fun successProductsListFromFireStore(productsList:ArrayList<Product>){

        mProductsList = productsList
        getCartItemList()
    }

    override fun onResume() {
        super.onResume()
       // getCartItemList()
       GlobalScope.launch {
           getProductList()
       }
    }

    fun successCartItemList(cartList:ArrayList<CartItem>){
        hideProgressDialog()

        for(product in mProductsList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity

                    if (product.stock_quantity?.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }
         mCartListItems = cartList
        if (cartList.size > 0){
            binding.apply {

                rvCartItemsList.visibility = View.VISIBLE
                llCheckout.visibility = View.VISIBLE
                tvNoCartItemFound.visibility = View.GONE
            }
            binding.apply {
              rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
              rvCartItemsList.setHasFixedSize(true)
           }
            val cartListAdapter = MyCartListAdapter(this@CartListActivity,cartList)
            binding.rvCartItemsList.adapter = cartListAdapter

            var subTotal = 0.0
            for (item in  mCartListItems){
                val availableQuantity = item.stock_quantity?.toInt()
                if (availableQuantity != null) {
                    if (availableQuantity > 0) {
                        val price = item.price?.toDouble()!!
                        val quantity = item.cart_quantity?.toInt()
                        subTotal += (price * quantity!!)
                    }
                }
            }

            binding.tvItemSubTotal.text =  "₹$subTotal"
            binding.tvShippingCharge.text = "₹50"

           if(subTotal > 0){
               binding.llCheckout.visibility = View.VISIBLE
               val total = subTotal + 50
               binding.tvTotalAmount.text = "₹$total"
           }else{
               binding.apply {
                   llCheckout.visibility = View.GONE
                   rvCartItemsList.visibility = View.GONE
                   tvNoCartItemFound.visibility = View.VISIBLE
               }

           }

        }

    }
    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemList()
    }

    fun itemRemovedSuccess(){
        hideProgressDialog()
        Toast.makeText(this,resources.getString(R.string.msg_item_removed_successfully),
        Toast.LENGTH_LONG
        ).show()

        getCartItemList()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarCartListActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarCartListActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}