package ca.ulaval.ima.mp.ui.review

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import ca.ulaval.ima.mp.R


class CreationActivity : AppCompatActivity() {

    val RESTAURANT_ID_KEY: String = "RESTAURANT_ID_KEY"
    private var restaurantId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.evaluation_creation_activity)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.action_bar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        restaurantId = intent.getStringExtra(RESTAURANT_ID_KEY)
//            ?: throw RuntimeException("No restaurant ID passed to review creation activity")
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
