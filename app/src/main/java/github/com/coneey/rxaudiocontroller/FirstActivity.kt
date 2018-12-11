package github.com.coneey.rxaudiocontroller

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_first.*

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        test_button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}