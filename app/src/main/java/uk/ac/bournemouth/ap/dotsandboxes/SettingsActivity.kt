package uk.ac.bournemouth.ap.dotsandboxes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar!!.title = "Dots And Boxes"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun sendMessage(AppCompatActivity: AppCompatActivity) {
        val playerName = findViewById<EditText>(R.id.editNameText)
        val gridColumns = findViewById<EditText>(R.id.editColumns)
        val gridRows = findViewById<EditText>(R.id.editRows)
        val nameMessage = playerName.text.toString()
        val gridCMessage = gridColumns.text.toString().toInt()
        val gridRMessage = gridRows.text.toString().toInt()
        val intent = Intent(this, PlayGameActivity::class.java).apply {
            putExtra("ID_EXTRA1", nameMessage)
            putExtra("ID_EXTRA2", gridCMessage)
            putExtra("ID_EXTRA3", gridRMessage)
        }
        startActivity(intent)
    }
}


