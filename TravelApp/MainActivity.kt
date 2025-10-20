package com.example.project3

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton

class MainActivity : AppCompatActivity() {
    lateinit var loc_one : RadioButton
    lateinit var loc_two : RadioButton
    lateinit var loc_three : RadioButton
    lateinit var loc_four : RadioButton
    lateinit var exRate : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.w("MainActivity", "inside MainActivity")

        travel = Travel("", 0.0)

        loc_one = findViewById(R.id.loc_one)
        loc_two = findViewById(R.id.loc_two)
        loc_three = findViewById(R.id.loc_three)
        loc_four = findViewById(R.id.loc_four)
        exRate = findViewById(R.id.data_amount)

    }


    fun modifyData(v : View){
        if(loc_one.isChecked){
            travel.setIndia()
        }else if (loc_two.isChecked){
            travel.setTurkey()
        }else if (loc_three.isChecked){
            travel.setMexico()
        }else if (loc_four.isChecked){
           travel.setItaly()
        }
        if(TextUtils.isEmpty(exRate.getText()) && (!loc_one.isChecked || !loc_two.isChecked || !loc_three.isChecked || !loc_four.isChecked)){
            Log.w("MainActivity", "Can't move on yet!")
        }else{
            travel.setExRate(exRate.text.toString().toDouble())
            val intent : Intent = Intent(this, DataActivity::class.java)
            startActivity( intent, ActivityOptions.makeSceneTransitionAnimation( this ).toBundle())
        }
    }


    companion object {
        lateinit var travel: Travel
    }

}