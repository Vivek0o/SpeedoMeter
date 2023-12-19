package com.example.test

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var speedometer: Speedometer
    private val handler = Handler()

    private var currentSpeed = 1f
    private lateinit var speed: TextView
    private lateinit var speed_unit: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speedometer = findViewById(R.id.speedoMeter)

//
//        // Start updating speed every 2000 milliseconds (2 seconds)
//        handler.postDelayed(updateSpeedRunnable, 500)
        val typeface = Typeface.createFromAsset(assets, "font.ttf")
        speed = findViewById(R.id.speed)
        speed_unit = findViewById(R.id.speed_unit)
        speedometer.setTypeface(typeface)

        speed.typeface = typeface
        speed_unit.typeface = typeface
    }

    private val updateSpeedRunnable = object : Runnable {
        override fun run() {
            // Set the current speed to the Speedometer view
            speedometer.setSpeed(currentSpeed)

            // Increment the speed by 1
            currentSpeed++

            // If the speed reaches 60, reset to 1
            if (currentSpeed > 60) {
                currentSpeed = 1f
            }

            // Schedule the next update after 2 seconds
            handler.postDelayed(this, 500)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the update runnable when the activity is destroyed
        handler.removeCallbacks(updateSpeedRunnable)
    }
}


