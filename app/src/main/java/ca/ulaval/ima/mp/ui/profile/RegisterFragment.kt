package ca.ulaval.ima.mp.ui.profile

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
import ca.ulaval.ima.mp.api.model.CreateAccount
import com.squareup.picasso.Picasso


class RegisterFragment : Fragment() {

    private var name = ""
    private var lastName = ""
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
        name = (root.findViewById(R.id.form_first_name) as EditText).text.toString()
        lastName = (root.findViewById(R.id.form_last_name) as EditText).text.toString()
        email = (root.findViewById(R.id.form_email) as EditText).text.toString()
        password = (root.findViewById(R.id.form_password) as EditText).text.toString()
    }

    private fun setSubmitButton(root: View) {
        val button: Button = root.findViewById(R.id.button_login)

        button.setOnClickListener {
            getValues(root)
            APIService.createAccount(
                CreateAccount(
                    first_name = this.name,
                    last_name = this.lastName,
                    email = this.email,
                    password = this.password
                ), createHandler { result ->
                    try {
                        result.getResult()
                        activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_register_to_login)
                    }
                    catch (e: APIService.CallFailureException) {
                        val toast = Toast.makeText(
                            activity,
                            e.wrapper?.error?.display,
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                    }
                })
        }
    }

    private fun setSwitchLogin(textView: TextView) {
        textView.setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_register_to_login)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.register_fragment, container, false)

        setBackground(root.findViewById(R.id.form_background))
        setSubmitButton(root)
        setSwitchLogin(root.findViewById(R.id.switch_register))
        (activity as AppCompatActivity).supportActionBar?.hide()
        return root
    }
}