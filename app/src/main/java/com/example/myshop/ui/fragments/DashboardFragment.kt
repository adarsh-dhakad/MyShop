package com.example.myshop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.Product
import com.example.myshop.ui.activities.CartListActivity
import com.example.myshop.ui.activities.SettingsActivity
import com.example.myshop.ui.adapters.MyDashboardListAdapter

class DashboardFragment : BaseFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // if we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

    private fun getProductListFromFireStore(){

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemsList(this)
    }

   fun successProductsListFromFireStore(dashboardItemList : ArrayList<Product>){
       hideProgressDialog()
       val rvMyProductItems = view?.findViewById<RecyclerView>(R.id.rv_my_dashboard_items)
       val  tvNoDashboardItemFound =view?.findViewById<TextView>(R.id.tv_no_dashboard_item_found)
       if (dashboardItemList.size > 0){

           if(rvMyProductItems != null) {
               rvMyProductItems.visibility = View.VISIBLE
              if (tvNoDashboardItemFound != null) {
                   tvNoDashboardItemFound.visibility = View.GONE
               }
               rvMyProductItems.layoutManager = GridLayoutManager(activity,2)
               rvMyProductItems.setHasFixedSize(true)
               val adapter = MyDashboardListAdapter(requireActivity(), dashboardItemList)
               rvMyProductItems.adapter = adapter

           } }else{

           val rvMyProductItems = view?.findViewById<RecyclerView>(R.id.rv_my_dashboard_items);
           val  tvNoProductsFound =view?.findViewById<TextView>(R.id.tv_no_dashboard_item_found);
           if (rvMyProductItems != null) {
               rvMyProductItems.visibility = View.GONE
           }
           if (tvNoProductsFound != null) {
               tvNoProductsFound.visibility = View.VISIBLE

       }


   } }



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
        super.onCreateOptionsMenu( menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings ->{
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart ->{
                startActivity(Intent(activity,CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}