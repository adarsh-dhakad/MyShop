package com.example.myshop.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myshop.models.*
import com.example.myshop.ui.activities.*
import com.example.myshop.ui.fragments.DashboardFragment
import com.example.myshop.ui.fragments.OrdersFragment
import com.example.myshop.ui.fragments.ProductsFragment
import com.example.myshop.ui.fragments.SoldProductsFragment
import com.example.myshop.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FirestoreClass {

    // Access a Cloud Firestore instance.
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.uid!!)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
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

    fun getCurrentUser():FirebaseUser? {

      return FirebaseAuth.getInstance().currentUser
    }

    /**
     * A function to get the user id of current logged user.
     */

    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    /**
     * A function to get the logged user details from from FireStore Database.
     */
    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MYSHOP_PREFENCES,
                        Context.MODE_PRIVATE
                    )

                // Create an instance of the editor which is help us to edit the SharedPreference.
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }

                    is SettingsActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userDetailsSuccess(user)
                    }

                    is CheckoutActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
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


    /**
     * A function to update the user profile data into the database.
     *
     * @param activity The activity is used for identifying the Base activity to which the result is passed.
     * @param userHashMap HashMap of fields which are to be updated.
     */
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.USERS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {

                // Notify the success result.
                when (activity) {
                    is UserProfileActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
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

    // A function to upload the image to the cloud storage.
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    /**
     * A function to make an entry of the user's product in the cloud firestore database.
     */
    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {

        mFireStore.collection(Constants.PRODUCT)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }

    /**
     * A function to get the products list from cloud firestore.
     *
     * @param fragment The fragment is passed as parameter as the function is called from fragment and need to the success result.
     */
    fun getProductsList(fragment: Fragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCT)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when (fragment) {
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

    /**
     * A function to get the dashboard items list. The list will be an overall items list, not based on the user's id.
     */
    fun getDashboardItemsList(fragment: DashboardFragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCT)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }

                // Pass the success result to the base fragment.
                fragment.successProductsListFromFireStore(productsList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }
    }

    fun updateMyCart(context: Context,cart_id: String,itemHashMap:HashMap<String,Any>){
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                  when(context){
                      is CartListActivity ->{
                          context.itemUpdateSuccess()
                      }
                  }
            }
            .addOnFailureListener {
                    e->
                  when(context){
                      is CartListActivity ->{
                          context.hideProgressDialog()
                      }
                  }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                e
                )
            }
    }

    fun removeItemFromCart(context: Context,cart_id:String){
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when(context){
                    is CartListActivity ->{
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when(context){
                    is CartListActivity -> {
                      context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,"Error while removing the item from the cart list.",
                    e
                )
            }
    }

    fun addCartItems(activity: ProductDetailsActivity,addToCart:CartItem){
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
              activity.addToCartSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding item to card.",
                e
                )
            }
    }

    fun getCartList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<CartItem> = ArrayList()
                for (i in document.documents) {
                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id
                    list.add(cartItem)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemList(list)
                    }
                    is CheckoutActivity ->{
                        activity.successCartItemList(list)
                    }
                }

            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing card list.",
                    e
                )
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }

                }
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity,productId: String){
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID,productId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName,document.documents.toString())
                if(document.documents.size >0){
                    activity.productExistsInCart()
                }else{
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener {  e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing card list.",
                    e
                )
            }
    }
    /**
     * A function to delete the product from the cloud firestore.
     */
    fun deleteProduct(fragment: ProductsFragment, productId: String) {

        mFireStore.collection(Constants.PRODUCT)
            .document(productId)
            .delete()
            .addOnSuccessListener {

                // Notify the success result to the base class.
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    fun getSoldProductsList(fragment:SoldProductsFragment){
        mFireStore.collection(Constants.SOLD_PRODUCT)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val list:ArrayList<SoldProduct> = ArrayList()
                for (i in document.documents){
                   val soldProduct = i.toObject(SoldProduct::class.java)!!
                    soldProduct.id = i.id
                    Log.i(
                        fragment.javaClass.simpleName,
                        "product get success ${soldProduct.title}"

                        )
                    list.add(soldProduct)
                }
            fragment.successSoldProductsList(list)
            }.addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                   fragment.javaClass.simpleName,
                    "Error while getting the list of aold product.",
                    e
                )
            }

    }

    fun getMyOrdersList(fragment:OrdersFragment){
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val list:ArrayList<Order> = ArrayList()

                for (i in document.documents){

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)

                }

                fragment.populateOrdersListInUI(list)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName , "Error while getting the orders list.",e)
            }
    }

    fun updateAllDetails(activity:CheckoutActivity,cartList:ArrayList<CartItem> , order:Order){
        val writeBatch = mFireStore.batch()

        for(cartItem in cartList){

            val productHashMap = HashMap<String,Any>()
            productHashMap[Constants.STOCK_QUANTITY] =
                 (cartItem.stock_quantity!!.toInt() - cartItem.cart_quantity!!.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.PRODUCT)
                .document(cartItem.product_id)

            writeBatch.update(documentReference,productHashMap)
        }



      for(cartItem in cartList){

       //     val productHashMap = HashMap<String,Any>()

        //    productHashMap[Constants.STOCK_QUANTITY] =
       //         (cartItem.stock_quantity!!.toInt() - cartItem.cart_quantity!!.toInt()).toString()




           val soldProduct = SoldProduct(
               cartItem.product_owner_id,
               cartItem.title,
               cartItem.price,
               cartItem.cart_quantity,
               cartItem.image,
               order.title,
               order.order_datetime,
               order.sub_total_amount,
               order.shipping_charge,
               order.total_amount,
               order.address

           )

            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCT)
                .document(cartItem.product_id)

            writeBatch.set(documentReference,soldProduct)
        }

        for (cartItem in cartList){

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cartItem.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {
          activity.allDetailsUpdatedSuccessfully()

        }.addOnFailureListener { e ->


            activity.hideProgressDialog()

            Log.e(activity.javaClass.simpleName , "Error while updating all the details after order placed.",
            e )
        }

    }

    /**
     * A function to get the product details based on the product id.
     */
    fun getProductDetails(activity:ProductDetailsActivity, productId: String) {

        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCT)
            .document(productId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the product details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val product = document.toObject(Product::class.java)!!
                when(activity){
                    is ProductDetailsActivity ->{
                        activity.productDetailsSuccess(product)
                    }
                }


            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                when(activity){
                    is ProductDetailsActivity ->{
                        activity.hideProgressDialog()
                    }
                }


                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order){
        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order,SetOptions.merge())
            .addOnSuccessListener {
                  activity.orderPlacedSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while placing an order",
                e
                )
            }
    }

    fun getAddressesList(activity:AddressListActivity){

        GlobalScope.launch(Dispatchers.IO){

            mFireStore.collection(Constants.ADDRESSES)
                .whereEqualTo(Constants.USER_ID,getCurrentUserID())
                .get()
                .addOnSuccessListener {
                        document ->
                    Log.e(activity.javaClass.simpleName,document.documents.toString())
                    val addressList:ArrayList<Address> = ArrayList()
                    for (i in document.documents){
                        val address = i.toObject(Address::class.java)!!
                        address.id = i.id
                        addressList.add(address)
                    }
                    activity.successAddressListFromFirestore(addressList)
                }
                .addOnFailureListener {
                        e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,
                        "Error while getting the address list.",
                        e)
                }



        }

    }

    fun getAllProductList(activity: Activity){
        mFireStore.collection(Constants.PRODUCT)
            .get()
            .addOnSuccessListener { document->
             Log.e("Product List",document.documents.toString())
                val productsList:ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when(activity){
                    is CheckoutActivity ->{
                        activity.successProductsListFromFireStore(productsList)
                    }
                   is CartListActivity -> {
                       activity.successProductsListFromFireStore(productsList)
                   }
                }

             }
            .addOnFailureListener { e ->
                when(activity){
                    is CheckoutActivity ->{
                        activity.hideProgressDialog()
                    }
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e("Get Product List","Error while getting all product list.",e)
            }
    }
    fun updateAddress(activity: AddEditAddressActivity, addressInfo:Address,id:String){
        mFireStore.collection(Constants.ADDRESSES)
            .document(id)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the address.",
                    e
                )
            }

    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo)
            .addOnSuccessListener {
              activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                   activity.javaClass.simpleName,
                    "Error while adding the address.",
                      e
                )


            }
               
    }
}