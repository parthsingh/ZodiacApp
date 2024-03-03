package com.example.zodiacapp

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : ComponentActivity(), AdapterView.OnItemSelectedListener{

    var sunSign = "Aries"
    var resultView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var buttonView: Button = findViewById(R.id.button)

        findViewById<Button>(R.id.button).setOnClickListener {
            GlobalScope.async {
                getPredictions()
            }
        }

        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(this,R.array.sunsigns,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter;
        spinner.onItemSelectedListener = this

        resultView = findViewById(R.id.resultView)

        resultView!!.movementMethod = ScrollingMovementMethod()






    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        sunSign = "Aries"
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            sunSign = parent.getItemAtPosition(position).toString()
        }
    }

    suspend fun getPredictions() {
        try {
            val result = GlobalScope.async {
                callAztroAPI("https://horoscope-astrology.p.rapidapi.com/sign?s=" + sunSign)
            }.await()

            onResponse(result)

//            GlobalScope.async {
//                callAPI()
//            }.await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAztroAPI(apiUrl:String ):String?{
        var result: String? = ""

        val url: URL;
        var connection: HttpURLConnection? = null
        try {
            url = URL(apiUrl)
            connection = url.openConnection() as HttpURLConnection
            // set headers for the request
            // set host name


            // set the rapid-api key
            connection.setRequestProperty("X-RapidAPI-Key", "a580783b34msh5ddca55198c7c8bp169420jsnce6e30875511")

            connection.setRequestProperty("X-RapidAPI-Host", "horoscope-astrology.p.rapidapi.com")
           // connection.setRequestProperty("content-type", "application/x-www-form-urlencoded")
            // set the request method - POST
            connection.requestMethod = "GET"
            val `in` = connection.inputStream
            val reader = InputStreamReader(`in`)

            // read the response data
            var data = reader.read()
            while (data != -1) {
                val current = data.toChar()
                result += current
                data = reader.read()
            }
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // if not able to retrieve data return null
        return null

    }



    private fun onResponse(result: String?) {
        try {

            // convert the string to JSON object for better reading
            val resultJson = JSONObject(result)

            // Initialize prediction text
            var prediction=""
            //prediction += this.sunSign

            // Update text with various fields from response
            //prediction += resultJson.optString("date_range")+"nn"
            println(resultJson)
            //prediction += resultJson.optString("health")

            prediction += "Ruling planet: " + resultJson.optString("ruling_planet") +"\n"

            prediction += "\nSymbol: " + resultJson.optString("symbol") +"\n"

            prediction += "\nCompatibility: " + resultJson.optString("compatibility") +"\n"

            prediction += "\nStrengths: " + resultJson.optString("strengths") +"\n"

            prediction += "\nWeaknesses: " + resultJson.optString("weaknesses") +"\n"

            prediction += "\nAbout: " + resultJson.optString("about")


            //Update the prediction to the view
            println(prediction)
            setText(this.resultView,prediction)

        } catch (e: Exception) {
            e.printStackTrace()
            this.resultView!!.text = "Oops!! something went wrong, please try again"
        }
    }

    private fun setText(text: TextView?, value: String) {
        runOnUiThread { text!!.text = value }
    }

}

