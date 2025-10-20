package com.example.project3

import java.text.NumberFormat

class Travel {
    private var location : String
    private var exRate : Double
    private var morning : String
    private var please : String
    private var visa : String
    private var currency : String

    init{
        location = ""
        exRate = 0.0
        morning = ""
        please = ""
        visa = ""
        currency = ""
    }

    constructor(newLocation : String, newExRate : Double){
        setLocation(newLocation)
        setExRate(newExRate)
    }

    fun setLocation(location : String){
        this.location = location
    }

    fun setExRate(exRate : Double){
        this.exRate = exRate
    }
    fun setMorning(morning : String){
        this.morning = morning
    }
    fun setPlease(please : String){
        this.please = please
    }
    fun setVisa(yn : String){
        this.visa = yn
    }
    fun setCurrency(currency : String){
        this.currency = currency
    }
    fun getCurrency() : String{
        return this.currency
    }
    fun getVisa() : String{
        return this.visa
    }
    fun getExRate() : Double{
        return this.exRate
    }
    fun getMorning() : String{
        return this.morning
    }
    fun getPlease() : String{
        return this.please
    }
    fun setIndia(){
        setLocation("India")
        setMorning("शुभ प्रभात")
        setPlease("कृपया")
        setVisa("Yes")
        setCurrency("Rupees")
    }
    fun setTurkey(){
        setLocation("Turkey")
        setMorning("Günaydın")
        setPlease("Lütfen")
        setVisa("Yes")
        setCurrency("Lira")
    }
    fun setMexico(){
        setLocation("Mexico")
        setMorning("Buenos Dias")
        setPlease("Por Favor")
        setVisa("No")
        setCurrency("Pesos")
    }
    fun setItaly(){
        setLocation("Italy")
        setMorning("Buongiorno")
        setPlease("Per Favore")
        setVisa("No, yes if > 3 months")
        setCurrency("Euros")
    }
    fun setOutput(amountString : String) : String {
        val amount : Double = amountString.toDouble()
        val numberFormat = NumberFormat.getCurrencyInstance()
        return (numberFormat.format(amount * MainActivity.travel.getExRate())).toString() + " " + MainActivity.travel.getCurrency()
    }
}