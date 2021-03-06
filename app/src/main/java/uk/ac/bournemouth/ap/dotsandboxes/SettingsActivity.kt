package uk.ac.bournemouth.ap.dotsandboxes

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    var playerName = "Player1"
    var gridColumns = 3
    var gridRows = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar!!.title = "Dots And Boxes"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    fun onSave(view: View) {
        //get text views
        val inputPlayerName = (findViewById<EditText>(R.id.editNameText)).text.toString()
        val inputGridColumns = (findViewById<EditText>(R.id.editColumns)).text.toString()
        val inputGridRows = (findViewById<EditText>(R.id.editRows)).text.toString()
        //if any text views are empty
        if (inputPlayerName.trim().isNotEmpty()) {
            playerName = inputPlayerName
        } else {
            playerName = "Player1"
        }
        if (inputGridColumns.trim().isNotEmpty()) {
            gridColumns = inputGridColumns.toInt()
        } else {
            gridColumns = 3
        }
        if (inputGridRows.trim().isNotEmpty()) {
            gridRows = inputGridRows.toInt()
        } else {
            gridRows = 3
        }

        val settings = getSharedPreferences("UserSettings", 0)
        val editor = settings.edit()
        editor.putString("PlayerName", playerName)
        editor.putInt("GridColumns", gridColumns)
        editor.putInt("GridRows", gridRows)
        editor.commit()

        val toast = Toast.makeText(this@SettingsActivity, "saved", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 140)
        toast.show()
    }

    fun onDefault(view: View) {
        val settings = getSharedPreferences("UserSettings", 0)
        val editor = settings.edit()
        editor.clear()
        editor.commit()

        val toast = Toast.makeText(this@SettingsActivity, "Default Settings", Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 140)
        toast.show()

        playerName = "Player1"
        gridColumns = 3
        gridRows = 3
    }

//    fun getSettings(): List<String> {
//        val settings = getSharedPreferences("UserSettings", 0)
//        val inputPlayerName = settings.getString("PlayerName", "").toString()
//        val inputGridRows = settings.getInt("gridRows", 3).toString()
//        val inputGridColumns = settings.getInt("GridColumns", 3).toString()
//        return listOf(inputPlayerName, inputGridRows, inputGridColumns)
//    }
}

