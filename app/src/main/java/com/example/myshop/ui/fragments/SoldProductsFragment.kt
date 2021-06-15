package com.example.myshop.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.SoldProduct
import com.example.myshop.ui.adapters.SoldProductsListAdapter


class SoldProductsFragment : BaseFragment(){



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sold_product, container, false)
    }

    override fun onResume() {
        super.onResume()

        getSoldProductsList()
    }

    private fun getSoldProductsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getSoldProductsList(this@SoldProductsFragment)
    }

    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
    Log.i("sold product fragment 123","${soldProductsList.get(0).title}")
        // Hide Progress dialog.
        hideProgressDialog()

        val rvSoldProductItems = view?.findViewById<RecyclerView>(R.id.rv_sold_product_items)
        val tvNoSoldProductsFound = view?.findViewById<TextView>(R.id.tv_no_sold_products_found)

        if (soldProductsList.size > 0) {

            rvSoldProductItems?.visibility = View.VISIBLE
            tvNoSoldProductsFound?.visibility = View.GONE

            rvSoldProductItems!!.layoutManager = LinearLayoutManager(activity)
            rvSoldProductItems.setHasFixedSize(true)

            val soldProductsListAdapter =
                SoldProductsListAdapter(requireActivity(), soldProductsList)
            rvSoldProductItems.adapter = soldProductsListAdapter
        } else {
            rvSoldProductItems?.visibility = View.GONE
            tvNoSoldProductsFound?.visibility = View.VISIBLE
        }
    }


}