package com.example.project4

import android.content.Context
import android.content.Context.*
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Game : AppCompatActivity{
    private var highScore : Int
    private var currScore : Int
    private var level : Int
    private var count : Int
    var targetPattern : ArrayList<String> = ArrayList<String>(0)
    var colors = arrayOf("Blue", "Red", "Yellow", "Green")

    init{
        highScore = 0
        currScore = 0
        level = 1
        count = 0
    }

    constructor(context : Context){
        Log.w("SCORE", getHighScoreFromPreferences(context).toString())
        if(level <= getHighScoreFromPreferences(context)){
            level = getHighScoreFromPreferences(context)
        }
        highScore = getHighScoreFromPreferences(context)
        level = highScore + 1
        var check : Int = 0
        while(check < level){
            var random = (0..3).random()
            targetPattern.add(colors[random])
            check++
        }
    }


    fun correctClick(currColor: String, context: Context) {

        if (targetPattern.size > getCount() && targetPattern[getCount()] == currColor) {
            // Correct click, continue the game
            addCount()
            Log.w("Current Count", getCount().toString())
            if (targetPattern.size == getCount()) {
                // The user has correctly matched the entire pattern
                addPattern()
                resetCount()
                currScore = level
                Log.w("Current Score", getScore().toString())
                level += 1
                newToast(context)
            }
            if(getScore() > getHighScore()){
                var pref : SharedPreferences = context.getSharedPreferences( context.packageName + "_preferences", MODE_PRIVATE )
                val editor = pref.edit()
                editor.putInt(PREFERENCE_SCORE, getScore())
                editor.commit()
                Log.w("New High Score", getHighScoreFromPreferences(context).toString())
            }
        } else {
            // Incorrect click, handle the mistake
            Log.w("Mistake", "You made a mistake")
            onStop()
        }
    }

    fun getHighScoreFromPreferences(context : Context) : Int{
        val pref = context.getSharedPreferences( context.packageName + "_preferences", MODE_PRIVATE )
        return pref.getInt(PREFERENCE_SCORE, 0)
    }

    private fun addPattern(){
        var random = (0..3).random()
        targetPattern.add(colors[random])
    }

    fun newToast(context : Context){
        val t : Toast = Toast.makeText(context, targetPattern.joinToString(), Toast.LENGTH_LONG )
        t.show()
    }

    fun getScore() : Int {
       return currScore
    }

    private fun resetCount(){
        count = 0
    }
    private fun addCount(){
        count++
    }
    private fun getCount() : Int{
        return count
    }

    private fun getHighScore() : Int{
        return highScore
    }

    fun resetGame(context : Context){
        setPreferences(context)
        highScore = 0
        targetPattern.clear()
        addPattern()
        resetCount()
        level = 1
        currScore = 0
        newToast(context)
    }

    fun setPreferences( context : Context ) {
        var pref : SharedPreferences = context.getSharedPreferences( context.packageName + "_preferences", Context.MODE_PRIVATE )
        var editor : SharedPreferences.Editor = pref.edit()
        editor.putInt( PREFERENCE_SCORE, 0 )
        editor.commit()
    }

    companion object {
        private const val PREFERENCE_SCORE : String = "Score"
    }
}