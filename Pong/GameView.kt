package com.example.project6

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

class GameView : View {
    private lateinit var paint : Paint
    private var width : Int = 0
    private var height : Int  = 0

    private lateinit var paddleRect : Rect

    private lateinit var game : Pong

    constructor(context : Context, width : Int, height : Int) : super(context){
        this.width = width
        this.height = height

        paint = Paint()
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        paint.strokeWidth = 20.0f

        val lineWidth = width / 5 // One-fifth of the screen width
        val lineY = (height - (height / 10)).toFloat() // Y-coordinate for the line

        game = Pong(10, width, height, context)

        val paddleLeft = game.getPaddleCenter().x - (lineWidth / 2)
        val paddleRight = game.getPaddleCenter().x + (lineWidth / 2)
        paddleRect = Rect(paddleLeft, lineY.toInt(), paddleRight, lineY.toInt())

        game.setPaddleRect(paddleRect)
        game.setBallSpeed(width * 0.0002f)
        game.setBallCenter(width / 2f, game.getBallRadius().toFloat())
        game.setGameRect(Rect(0, 0, width, height))
        game.setDeltaTime( DELTA_TIME )
    }

    fun getGame() : Pong {
        return game
    }

    override fun onDraw(canvas : Canvas){
        super.onDraw(canvas)

        //draw the paddle
        val lineCenterX = width - (width / 2) // Center X in the last fourth of the screen
        val lineWidth = width / 5 // One-fifth of the screen width
        val lineY = (height - (height / 10)).toFloat() // Y-coordinate for the line

        val paddleLeft = game.getPaddleCenter().x - (lineWidth / 2)
        val paddleRight = game.getPaddleCenter().x + (lineWidth / 2)
        paddleRect.set(paddleLeft, lineY.toInt(), paddleRight, lineY.toInt() + 10)

        canvas.drawCircle(game.getBallCenter().x.toFloat(), game.getBallCenter().y.toFloat(), game.getBallRadius().toFloat(), paint)
        canvas.drawRect(paddleRect, paint)
    }

    companion object {
        const val DELTA_TIME : Int  = 200
    }

}