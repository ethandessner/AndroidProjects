package com.example.project6

import java.util.TimerTask

class GameTimerTask : TimerTask {
    private lateinit var activity : MainActivity

    constructor( mainActivity : MainActivity ) {
        activity = mainActivity
    }

    override fun run() {
        // update the model
        activity.updateModel()
        // update the view
        activity.updateView()
    }
}