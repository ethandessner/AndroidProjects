package com.example.project1

import android.util.Log
import kotlin.math.pow
import java.text.DecimalFormat
import java.text.NumberFormat

class Mortgage {
    private var years : Int
    private var amount : Double
    private var interest : Double

    init{
        Log.w("Mortgage", "Inside init")
        years = 0
        amount = 0.0
        interest = 0.0
    }

     constructor(years : Int, amount : Double, interest : Double){
        Log.w("Mortgage", "Inside constructor")
        setYears(years)
        setAmount(amount)
        setInterest(interest)
    }

    fun getYears() : Int {
        return this.years
    }
    fun getAmount() : Double {
        return this.amount
    }
    fun getInterest() : Double {
        return this.interest
    }

    fun setYears( years : Int) {
        Log.w( "Mortgage", "Inside setYears" )
        this.years = years
    }

    fun setAmount( amount : Double) {
        Log.w( "Mortgage", "Inside setYears" )
        this.amount = amount
    }

    fun setInterest( interest : Double) {
        Log.w( "Mortgage", "Inside setYears")
        this.interest = interest
    }

    fun monthlyPayment() : Double {
        val mIR : Double = interest/12
        val a : Double = 1 / (1 + mIR)
        val b : Double = (a.pow(years * 12) - 1) / (a - 1)
        return amount / (a * b)
    }

    fun totalPayments() : Double{
        val monthPay : Double = monthlyPayment()
        val totalPay : Int = years * 12
        return monthPay * totalPay
    }

    fun monthPayString() : String{
        val monthPay : Double = monthlyPayment()
        val numberFormat = NumberFormat.getCurrencyInstance()
//        Log.w("TEST", "NUMFORMAT " + numberFormat.format(monthPay))
        return numberFormat.format(monthPay)
    }

    fun totalPayString() : String{
        val totalPay : Double = totalPayments()
        val numberFormat = NumberFormat.getCurrencyInstance()
//        Log.w("PAY", "NUMFORMAT " + numberFormat.format(totalPay))
        return numberFormat.format(totalPay)
    }
}