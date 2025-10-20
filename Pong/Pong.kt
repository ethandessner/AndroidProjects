package com.example.project6

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Point
import android.graphics.Rect
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity

class Pong : AppCompatActivity{
    private var gameRect : Rect? = null
    private var deltaTime = 0
    private var ballCenter : Point? = null
    private var ballRadius = 0
    private var ballSpeed = 0f
    private var ballAngle = 45.0
    private var paddleCenter : Point? = null
    private var paddleRect : Rect? = null
    private var paddleWidth = 0
    private var begin : Boolean = false
    private var screenWidth : Int = 0
    private var screenHeight : Int = 0
    private var currScore : Int = 0
    private var highScore : Int = 0
    private var gameOver : Boolean = false

    private lateinit var pool : SoundPool
    private var collideId : Int = 0

    constructor( newBallRadius: Int, width : Int, height : Int, context : Context) {
        highScore = getHighScoreFromPreferences(context)
        setBallRadius(newBallRadius)
        screenWidth = width
        screenHeight = height
        currScore = 0
        paddleCenter = Point(width - (width / 2), height - (height / 10))
        val poolBuilder : SoundPool.Builder = SoundPool.Builder( )
        pool = poolBuilder.build()
        collideId = pool.load( context, R.raw.hit, 1 )
    }

    fun getHighScoreFromPreferences(context : Context) : Int{
        val pref = context.getSharedPreferences( context.packageName + "_preferences", MODE_PRIVATE )
        return pref.getInt(PREFERENCE_SCORE, 0)
    }

    private fun addScore(){
        currScore++
    }

    fun getScore() : String {
        return currScore.toString()
    }

    fun setPaddleRect(newPaddleRect: Rect?){
        paddleRect = newPaddleRect
        paddleWidth = newPaddleRect!!.right - newPaddleRect!!.left
    }

    fun movePaddle(distanceX: Int) {
    // Adjust the paddle position based on the scroll distance
        paddleCenter!!.x += distanceX

    // Ensure that the paddle doesn't move beyond the screen boundaries
        if (paddleCenter!!.x - (paddleWidth / 2) < gameRect!!.left) {
        // Limit the left boundary
            paddleCenter!!.x = gameRect!!.left + (paddleWidth / 2)
        } else if (paddleCenter!!.x + (paddleWidth / 2) > gameRect!!.right) {
        // Limit the right boundary
        paddleCenter!!.x = gameRect!!.right - (paddleWidth / 2)
        }
    }

    fun getPaddleCenter() : Point{
        return paddleCenter!!
    }

    fun startBounce(){
        begin = true
    }

    fun getStartCheck() : Boolean{
        return begin
    }

    fun setDeltaTime(newDeltaTime: Int) {
        if (newDeltaTime > 0)
            deltaTime = newDeltaTime
    }

    fun setBallCenter(width : Float, height : Float){
        ballCenter = Point(width.toInt(), height.toInt())
    }

    fun setGameRect(newGameRect: Rect?) {
        if (newGameRect != null)
            gameRect = newGameRect
    }

    fun setBallRadius(newBallRadius : Int){
        if (newBallRadius > 0)
            ballRadius = newBallRadius
    }

    fun setBallSpeed(newBallSpeed : Float){
        if (newBallSpeed > 0)
            ballSpeed = newBallSpeed
    }

    fun getBallRadius() : Int {
        return ballRadius
    }

    fun getBallCenter(): Point {
        return ballCenter!!
    }

    fun gameOver() : Boolean {
        return gameOver
    }

    fun bounceBall(context : Context) {
        // Update the ball's position based on its current angle and speed
        ballCenter!!.x += (ballSpeed * Math.cos(ballAngle) * deltaTime).toInt()
        ballCenter!!.y += (ballSpeed * Math.sin(ballAngle) * deltaTime).toInt()

        // Check for collisions with the walls
        if (ballCenter!!.x - 2 * ballRadius < gameRect!!.left || ballCenter!!.x + 2 * ballRadius > gameRect!!.right) {
            // Ball hits left or right wall, so invert the x direction (reflect off the wall)
            ballAngle = Math.PI - ballAngle

        }

        if (ballCenter!!.y - (2 * ballRadius) < gameRect!!.top) {
            // Ball hits the top wall, so invert the y direction (reflect off the top wall)
            ballAngle = -ballAngle

        }

        if (ballCenter!!.x + ballRadius <= paddleCenter!!.x + (screenWidth / 10) && ballCenter!!.x + ballRadius >= paddleCenter!!.x - (screenWidth / 10)
            && ballCenter!!.y + ballRadius >= paddleCenter!!.y && ballCenter!!.y + ballRadius - (ballSpeed * Math.sin(ballAngle) * deltaTime).toInt() < paddleCenter!!.y) {
            pool.play(collideId, 500.0f, 500.0f, 2, 0, 1.0f)
            addScore()

            ballAngle = -ballAngle
        }

        if(ballCenter!!.y > paddleRect!!.bottom){
            gameOver = true
            setPreferences(context)
        }

    }

    private fun setPreferences(context : Context ) {
        if(currScore > highScore) {
            var pref: SharedPreferences = context.getSharedPreferences(
                context.packageName + "_preferences",
                Context.MODE_PRIVATE
            )
            var editor: SharedPreferences.Editor = pref.edit()
            editor.putInt(PREFERENCE_SCORE, currScore)
            editor.commit()
        }
    }

    companion object {
        private const val PREFERENCE_SCORE : String = "Score"
    }
}