package com.example.project6

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.TextView
import java.util.Timer

class MainActivity : Activity() {
    private lateinit var gameView : GameView
    private lateinit var game : Pong
    private lateinit var detector : GestureDetector
    private lateinit var scoreTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var width : Int = Resources.getSystem().displayMetrics.widthPixels
        var height : Int = Resources.getSystem().displayMetrics.heightPixels

        var statusBarId : Int = resources.getIdentifier( "status_bar_height", "dimen", "android" )
        var statusBarHeight : Int  = resources.getDimensionPixelSize( statusBarId )

        gameView = GameView( this, width, height - statusBarHeight )
        game = gameView.getGame()
        setContentView( gameView )
        var handler : TouchHandler = TouchHandler( )
        detector = GestureDetector( this, handler )
        detector.setOnDoubleTapListener( handler )

        var gameTimer : Timer = Timer( )
        var gameTimerTask : GameTimerTask = GameTimerTask( this )
        gameTimer.schedule( gameTimerTask, 0L, GameView.DELTA_TIME.toLong() )

        scoreTextView = TextView(this)
        scoreTextView.text = "Score: " + game.getScore()
        scoreTextView.textSize = 24f
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM

        addContentView(scoreTextView, layoutParams)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if( event != null )
            detector.onTouchEvent( event )
        return super.onTouchEvent(event)
    }

    fun updateModel( ) {
        if(game.getStartCheck()){
            game.bounceBall(this)
            runOnUiThread {
                scoreTextView.text = "Score: " + game.getScore()
            }
            if(game.gameOver()){
                runOnUiThread {
                    scoreTextView.text = "Game Over! Final score: " + game.getScore() + "\nYour High Score is: " + game.getHighScoreFromPreferences(this).toString()
                }
            }
        }
    }

    fun updateView( ) {
        gameView.postInvalidate()
    }

    inner class TouchHandler : GestureDetector.SimpleOnGestureListener ( ) {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            game.startBounce()
            return super.onSingleTapConfirmed(e)
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            game.startBounce()

            game.movePaddle(-distanceX.toInt())
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

    }
}