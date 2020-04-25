package ca.ulaval.ima.mp.ui.review.creation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.AccountLogin
import ca.ulaval.ima.mp.api.model.TokenOutput
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.action_bar.view.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.login_fragment.view.*

class LoginActivity : AppCompatActivity() {

    companion object {
        val LOGIN_REQUEST = 1;
    }

    private var email = ""
    private var password = ""

    private fun setBackground(imageView: ImageView) {
        Picasso.with(this)
            .load(R.drawable.background_small)
            .fit()
            .centerCrop()
            .into(imageView)
    }

    private fun getValues() {
        email = container.form_email.text.toString()
        password = container.form_password.text.toString()
    }

    private fun setSubmitButton() {
        val button: Button = findViewById(R.id.button_login)
        button.setOnClickListener {
            getValues()
            APIService.login(
                AccountLogin(
                    email = "philippe.desousaviolante@gmail.com",//this.email,
                    password = "Password1234"// this.password
                ), createHandler { result ->
                    try {
                        val res: TokenOutput = result.getResult()
                        val sharedPref = getSharedPreferences(
                            getString(R.string.token_shared_pref), Context.MODE_PRIVATE
                        )
                        val gson = Gson()
                        val json = gson.toJson(res)
                        with(sharedPref!!.edit()) {
                            putString(getString(ca.ulaval.ima.mp.R.string.token_shared_pref), json)
                            commit()
                        }
                        val resultIntent: Intent = Intent()
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    } catch (e: Exception) {
                        when (e) {
                            is APIService.CallFailureException -> {
                                val toast = Toast.makeText(
                                    this,
                                    e.wrapper?.error?.display,
                                    Toast.LENGTH_SHORT
                                )
                                toast.show()
                            }
                        }
                    }
                })
        }
    }

    private fun setToolbar() {
        setSupportActionBar(login_action_bar.toolbar as Toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setToolbar()

        container.layout_switch.visibility = View.GONE
        if (APIService.logged) {
            val resultIntent: Intent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        setBackground(container.form_background)
        setSubmitButton()
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
