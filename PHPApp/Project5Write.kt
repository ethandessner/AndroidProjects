package com.example.project5

import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.URL
import java.net.URLConnection

class Project5Write : Thread {

    lateinit var activity : MainActivity
    var result : String = "NOT SET YET"

    constructor( fromActivity : MainActivity ) {
        activity = fromActivity
    }

    override fun run( ) {

        try {
            val url: URL = URL(MainActivity.URL_PHP_POST)
            val connection: URLConnection = url.openConnection()
            Log.w("MainActivity", "connection is " + connection)

            // write data
            connection.doOutput = true
            val os: OutputStream = connection.getOutputStream()
            Log.w("MainActivity", "os is " + os)
            var osw : OutputStreamWriter = OutputStreamWriter( os )
            Log.w("MainActivity", "osw is " + osw)
            val data : String = "data={\"email\":\"edessner@umd.edu\",\"name\":\"Ethan\",\"number\":21}"
            Log.w("MainActivity", "data is " + data)
            osw.write( data )
            osw.flush()
            osw.close()

            // read result
            val iStream : InputStream = connection.getInputStream()
            val isr : InputStreamReader = InputStreamReader( iStream )
            val br : BufferedReader = BufferedReader( isr )
            result = ""
            var line : String? = br.readLine()
            while( line != null ) {
                result += line
                line = br.readLine()
            }

            Log.w("MainActivity", "result is " + result)

            // update GUI
            var updateGui: UpdateGui = UpdateGui()
            activity.runOnUiThread(updateGui)
        } catch( e : Exception ) {
            Log.w( "MainActivity", "excepiton: " + e.message )
        }
    }

    inner class UpdateGui : Runnable {
        override fun run() {
            Log.w( "MainActivity", "Inside UpdateGui:run" )
            activity.updateViewWithJson( result )
        }
    }
}