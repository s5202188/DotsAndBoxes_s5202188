package uk.ac.bournemouth.ap.dotsandboxes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PlayGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game)

        supportActionBar!!.title = "Dots And Boxes"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

}
