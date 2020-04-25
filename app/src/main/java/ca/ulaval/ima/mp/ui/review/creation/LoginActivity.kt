package ca.ulaval.ima.mp.ui.review.creation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.navigation.findNavController
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.AccountLogin
import ca.ulaval.ima.mp.api.model.TokenOutput
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.review_creation_popup_fragment.view.*

class LoginActivity : AppCompatActivity() {

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
        email = (findViewById<EditText>(R.id.form_email)).text.toString()
        password = (findViewById<EditText>(R.id.form_password)).text.toString()
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
                        APIService.saveToken(this, res)
                        val resultIntent: Intent = Intent()
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                    catch (e: Exception) {
                        when(e) {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_fragment)

        supportActionBar?.hide()

        if (APIService.logged) {
            val resultIntent: Intent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        setBackground(findViewById(R.id.form_background))
        setSubmitButton()
    }
}
