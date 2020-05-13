package uk.ac.bournemouth.ap.dotsandboxes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button


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

        /*
        val settings : SharedPreferences = getSharedPreferences("UserSettings", 0)
        val inputPlayerName = settings.getString("PlayerName", "").toString()
        val inputGridCol = settings.getInt("GridColumns", 3)
        val inputGridRows = settings.getInt("gridRows", 3)

        if (inputPlayerName.trim().isNotEmpty()) {
            playerName = inputPlayerName
        } else {
            playerName = "Player1"
        }

        mGameView = GameView(this)
        mGameView.mGame = StudentDotsBoxGame(inputGridCol, inputGridRows, listOf(StudentDotsBoxGame.User("Player1"), StudentDotsBoxGame.PlayerComputer("Computer")))
        setContentView(mGameView)
*/
    }

}