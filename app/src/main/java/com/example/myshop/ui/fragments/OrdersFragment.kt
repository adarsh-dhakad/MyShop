package com.example.myshop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.Order
import com.example.myshop.ui.adapters.MyOrdersListAdapter


class OrdersFragment : BaseFragment() {



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_orders, container, false)
       // val textView: TextView = root.findViewById(R.id.text_notifications)
        return root
    }

    fun populateOrdersListInUI(ordersList:ArrayList<Order>) {
       hideProgressDialog()

        val rvMyOrderItems = view?.findViewById<RecyclerView>(R.id.rv_my_order_items)
        val tvNoOrderFound = view?.findViewById<TextView>(R.id.tv_no_orders_found)

        if (ordersList.size > 0) {
            rvMyOrderItems?.visibility = View.VISIBLE
            tvNoOrderFound?.visibility = View.GONE

            rvMyOrderItems?.layoutManager = LinearLayoutManager(activity)
            rvMyOrderItems?.setHasFixedSize(true)

            val myOrdersListAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            rvMyOrderItems?.adapter = myOrdersListAdapter
        } else {
          rvMyOrderItems?.visibility = View.GONE
          tvNoOrderFound?.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }

    private fun getMyOrdersList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getMyOrdersList(this@OrdersFragment)
    }
}