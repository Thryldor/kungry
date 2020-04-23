package ca.ulaval.ima.mp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.ActionBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.AccountLogin
import ca.ulaval.ima.mp.ui.restaurant.RestaurantListFragment
import ca.ulaval.ima.mp.ui.restaurant.dummy.DummyContent

class MainActivity : AppCompatActivity(), RestaurantListFragment.OnRestaurantListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupCustomActionBar();

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_map, R.id.navigation_restaurants, R.id.navigation_profile))
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
                        } catch (e: APIService.AuthenticationFailuredException) {
                            Log.d("MP", e!!.wrapper!!.error!!.display)
                        }
                    });
                })
        }
        navView.setupWithNavController(navController)
    }

    private fun setupCustomActionBar() {
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.action_bar)
    }

    override fun onRestaurantClick(item: DummyContent.DummyItem?) {
        // TODO Implement
    }
}
