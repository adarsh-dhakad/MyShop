package com.example.myshop.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.myshop.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadUserPicture(image: Any, imageView: ImageView){

        try{
            Glide
                    .with(context)
                    // .load(Uri.parse(imageURI.toString()))
                    .load(image) //uri of the image
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_placeholder)
                    .into(imageView)
        }catch (e: IOException){
            e.printStackTrace()
        }

    }

    fun loadProductPicture(image: Any, imageView: ImageView) {

        try{
            Glide
                    .with(context)
                    // .load(Uri.parse(imageURI.toString()))
                    .load(image) //uri of the image
                    .centerCrop()

                    .into(imageView)
        }catch (e: IOException){
            e.printStackTrace()
        }

    }

}