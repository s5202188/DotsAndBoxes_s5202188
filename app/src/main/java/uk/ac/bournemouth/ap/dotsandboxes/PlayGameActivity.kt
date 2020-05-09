package uk.ac.bournemouth.ap.dotsandboxes

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.content.Context;


class PlayGameActivity : AppCompatActivity() {

    val settings : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        supportActionBar!!.title = "Dots And Boxes"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


    }

    fun getSettings() {
        val settings : SharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE)
        var playerName = settings.getString("PlayerName", "").toString()
        var gridColumns = settings.getString("GridColumns", "").toString().toInt()
        var gridRows = settings.getString("gridRows", "").toString().toInt()

    }
}