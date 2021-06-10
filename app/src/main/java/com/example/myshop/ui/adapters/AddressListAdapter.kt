package com.example.myshop.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.R
import com.example.myshop.models.Address
import com.example.myshop.ui.activities.AddEditAddressActivity
import com.example.myshop.ui.activities.CheckoutActivity
import com.example.myshop.utils.Constants
import com.example.myshop.utils.MSPTextView
import com.example.myshop.utils.MSPTextViewBold

class AddressListAdapter(
    private val context: Context,
    private var list:ArrayList<Address>,
    private val selectAddress:Boolean
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_address_layout,
                parent,
                false
            )
        )
    }

    fun notifyEditItem(activity: Activity, position: Int){
          val intent = Intent(context,AddEditAddressActivity::class.java)
          intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS,list[position])
          activity.startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)
          notifyItemChanged(position)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val model = list[position]

        if(holder is MyViewHolder){
            holder.itemView.findViewById<MSPTextViewBold>(R.id.tv_address_full_name).text = model.full_name
            holder.itemView.findViewById<MSPTextView>(R.id.tv_address_type).text = model.type
            holder.itemView.findViewById<MSPTextView>(R.id.tv_address_details).text = "${model.address}, ${model.zipCode}"
            holder.itemView.findViewById<MSPTextView>(R.id.tv_address_mobile_number).text = model.mobileNumber

            if (selectAddress){
                holder.itemView.setOnClickListener {

                  val intent = Intent(context,CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS,model)
                    context.startActivity(intent)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // A viewHolder describe on item view
    private class MyViewHolder(view: View):RecyclerView.ViewHolder(view)

}