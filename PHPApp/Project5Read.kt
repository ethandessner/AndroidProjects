package com.example.project5

import android.util.Log
import java.io.InputStream
import java.net.URL
import java.util.Scanner

class Project5Read : Thread {

    lateinit var activity : MainActivity
    var result : String = "NOT SET YET"

    constructor( fromActivity : MainActivity ) {
        activity = fromActivity
    }

    override fun run( ) {
        val url : URL = URL( MainActivity.URL_PHP_GET + "?email=edessner@umd.edu" )
        val iStream : InputStream = url.openStream()
        // read from iStream
        val scan : Scanner = Scanner( iStream )
        result = ""
        while( scan.hasNext( ) ) {
            result += scan.nextLine( )
        }
        Log.w( "MainActivity", "result is " + result )

        // update GUI
        var updateGui : UpdateGui = UpdateGui( )
        activity.runOnUiThread( updateGui )
    }

    inner class UpdateGui : Runnable {
        override fun run() {
            Log.w( "MainActivity", "Inside UpdateGui:run" )
            activity.updateRead( result )
        }
    }
}