package com.example.myshop.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myshop.models.Product
import com.example.myshop.models.User
import com.example.myshop.ui.activities.*
import com.example.myshop.ui.fragments.BaseFragment
import com.example.myshop.ui.fragments.DashboardFragment
import com.example.myshop.ui.fragments.ProductsFragment
import com.example.myshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.HashMap

class FirestoreClass {
    val mFireStore =  FirebaseFirestore.getInstance()

   fun getCurrentUserID():String{

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID =  ""
        if (currentUser != null){
            currentUserID = currentUser.uid
        }

        return  currentUserID  }



    fun getUserDetails(activity: Activity){

        mFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

              //      Log.i(activity.javaClass.simpleName,document.toString())

                    val user = document.toObject(User::class.java)!!

                    val sharePreferences = activity.getSharedPreferences(
                            Constants.MYSHOP_PREFENCES,
                            Context.MODE_PRIVATE

                    )

                    val editor : SharedPreferences.Editor = sharePreferences.edit()
                    editor.putString(
                            Constants.LOGGED_IN_USERNAME,
                            "${user.firstName} ${user.lastName}"

                    )
                    editor.apply()
                    when(activity){
                        is LoginActivity -> {
                            if (user != null) {
                                activity.userLoggedInSuccess(user)
                            }

                        }
                        is SettingsActivity -> {
                               activity.userDetailsSuccess(user)
                        }

                    }


                }
                .addOnFailureListener { e ->

                    when(activity){
                        is LoginActivity -> {
                            activity.hideProgressDialog()
                        }
                        is SettingsActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                  Log.e(
                          activity.javaClass.simpleName,
                          "Error while getting user details.",
                  e
                  )
                }


    }

    fun registerUser(activity: RegisterActivity, userInfo: User){
        // the "users" is  collection name .if the collection is already created then it will not create the same

        mFireStore.collection(Constants.USERS)
                //  mFireStore.collection("doctors")
                // Document ID for users fields ,Here the Document it is the User ID.
                .document(userInfo.uid!!)
                //   .document(userInfo.uid)
                // here the userInfo are field and the setoption is set to merge .It is for if we we want to marge late on instead of replacing the fields.
                .set(userInfo, SetOptions.merge())

                .addOnSuccessListener {
                    activity.userRegistrationSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while registering the user.",
                            e
                    )

                }



    }

    fun uploadImageToCloudStorage(activity: Activity,imageFileURI: Uri?,imageType:String){
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis()+"."
                    +Constants.getFileExtension(activity,imageFileURI)
        )

        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                //   Log.e( "Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString() )
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener {uri ->
                        //    Log.e("Downloadable Image URI",uri.toString())
                        when(activity){
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddProductActivity ->{
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }

                        //   binding.tvTextResult.text
                        //     binding.tvTextResult.text = "Your image uploaded successfully $uri"
                        //  Glide.with(this)
                        //     .load(uri)
                        //   .placeholder(R.mipmap.ic_launcher)
                        // .into(binding.image)

                    }.addOnFailureListener {exception ->

                        when(activity){
                            is UserProfileActivity -> {
                                activity.hideProgressDialog()
                            }
                            is AddProductActivity ->{
                                activity.hideProgressDialog()
                            }
                        }

                        //   Toast.makeText(this,"faild ${exception.message}", Toast.LENGTH_LONG).show()
                        Log.e(javaClass.simpleName,exception.message,exception)
                    }
            }
    }

    fun updateUserProfile(activity: Activity,userHashMap: HashMap<String,Any>){

        mFireStore.collection(Constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity){
                    is UserProfileActivity ->{
                        activity.userProfileUpdateSuccess()
                    }
                }

            }
            .addOnFailureListener { e->
                when(activity){
                    is UserProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

   fun uploadProductDetails(activity: AddProductActivity,productinfo:Product){
       mFireStore.collection(Constants.PRODUCT)
               .document()
               .set(productinfo, SetOptions.merge())
               .addOnSuccessListener {

                   activity.productUploadSuccess()
               }
               .addOnFailureListener { e->
                   activity.hideProgressDialog()
                 Log.e(
                         activity.javaClass.simpleName,
                         "Error while uploading the product details",
                         e
                 )

               }
   }

    fun getProductDetails(activity: ProductDetailsActivity,productId: String){
        mFireStore.collection(Constants.PRODUCT)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName,document.toString())

                val product = document.toObject(Product::class.java)
                if (product!=null){
                activity.productDetailsSuccess(product) }



            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting the product details.",e)
            }
    }

    fun deleteProduct(fragment: ProductsFragment,productId:String){
        mFireStore.collection(Constants.PRODUCT)
                .document(productId)
                .delete()
                .addOnSuccessListener {
                            fragment.productDeleteSuccess()

                }.addOnFailureListener { e->

                  fragment.hideProgressDialog()
                  Log.e(
                          fragment.requireActivity().javaClass.simpleName,
                          "Error while deleting the product.",
                          e
                  )
                }
    }

   fun getProductsList(fragment: Fragment){
       mFireStore.collection(Constants.PRODUCT)
           .whereEqualTo(Constants.USER_ID,getCurrentUserID())
           .get()
           .addOnSuccessListener { document ->
               Log.e("Product List",document.documents.toString())
               val productsList:ArrayList<Product> = ArrayList()
               for(i in document.documents){
                   val product = i.toObject(Product::class.java)
                  product!!.product_id = i.id
                   productsList.add(product)
               }

               when(fragment){
                   is ProductsFragment -> {
                       fragment.successProductsListFromFireStore(productsList)
                   }
               }

           }
               .addOnFailureListener { e ->
                   // Hide the progress dialog if there is any error based on the base class instance.
                   when (fragment) {
                       is ProductsFragment -> {
                           fragment.hideProgressDialog()
                       }
                   }
                   Log.e("Get Product List", "Error while getting product list.", e)
               }
   }

    fun getDashboardItemsList(fragment: DashboardFragment) {

        mFireStore.collection(Constants.PRODUCT)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val productsList:ArrayList<Product> = ArrayList()

                for(i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)

            }


                        fragment.successProductsListFromFireStore(productsList)

                }
            .addOnFailureListener {e ->

                       fragment.hideProgressDialog()

                Log.e("Get Product List", "Error while getting product list.", e)
            }


    }
}