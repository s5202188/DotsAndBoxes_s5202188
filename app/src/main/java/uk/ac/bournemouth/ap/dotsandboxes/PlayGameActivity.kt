package uk.ac.bournemouth.ap.dotsandboxes

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.example.student.dotsboxgame.StudentDotsBoxGame


class PlayGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        supportActionBar!!.title = "Dots And Boxes"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val button = findViewById<Button>(R.id.buttonRestart)
        button.setOnClickListener {
            val intent = Intent(this, PlayGameActivity::class.java)
            startActivity(intent)
        }
        val button2 = findViewById<Button>(R.id.buttonSettings)
        button2.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

//        var pName = "Player1"
//        var row = 3
//        var col = 3
//        val settings: SharedPreferences = getSharedPreferences("UserSettings", 0)
//        val inputPlayerName = settings.getString("PlayerName", "").toString()
//        val inputGridRows = settings.getInt("gridRows", 3).toString().toInt()
//        val inputGridColumns = settings.getInt("GridColumns", 3).toString().toInt()
//        if (inputPlayerName.isNotEmpty()) {
//            pName = inputPlayerName
//            row = inputGridRows
//            col = inputGridColumns
//        }

//        var mGame = StudentDotsBoxGame(row, col, listOf(StudentDotsBoxGame.User("Player 1"), StudentDotsBoxGame.PlayerComputer("Computer 1")))
    }
}