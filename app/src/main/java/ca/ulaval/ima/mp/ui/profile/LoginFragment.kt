package ca.ulaval.ima.mp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ca.ulaval.ima.mp.R
import com.squareup.picasso.Picasso

class LoginFragment : Fragment() {

    private fun setBackground(imageView: ImageView) {
        Picasso.with(context)
            .load(R.drawable.background_small)
            .fit()
            .centerCrop()
            .into(imageView)
    }

    private fun setSubmitButton(button: Button) {
        button.setOnClickListener {

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

        setBackground(root.findViewById(R.id.form_background))
        setSubmitButton(root.findViewById(R.id.button_login))
        setSwitchRegister(root.findViewById(R.id.switch_register))
        (activity as AppCompatActivity).supportActionBar?.hide()

        val loginButton: Button = root.findViewById(R.id.button_login)
        loginButton.setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_login_to_register)
        }
        return root
    }
}
