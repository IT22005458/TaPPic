package com.example.tappic

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tappic.R
import java.util.*

class MainActivity : AppCompatActivity() {

    var score = 0
    var highScore = 0
    var handler: Handler = Handler()
    var runnable: Runnable = Runnable { }
    var imageArray = ArrayList<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageArray = arrayListOf(
            findViewById(R.id.imageView), findViewById(R.id.imageView1), findViewById(R.id.imageView2),
            findViewById(R.id.imageView3), findViewById(R.id.imageView4), findViewById(R.id.imageView5),
            findViewById(R.id.imageView6), findViewById(R.id.imageView7), findViewById(R.id.imageView8)
        )

        // Load the high score from SharedPreferences
        val preferences = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE)
        highScore = preferences.getInt("HIGH_SCORE", 0)
        findViewById<TextView>(R.id.HighScoreText).text = "High Score: $highScore"

        Game()
    }

    fun Game() {

        object : CountDownTimer(10000, 1000) {
            override fun onFinish() {
                findViewById<TextView>(R.id.TimeText).text = "Time: 0"
                handler.removeCallbacks(runnable)
                for (image in imageArray) {
                    image.visibility = View.INVISIBLE
                }

                val alertDialog = AlertDialog.Builder(this@MainActivity)
                alertDialog.setTitle("Game Over")
                alertDialog.setMessage("Do you want to play again?")
                alertDialog.setPositiveButton("Yes, Play Again") { dialog: DialogInterface?, which: Int ->
                    score = 0
                    findViewById<TextView>(R.id.ScoreText).text = "Score: $score"
                    Game()
                }
                alertDialog.setNegativeButton("No") { dialog: DialogInterface?, which: Int -> finish() }
                alertDialog.show()
            }

            override fun onTick(millisUntilFinished: Long) {
                findViewById<TextView>(R.id.TimeText).text = "Time:" + millisUntilFinished / 1000
            }
        }.start()

        runnable = object : Runnable {
            override fun run() {
                for (image in imageArray) {
                    image.visibility = View.INVISIBLE
                }
                val random = Random()
                val index = random.nextInt(8 - 0)
                imageArray[index].visibility = View.VISIBLE
                handler.postDelayed(runnable, 500)
            }
        }
        handler.post(runnable)
    }

    fun RightImageTap(view: View) {
        score++
        findViewById<TextView>(R.id.ScoreText).text = "Score: $score"

        // Update the high score if necessary
        if (score > highScore) {
            highScore = score
            findViewById<TextView>(R.id.HighScoreText).text = "High Score: $highScore"

            // Save the new high score
            val preferences = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putInt("HIGH_SCORE", highScore)
            editor.apply()
        }
    }

    fun WrongImageTap(view: View) {
        score--
        findViewById<TextView>(R.id.ScoreText).text = "Score: $score"
    }
}
