package com.example.myshop.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myshop.R
import com.example.myshop.utils.MSPTextView


open class BaseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var mProgressDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

  fun showProgressDialog(text:String){

      mProgressDialog = Dialog(requireActivity())
      mProgressDialog.setContentView(R.layout.dialog_progress)
      mProgressDialog.findViewById<MSPTextView>(R.id.tv_progress_text).text = text
      mProgressDialog.setCancelable(false)
      mProgressDialog.setCanceledOnTouchOutside(false)
      mProgressDialog.show()


  }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
}