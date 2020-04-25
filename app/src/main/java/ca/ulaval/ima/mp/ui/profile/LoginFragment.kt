package ca.ulaval.ima.mp.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler
import ca.ulaval.ima.mp.api.model.AccountLogin
import ca.ulaval.ima.mp.api.model.TokenOutput
import com.google.gson.Gson
import com.squareup.picasso.Picasso


class LoginFragment : Fragment() {

    private var email = ""
    private var password = ""

    private fun setBackground(imageView: ImageView) {
        Picasso.with(context)
            .load(R.drawable.background_small)
            .fit()
            .centerCrop()
            .into(imageView)
    }

    private fun getValues(root: View) {
        email = (root.findViewById(R.id.form_email) as EditText).text.toString()
        password = (root.findViewById(R.id.form_password) as EditText).text.toString()
    }

    private fun setSubmitButton(root: View) {
        val button: Button = root.findViewById(R.id.button_login)
        button.setOnClickListener {
            getValues(root)
            APIService.login(
                AccountLogin(
                    email = this.email,
                    password = this.password
                ), createHandler { result ->
                    try {
                        val res: TokenOutput = result.getResult()
                        APIService.saveToken(context!!, res)
                        activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_login_to_account)
                    }
                    catch (e: Exception) {
                        when(e) {
                            is APIService.CallFailureException -> {
                                val toast = Toast.makeText(
                                    activity,
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

    // Switch to Register fragment on click
    private fun setSwitchRegister(textView: TextView) {
        textView.setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_login_to_register)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.login_fragment, container, false)
        (activity as AppCompatActivity).supportActionBar?.hide()

        if (APIService.logged) {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_login_to_account)
        }
        setBackground(root.findViewById(R.id.form_background))
        setSubmitButton(root)
        setSwitchRegister(root.findViewById(R.id.switch_register))
        return root
    }
}
