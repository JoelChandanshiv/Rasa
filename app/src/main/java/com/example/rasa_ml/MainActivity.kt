package com.example.rasa_ml

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var tflitePrimary: Interpreter
    private lateinit var tfliteSecondary: Interpreter

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            tflitePrimary = Interpreter(loadModelFile("primary_rasa_model.tflite"))
            tfliteSecondary = Interpreter(loadModelFile("secondary_rasa_model.tflite"))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val editTextGlucose: EditText = findViewById(R.id.editTextGlucose)
        val editTextSucrose: EditText = findViewById(R.id.editTextSucrose)
        val editTextFructose: EditText = findViewById(R.id.editTextFructose)
        val editTextTannins: EditText = findViewById(R.id.editTextTannins)
        val editTextPhenolicAcids: EditText = findViewById(R.id.editTextPhenolicAcids)
        val editTextCitric: EditText = findViewById(R.id.editTextCitric)
        val editTextMalic: EditText = findViewById(R.id.editTextMalic)
        val editTextTartaricAcid: EditText = findViewById(R.id.editTextTartaricAcid)
        val editTextAlkaloids: EditText = findViewById(R.id.editTextAlkaloids)
        val editTextTerpenes: EditText = findViewById(R.id.editTextTerpenes)
        // Define other EditTexts for input features

        val btnPredict: Button = findViewById(R.id.button10)
        val btnClear: Button = findViewById(R.id.btnclear)
        val textViewResultPrimary: TextView = findViewById(R.id.textViewResultPrimary)
        val textViewResultSecondary: TextView = findViewById(R.id.textViewResultSecondary)

        val gotobtn = findViewById<Button>(R.id.visualisebtn)
        gotobtn.setOnClickListener {
            val Intent = Intent(this, graph::class.java)
            startActivity(Intent)
        }

        btnPredict.setOnClickListener {
            val glucose = editTextGlucose.text.toString().toFloatOrNull() ?: 0f
            val sucrose = editTextSucrose.text.toString().toFloatOrNull() ?: 0f
            val fructose = editTextFructose.text.toString().toFloatOrNull() ?: 0f
            val tannins = editTextTannins.text.toString().toFloatOrNull() ?: 0f
            val phenolicAcids = editTextPhenolicAcids.text.toString().toFloatOrNull() ?: 0f
            val citric = editTextCitric.text.toString().toFloatOrNull() ?: 0f
            val malic = editTextMalic.text.toString().toFloatOrNull() ?: 0f
            val tartaricAcid = editTextTartaricAcid.text.toString().toFloatOrNull() ?: 0f
            val alkaloids = editTextAlkaloids.text.toString().toFloatOrNull() ?: 0f
            val terpenes = editTextTerpenes.text.toString().toFloatOrNull() ?: 0f


            // Read other feature values

            val input = Array(1) { FloatArray(10) }
            input[0][0] = glucose
            input[0][1] = sucrose
            input[0][2] = fructose
            input[0][3] = tannins
            input[0][4] = phenolicAcids
            input[0][5] = citric
            input[0][6] = malic
            input[0][7] = tartaricAcid
            input[0][8] = alkaloids
            input[0][9] = terpenes
            // Assign other feature values to input array

            val outputPrimary = Array(1) { FloatArray(4) } // Assuming 5 classes
            val outputSecondary = Array(1) { FloatArray(3) }

            tflitePrimary.run(input, outputPrimary)
            tfliteSecondary.run(input, outputSecondary)

            val predictedPrimaryRasa = getRasaLabel(outputPrimary[0])
            val predictedSecondaryRasa = getRasaLabel(outputSecondary[0])

            textViewResultPrimary.text = "Predicted Primary Rasa: $predictedPrimaryRasa"
            textViewResultSecondary.text = "Predicted Secondary Rasa: $predictedSecondaryRasa"

        }

        btnClear.setOnClickListener {
            editTextGlucose.text.clear()
            editTextSucrose.text.clear()
            editTextFructose.text.clear()
            editTextTannins.text.clear()
            editTextPhenolicAcids.text.clear()
            editTextCitric.text.clear()
            editTextMalic.text.clear()
            editTextTartaricAcid.text.clear()
            editTextAlkaloids.text.clear()
            editTextTerpenes.text.clear()
            // Clear other EditText fields if any




            textViewResultPrimary.text = ""
            textViewResultSecondary.text = ""

        }
    }


    @Throws(IOException::class)
    private fun loadModelFile(modelName: String): MappedByteBuffer {
        val fileDescriptor = assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun getRasaLabel(rasaProbabilities: FloatArray): String {
        val maxIndex = rasaProbabilities.indices.maxByOrNull { rasaProbabilities[it] } ?: -1

        val rasaLabels = mapOf(
            0 to "Madhura(Sweet)",
            1 to "Katu(Pungent)",
            2 to "Kashaya(Astringent)",
            3 to "Amla(Sour)",
            4 to "Tikta(Bitter)"
        )

        return rasaLabels[maxIndex] ?: "Unknown"
    }
}