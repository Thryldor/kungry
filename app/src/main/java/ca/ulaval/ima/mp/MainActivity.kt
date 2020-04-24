package ca.ulaval.ima.mp


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.ulaval.ima.mp.api.model.RestaurantLight
import ca.ulaval.ima.mp.ui.restaurant.RestaurantActivity
import ca.ulaval.ima.mp.ui.restaurant.RestaurantListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.action_bar.view.*
import kotlinx.android.synthetic.main.review_creation_activity.*


class MainActivity : AppCompatActivity(), RestaurantListFragment.RestaurantListFragmentController {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setupCustomActionBar()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_map, R.id.navigation_restaurants, R.id.navigation_login
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun setupCustomActionBar() {
        setSupportActionBar((action_bar.toolbar) as Toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onRestaurantClick(restaurant: RestaurantLight) {
        val intent = Intent(this, RestaurantActivity::class.java).apply {
            putExtra(RestaurantActivity.RESTAURANT_ID_KEY, restaurant!!.id)
        }
        startActivity(intent)
    }
}
