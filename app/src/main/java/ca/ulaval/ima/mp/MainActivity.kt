package ca.ulaval.ima.mp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.AccountLogin
import ca.ulaval.ima.mp.api.model.RestaurantLight
import ca.ulaval.ima.mp.ui.restaurant.RestaurantListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), RestaurantListFragment.OnRestaurantListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setupCustomActionBar()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_map, R.id.navigation_restaurants, R.id.navigation_login))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, _, _ ->
            APIService.login(
                AccountLogin(
                    email = "cedric.thomas.1@ulaval.ca",
                    password = "CedricThomas42"
                ), createHandler { result ->
                    val tokenInfo = result.getResult()
                    APIService.me(createHandler { result ->
                        try {
                            val account = result.getResult()
                            Log.d("MP", account.email)
                        } catch (e: APIService.AuthenticationFailureException) {
                            Log.d("MP", e!!.wrapper!!.error!!.display)
                        }
                    });
                })
        }
        navView.setupWithNavController(navController)
    }

    fun setupCustomActionBar() {
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.action_bar)
    }

    override fun onRestaurantClick(item: RestaurantLight?) {
        // TODO Implement
    }
}
