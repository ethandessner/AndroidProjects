package com.example.project3

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat

class DataActivity : AppCompatActivity() {
    lateinit var amountET : EditText
    lateinit var output : TextView
    lateinit var visaCheck : TextView
    lateinit var morning : TextView
    lateinit var please : TextView
    lateinit var visa : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        Log.w("MainActivity", "inside MainActivity")

        amountET = findViewById(R.id.input)
        output = findViewById(R.id.output)
        visaCheck = findViewById(R.id.visa)
        morning = findViewById(R.id.translation)
        please = findViewById(R.id.pleasetrans)
        visa = findViewById(R.id.visa)

        updateView()
    }

    fun updateView(){
        morning.text = MainActivity.travel.getMorning()
        please.text = MainActivity.travel.getPlease()
        visa.text = MainActivity.travel.getVisa()
    }

    fun updateData(v : View){
//        Updating and staying on this page
         output.text = MainActivity.travel.setOutput(amountET.text.toString())
    }

    fun goBack(v : View){
//        going back to main page
        finish()
    }

}