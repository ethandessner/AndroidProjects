package com.example.project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mortgage = Mortgage(30, 200000.0, 0.06)
        val monthlyPay = mortgage.monthPayString()
        val totalPay = mortgage.totalPayString()

        Log.w("MainActivity", "Monthly payment is $monthlyPay")
        Log.w("MainActivity", "Total payment is $totalPay")

        mortgage.setAmount(300000.0)
        mortgage.setInterest(0.065)
        mortgage.setYears(15)

        val newMonth = mortgage.monthPayString()
        val newPay = mortgage.totalPayString()

        Log.w("MainActivity", "Monthly payment is $newMonth")
        Log.w("MainActivity", "Total payment is $newPay")
    }
}