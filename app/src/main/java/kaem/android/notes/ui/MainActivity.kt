package kaem.android.notes.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kaem.android.notes.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun createButtonTap(v: View) {
        val intent = Intent(this, CreateOrEditActivity::class.java)
        startActivity(intent)
    }
}
