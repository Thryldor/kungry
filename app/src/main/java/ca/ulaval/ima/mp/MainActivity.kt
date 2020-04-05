package ca.ulaval.ima.mp

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.model.*
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, _, _ ->
            APIService.login(
                AccountLogin(
                    email = "cedric.thomas.1@ulaval.ca",
                    password = "CedricThomas42"
                ), object : APIService.ResponseHandler<TokenOutput>() {
                    override fun onResult(result: APIService.Result<TokenOutput>) {
                        val tokenInfo = result.getResult()
                        Log.d("MP", tokenInfo.expires_in.toString())
                        APIService.me(object : APIService.ResponseHandler<Account>() {
                            override fun onResult(result: APIService.Result<Account>) {
                                try {
                                    val account = result.getResult()
                                    Log.d("MP", account.email)
                                } catch (e: APIService.AuthenticationFailuredException) {
                                    Log.d("MP", e!!.wrapper!!.error!!.display)
                                }
                            }
                        });

                    }
                }
            )
        }
        navView.setupWithNavController(navController)
    }
}
