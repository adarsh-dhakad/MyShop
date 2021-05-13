package com.example.myshop.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

class MSPTextView(context:Context,attrs:AttributeSet) :androidx.appcompat.widget.AppCompatTextView(context,attrs) {
init {
    applyFont()
}


    fun applyFont(){
        val typeface: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Regular.ttf")
        setTypeface(typeface)
}


}