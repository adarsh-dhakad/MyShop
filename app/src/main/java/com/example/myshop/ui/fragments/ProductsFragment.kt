package com.example.myshop.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R

import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.Product
import com.example.myshop.ui.activities.AddProductActivity
import com.example.myshop.ui.adapters.MyProductsListAdapter

class ProductsFragment : BaseFragment() {


override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // if we want to use the option menu in fragment we need to add it.
    setHasOptionsMenu(true)
}
    fun deleteProduct(productID:String){

        showAlertDialogToDeleteProduct(productID)

    }

    fun productDeleteSuccess(){

        hideProgressDialog()

        Toast.makeText(requireActivity(),resources.getString(R.string.product_delete_success_message),
                Toast.LENGTH_SHORT).show()

        getProductListFromFireStore()
    }
   private fun showAlertDialogToDeleteProduct(productID: String){
        val builder = AlertDialog.Builder(requireActivity())
       // set title
       builder.setTitle(resources.getString(R.string.delete_dialog_title))
       builder.setMessage(resources.getString(R.string.delete_dialog_message))
       builder.setIcon(android.R.drawable.ic_dialog_alert)
       // performing positive action
       builder.setPositiveButton(resources.getString(R.string.yes)){ dialogInterface, _ ->

           showProgressDialog(resources.getString(R.string.please_wait))
           FirestoreClass().deleteProduct(this,productID)
           dialogInterface.dismiss()

       }
       // performing nagative action
       builder.setNegativeButton(resources.getString(R.string.no)){ dialogInterface,_ ->
           dialogInterface.dismiss()

       }

       val alertDialog:AlertDialog = builder.create()
       // Set other dialog properties
       alertDialog.setCancelable(false)
       alertDialog.show()
   }

  fun successProductsListFromFireStore(productsList : ArrayList<Product>){
      hideProgressDialog()
  //    for (i in productsList){
  //        i.title?.let { Log.i("Product Name" , it) }
   //   }
      if (productsList.size > 0){
         val rvMyProductItems = view?.findViewById<RecyclerView>(R.id.rv_my_product_items);
         val  tvNoProductsFound =view?.findViewById<TextView>(R.id.tv_no_products_found);
          if(rvMyProductItems != null) {
              rvMyProductItems.visibility = View.VISIBLE
              if (tvNoProductsFound != null) {
                  tvNoProductsFound.visibility = View.GONE
              }

              rvMyProductItems.layoutManager =LinearLayoutManager(activity)
              rvMyProductItems.setHasFixedSize(true)
              val adapterProducts = MyProductsListAdapter(requireActivity(),productsList,this)
              rvMyProductItems.adapter = adapterProducts
          }


      }else{
          val rvMyProductItems = view?.findViewById<RecyclerView>(R.id.rv_my_product_items);
          val  tvNoProductsFound =view?.findViewById<TextView>(R.id.tv_no_products_found);
          if (rvMyProductItems != null) {
              rvMyProductItems.visibility = View.GONE
          }
          if (tvNoProductsFound != null) {
              tvNoProductsFound.visibility = View.VISIBLE
          }

      }
  }

    private fun getProductListFromFireStore(){
      showProgressDialog(resources.getString(R.string.please_wait))
      FirestoreClass().getProductsList(this)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
     //   homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_products, container, false)


        return root  }
     // option for add product
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu,menu)
        super.onCreateOptionsMenu( menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.action_add_product ->{
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}