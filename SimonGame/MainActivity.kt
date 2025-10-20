package com.example.project4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        game = Game(this)
        game.newToast(this)

    }

    fun addBlue(v: View){
        game.correctClick("Blue", this)
    }
    fun addRed(v: View){
        game.correctClick("Red", this)
    }
    fun addGreen(v: View){
        game.correctClick("Green", this)
    }
    fun addYellow(v: View){
        game.correctClick("Yellow", this)
    }
    fun reset(v :View){
        game.resetGame(this)
    }

    companion object{
        lateinit var game : Game
    }
}