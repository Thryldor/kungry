package ca.ulaval.ima.mp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.AccountLogin
import ca.ulaval.ima.mp.api.model.RestaurantLight
import ca.ulaval.ima.mp.ui.restaurant.RestaurantActivity
import ca.ulaval.ima.mp.ui.restaurant.RestaurantListFragment
import ca.ulaval.ima.mp.ui.review.list.ReviewListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.action_bar.view.*
import kotlinx.android.synthetic.main.review_creation_activity.*


class MainActivity : AppCompatActivity(), RestaurantListFragment.OnRestaurantListener {

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
        navController.addOnDestinationChangedListener { _, _, _ ->
            APIService.login(
                AccountLogin(
                    email = "cedric.thomas.1@ulaval.ca",
                    password = "CedricThomas42"
                ), createHandler { result ->
                    val intent = Intent(this, ReviewListActivity::class.java).apply {
                        putExtra(ReviewListActivity.RESTAURANT_ID_KEY, "1")
                    }
                    startActivity(intent)

                });
        }
        navView.setupWithNavController(navController)
    }

    fun setupCustomActionBar() {
        setSupportActionBar((action_bar.toolbar) as Toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onRestaurantClick(item: RestaurantLight?) {
        val intent = Intent(this, RestaurantActivity::class.java).apply {
            putExtra(RestaurantActivity.RESTAURANT_ID_KEY, item!!.id)
        }
        startActivity(intent)
    }
}
