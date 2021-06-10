package com.example.myshop.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.databinding.ActivityAddressListBinding
import com.example.myshop.firestore.DeleteAddressClass
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.Address
import com.example.myshop.ui.adapters.AddressListAdapter
import com.example.myshop.utils.Constants
import com.example.myshop.utils.SwipeToDeleteCallback
import com.myshoppal.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {

    private var mSelectAddress:Boolean = false

    private lateinit var binding:ActivityAddressListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

      getAddressList()

        binding.tvAddAddress.setOnClickListener {
            val intent = Intent(this,AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS,false)
        }

        if (mSelectAddress){
           binding.tvTitle.text = resources.getString(R.string.title_select_address)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            getAddressList()
        }
    }

    private fun getAddressList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAddressesList(this)
    }

    fun successAddressListFromFirestore(addressList:ArrayList<Address>){
        hideProgressDialog()
        if (addressList.size >0){
            binding.tvNoAddressFound.visibility = View.GONE
            binding.rvAddressList.visibility = View.VISIBLE
            binding.rvAddressList.layoutManager = LinearLayoutManager(this)
            binding.rvAddressList.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this,addressList,mSelectAddress)
            binding.rvAddressList.adapter = addressAdapter

        if (!mSelectAddress) {

            val editSwipeHandler = object : SwipeToEditCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = binding.rvAddressList.adapter as AddressListAdapter
                    adapter.notifyEditItem(
                        this@AddressListActivity,
                        viewHolder.adapterPosition
                    )
                }
            }

            val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
            editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)


            val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    showProgressDialog(resources.getString(R.string.please_wait))

                    addressList[viewHolder.adapterPosition].id?.let {
                        DeleteAddressClass().deleteAddress(
                            this@AddressListActivity,
                            it
                        )
                    }
                }

            }

            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)
        }
        }else{
            binding.tvNoAddressFound.visibility = View.VISIBLE
            binding.rvAddressList.visibility = View.GONE

        }
    }

    fun deleteAddressSuccess(){
        hideProgressDialog()
        Toast.makeText(this,
        resources.getString(R.string.msg_your_address_deleted_successfully),
        Toast.LENGTH_SHORT
        ).show()
        FirestoreClass().getAddressesList(this)
    }

    private fun setupActionBar(){
        // val toolbar_settings_activity = findViewById<Toolbar>(R.id.toolbar_settings_activity)
        val toolbarAddressListActivity: Toolbar = binding.toolbarAddressListActivity
        setSupportActionBar(toolbarAddressListActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbarAddressListActivity.setNavigationOnClickListener{ onBackPressed()}
    }
}