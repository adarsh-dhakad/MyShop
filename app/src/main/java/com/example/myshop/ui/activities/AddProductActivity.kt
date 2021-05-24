package com.example.myshop.ui.activities


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myshop.R
import com.example.myshop.databinding.ActivityAddProductBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.Product
import com.example.myshop.utils.Constants
import com.example.myshop.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity() , View.OnClickListener{
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var mProduct:Product
    private var checkUploadAdd:Boolean = true
    private var mSelectedImageFileUri: Uri? = null
    private  var mProductImageURL:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT)){
            mProduct = intent.getParcelableExtra(Constants.EXTRA_PRODUCT)!!
            Log.i("Product Id", mProduct.toString())
            showProductDetails(mProduct)
        }

        binding.ivAddUpdateProduct.setOnClickListener(this@AddProductActivity)
        binding.btnSubmitAddProduct.setOnClickListener(this@AddProductActivity)

    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.iv_add_update_product ->{
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED ){
                        Constants.showImageChooser(this@AddProductActivity)
                    }else{
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }

                }

                R.id.btn_submit_add_product ->{
                    if (validateProductDetails()){

                      if (checkUploadAdd) if (mSelectedImageFileUri != null){
                            uploadProductImage()
                        }
                    }
                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //   showErrorSnackBar("The storage permission granted .",false)
                Constants.showImageChooser(this@AddProductActivity)
            } else {
                Toast.makeText(this@AddProductActivity,
                    resources.getString(R.string.read_storage_permission_dened), Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            if (data != null) {
                binding.ivAddUpdateProduct.setImageDrawable(ContextCompat.getDrawable(this@AddProductActivity,R.drawable.ic_vector_edit))
                try {

                    mSelectedImageFileUri = data.data!!;
                    //    binding.ivUserPhoto.setImageURI(Uri.parse(selectedImageFileUri.toString()))
                    //  binding.ivUserPhoto.setImageURI(selectedImageFileUri)
                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,binding.ivProductImage)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this,
                        resources.getString(R.string.image_selection_failed),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    /**
     * A function to validate the product details.
     */
    private fun validateProductDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(binding.etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(binding.etProductDescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etProductQuantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAddProductActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(Constants.MYSHOP_PREFENCES,Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME,"")!!
        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            binding.etProductTitle.text.toString().trim { it <= ' '},
            binding.etProductPrice.text.toString().trim { it <= ' '},
            binding.etProductDescription.text.toString().trim { it <= ' '},
            binding.etProductQuantity.text.toString().trim { it <= ' ' },
            mProductImageURL,
        )
        FirestoreClass().uploadProductDetails(this,product)
    }
    private  fun uploadProductImage(){
        if(mSelectedImageFileUri.toString() != mProductImageURL.toString()){
        showProgressDialod(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri,Constants.PRODUCT_IMAGE)
            }
    }
    fun productUploadSuccess(){
        hideProgressDialog()
        // showErrorSnackBar(,false)
        Toast.makeText(this@AddProductActivity,resources.getString(R.string.product_uploaded_success_message),Toast.LENGTH_LONG)
            .show()
        finish()

    }
    fun imageUploadSuccess(imageURL:String){
        //    hideProgressDialog()
        mProductImageURL = imageURL
        // showErrorSnackBar("Product image is uploaded successfully,Image URL : $imageURL",false)

        uploadProductDetails()
    }
    // product details
    private fun showProductDetails(product: Product){

        checkUploadAdd = false
        mSelectedImageFileUri = Uri.parse(product.image)
        GlideLoader(this@AddProductActivity).loadUserPicture(
            product.image!!,
            binding.ivProductImage
        )
        binding.apply {
            etProductTitle.setText("${product.title}")
            etProductPrice.setText("₹${product.price}")
            etProductDescription.setText("${product.description}")
            etProductQuantity.setText("${product.stock_quantity}")

        }
    }
}