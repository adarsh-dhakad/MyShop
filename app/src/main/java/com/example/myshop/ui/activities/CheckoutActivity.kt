package com.example.myshop.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshop.R
import com.example.myshop.databinding.ActivityCheckoutBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.*
import com.example.myshop.ui.adapters.MyCartListAdapter
import com.example.myshop.utils.Constants
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject


class CheckoutActivity : BaseActivity() , PaymentResultListener,View.OnClickListener {
    private lateinit var binding:ActivityCheckoutBinding
    private var mAddressDetails: Address? = null
    private lateinit var mProductsList:ArrayList<Product>
    private lateinit var mCartItemList:ArrayList<CartItem>
    private var mSubTotal:Double = 0.0
    private var mTotalAmount:Double = 0.0
    private lateinit var mUserDetails:User
    private lateinit var mPaymentId:String
    private lateinit var mOrderDetails:Order

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

        getProductList()

        binding.btnPlaceOrder.setOnClickListener ( this )
        binding.rbOnlinePay.setOnClickListener ( this )
    }
    private fun getUserDetails(){
       // showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)

    }
    fun userDetailsSuccess(user: User){
        mUserDetails = user
        hideProgressDialog()
   }


    fun successProductsListFromFireStore(productsList:ArrayList<Product>){
        mProductsList = productsList
        getCartItemList()
    }

    fun allDetailsUpdatedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this,"Your order was placed successfully.",
            Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(this,DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun orderPlacedSuccess() {

    FirestoreClass().updateAllDetails(this,mCartItemList , mOrderDetails)

    }

    private fun placeAnOrder(){
       showProgressDialog(resources.getString(R.string.please_wait))

        if (mAddressDetails != null) {

            mOrderDetails = Order(
        FirestoreClass().getCurrentUserID(),
        mCartItemList,
        mAddressDetails!!,
        "My order ${System.currentTimeMillis()}",
        mCartItemList[0].image,
        mSubTotal.toString(),
          "₹50",
        mTotalAmount.toString(),
        mPaymentId,
        System.currentTimeMillis(),
        )
    FirestoreClass().placeOrder(this, mOrderDetails)
}
    }

    private fun getProductList(){
        // showProgress Dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductList(this@CheckoutActivity)
    }

    private fun getCartItemList(){
        FirestoreClass().getCartList(this@CheckoutActivity)
    }

    fun successCartItemList(cartList:ArrayList<CartItem>) {
        getUserDetails()
       // hideProgressDialog()

        for (product in mProductsList){
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id){
                     cartItem.stock_quantity = product.stock_quantity.toString()
                }
            }
        }
        mCartItemList = cartList

        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)
        val cartListAdapter = MyCartListAdapter(this@CheckoutActivity,mCartItemList,false)
        binding.rvCartListItems.adapter = cartListAdapter


        for(item in mCartItemList){
            val availableQuantity = item.stock_quantity?.toInt()
            if (availableQuantity != null) {
                if(availableQuantity>0){
                    val price = item.price?.toDouble()
                    val quantity = item.cart_quantity?.toInt()
                    mSubTotal += (price!!* quantity!!)
                }
            }
        }
      binding.tvCheckoutSubTotal.text = "₹$mSubTotal"
      binding.tvCheckoutShippingCharge.text = "₹50"

      if(mSubTotal > 0) {
          binding.llCheckoutPlaceOrder.visibility = View.VISIBLE
          mTotalAmount = mSubTotal + 50
          binding.tvCheckoutTotalAmount.text = "$mTotalAmount"

      } else{
          binding.llCheckoutPlaceOrder.visibility = View.GONE
      }

    }


    private fun startPayment() {
        /* *  You need to pass current activity in order to let Razorpay create CheckoutActivity        * */

        val activity: Activity = this
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_qdR4ZyXR0qTmH0")
        try {
            val options = JSONObject()
            options.put("name",mCartItemList[0].title)
           // options.put("description","Demoing Charges")
            //You can omit the image option to fetch the image from dashboard
             options.put("image",mCartItemList[0].image)
             options.put("theme.color", "#FD089C");
             options.put("currency","INR");
            // options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount",mTotalAmount*100)
            // pass amount in currency subunits
            // val retryObj = new JSONObject();
            // retryObj.put("enabled", true);
            // retryObj.put("max_count", 4);
            // options.put("retry", retryObj);
            val prefill = JSONObject()
            prefill.put("email",mUserDetails.uemail)
            prefill.put("contact",mUserDetails.mobile)
            options.put("prefill",prefill)
            checkout.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        /**
         * Add your logic here for a failed payment response
         */
        Toast.makeText(this,"Payment failed please try again ",Toast.LENGTH_LONG).show()
        startActivity(Intent(this,CheckoutActivity::class.java))
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        if(razorpayPaymentId != null){
        mPaymentId = razorpayPaymentId
        Toast.makeText(this,"Payment success full paymentId is $razorpayPaymentId",Toast.LENGTH_LONG).show()
        placeAnOrder()
       }else{
            Toast.makeText(this,"Payment success full but paymentId is null",Toast.LENGTH_LONG).show()
            placeAnOrder()
        }
        val intent = Intent(this,DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.btn_place_order ->{
                    mPaymentId = resources.getString(R.string.lbl_cash_on_delivery)
                    placeAnOrder()
                }
                R.id.rb_online_pay ->{
                    binding.btnPlaceOrder.visibility = View.GONE
                    startPayment()
                }

            }
        }
    }
}