package uk.ac.bournemouth.ap.dotsandboxes

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.content.Context;
import org.example.student.dotsboxgame.StudentDotsBoxGame


class PlayGameActivity : AppCompatActivity() {
    var playerName = "Player1"

    lateinit var mGameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        supportActionBar!!.title = "Dots And Boxes"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val settings : SharedPreferences = getSharedPreferences("UserSettings", 0)
        val inputPlayerName = settings.getString("PlayerName", "").toString()
        val inputGridCol = settings.getInt("GridColumns", 3)
        val inputGridRows = settings.getInt("gridRows", 3)

        if (inputPlayerName.trim().isNotEmpty()) {
            playerName = inputPlayerName
        } else {
            playerName = "Player1"
        }

//        mGameView = GameView(this)
//        mGameView.mGame = StudentDotsBoxGame(inputGridCol, inputGridRows, listOf(StudentDotsBoxGame.User("Player1"), StudentDotsBoxGame.PlayerComputer("Computer")))
//        setContentView(mGameView)

    }

}