package com.example.myshop.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class MSPButton(context:Context,attributeSet: AttributeSet):AppCompatButton(context,attributeSet) {

    init {
        applyFont()
    }

   fun applyFont(){
     val typeface:Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
     setTypeface(typeface)
   }
}