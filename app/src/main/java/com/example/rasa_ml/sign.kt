package com.example.rasa_ml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class sign : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign)
            val secondActbutton = findViewById<Button>(R.id.btnlogin)
            secondActbutton.setOnClickListener{
                val Intent = Intent(this,predictionscreen::class.java)
                startActivity(Intent)
            }
        }
    }
