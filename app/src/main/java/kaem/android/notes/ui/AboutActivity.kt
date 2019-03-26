package kaem.android.notes.ui

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.widget.TextView
import kaem.android.notes.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initViews()
    }

    private fun initViews() {
        var ref : Spanned
        var donate : Spanned
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ref = Html.fromHtml(getString(R.string.about_ref), Html.FROM_HTML_MODE_LEGACY)
            donate = Html.fromHtml(getString(R.string.about_donate), Html.FROM_HTML_MODE_LEGACY)
        } else {
            ref = Html.fromHtml(getString(R.string.about_ref))
            donate = Html.fromHtml(getString(R.string.about_donate))
        }

        val refTextView = findViewById<TextView>(R.id.about_ref)
        refTextView.text = ref
        refTextView.movementMethod = LinkMovementMethod.getInstance()

        val donateTextView = findViewById<TextView>(R.id.about_donate)
        donateTextView.text = donate
        donateTextView.movementMethod = LinkMovementMethod.getInstance()


    }
}
