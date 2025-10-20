package com.example.project5

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startWrite(v : View){
        var task : Project5Write = Project5Write( this )
        task.start()
    }
    fun startRead(v : View){
        var task : Project5Read = Project5Read( this )
        task.start()
    }

    fun updateViewWithJson( s : String ) {
        try {
            var jsonObject: JSONObject = JSONObject(s)
            var result : String = jsonObject.getString( "result" )
            var tv: TextView = findViewById<TextView>(R.id.tv)
            tv.text = result.toString()
        } catch ( je : JSONException) {
            Log.w( "MainActivity", "Json exception is " + je.message )
        }
    }

    fun updateRead( s : String ) {
        try {
            var jsonObject: JSONObject = JSONObject(s)
            var data : JSONArray = jsonObject.getJSONArray( "data" )
            var tv: TextView = findViewById<TextView>(R.id.tv)

            if(jsonObject.getString("found") == "no"){
                tv.text = "NA"
            }else if(jsonObject.getString("found") == "yes"){
                if(data[2].toString() == "red"){
                    tv.setBackgroundColor(Color.RED)
                }else if(data[2].toString() == "blue"){
                    tv.setBackgroundColor(Color.BLUE)
                }else if(data[2].toString() == "green"){
                    tv.setBackgroundColor(Color.GREEN)
                }else if(data[2].toString() == "yellow"){
                    tv.setBackgroundColor(Color.YELLOW)
                }else if(data[2].toString() == "cyan"){
                    tv.setBackgroundColor(Color.CYAN)
                }
                tv.text = data[0].toString() + " will be " + data[1].toString() + " years old"
            }

        } catch ( je : JSONException) {
            Log.w( "MainActivity", "Json exception is " + je.message )
        }
    }
    companion object {
        val URL_PHP_POST : String = "https://cmsc436-2301.cs.umd.edu/project5Write.php"
        val URL_PHP_GET : String = "https://cmsc436-2301.cs.umd.edu/project5Read.php"
    }
}