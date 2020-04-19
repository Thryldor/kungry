package ca.ulaval.ima.mp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.ulaval.ima.mp.ui.review.CreationActivity
import ca.ulaval.ima.mp.ui.restaurant.RestaurantListFragment
import ca.ulaval.ima.mp.ui.restaurant.dummy.DummyContent

class MainActivity : AppCompatActivity(), RestaurantListFragment.OnRestaurantListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupCustomActionBar();

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_map, R.id.navigation_restaurants, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, _, _ ->
            val intent = Intent(this, CreationActivity::class.java).apply {
                putExtra(CreationActivity.RESTAURANT_ID_KEY, "id")
            }
            startActivity(intent)
        }
        navView.setupWithNavController(navController)
    }

    fun setupCustomActionBar() {
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.action_bar)
    }

    override fun onRestaurantClick(item: DummyContent.DummyItem?) {
        // TODO Implement
    }
}
