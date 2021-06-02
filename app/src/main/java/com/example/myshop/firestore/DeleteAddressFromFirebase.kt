package com.example.myshop.firestore

import android.util.Log
import com.example.myshop.ui.activities.AddressListActivity
import com.example.myshop.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class DeleteAddressClass {
    // Access a Cloud Firestore instance.
    private val mFireStore = FirebaseFirestore.getInstance()

    fun deleteAddress(activity: AddressListActivity,addressId:String){
          mFireStore.collection(Constants.ADDRESSES)
              .document(addressId)
              .delete()
              .addOnSuccessListener {
                   activity.deleteAddressSuccess()
              }
              .addOnFailureListener {
                  e->
                  activity.hideProgressDialog()
                  Log.e(
                      activity.javaClass.simpleName,
                      "Error while deleting the address.",
                      e
                  )
              }
    }
}