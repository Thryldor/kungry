package ca.ulaval.ima.mp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import ca.ulaval.ima.mp.R


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.profile_fragment, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        textView.setText("This is the login page")

        (activity as AppCompatActivity).supportActionBar?.hide();
        val registerButton: Button = root.findViewById(R.id.button_register)
        registerButton.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }

        val loginButton: Button = root.findViewById(R.id.button_login)
        loginButton.setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_firstFragment_to_secondFragment)
        }
        return root
    }
}
