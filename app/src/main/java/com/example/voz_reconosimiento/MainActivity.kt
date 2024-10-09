package com.example.voz_reconosimiento
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
class MainActivity : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textView: TextView
    private lateinit var buttonSpeak: Button
    // Mapa de colores reconocibles
    private val colorsMap = mapOf(
        "rojo" to android.graphics.Color.RED,
        "verde" to android.graphics.Color.GREEN,
        "azul" to android.graphics.Color.BLUE,
        "amarillo" to android.graphics.Color.YELLOW,
        "negro" to android.graphics.Color.BLACK,
        "blanco" to android.graphics.Color.WHITE
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        buttonSpeak = findViewById(R.id.buttonSpeak)
        // Verificar permisos de micrófono
        checkPermissions()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        buttonSpeak.setOnClickListener {
            startListening()
        }
    }
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }
    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                textView.text = "Escuchando..."
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                textView.text = "Error: $error"
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    val spokenColor = matches[0].toLowerCase(Locale.getDefault())
                    textView.text = "Color reconocido: $spokenColor"
                    changeBackgroundColor(spokenColor)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        speechRecognizer.startListening(intent)
    }
    private fun changeBackgroundColor(color: String) {
        val colorValue = colorsMap[color]
        if (colorValue != null) {
            window.decorView.setBackgroundColor(colorValue)
        } else {
            textView.text = "Color no reconocido"
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}