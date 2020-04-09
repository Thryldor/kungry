package ca.ulaval.ima.mp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ca.ulaval.ima.mp.R
import kotlinx.android.synthetic.main.account_fragment.*

class AccountFragment : Fragment() {

    private val firstName = "John"
    private val lastName = "Doe"
    private val email = "johndoe@mail.com"
    private val eval = 2

    private fun getAccountInfos() {

    }

    private fun showInfos(root: View) {
        (root.findViewById(R.id.name_info) as TextView).text = firstName + " " + lastName
        (root.findViewById(R.id.email_info) as TextView).text = email
        (root.findViewById(R.id.eval_info) as TextView).text = eval.toString()
    }

    private fun setDisconnectButton(button: Button) {
        button.setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.action_account_to_login)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.account_fragment, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        showInfos(root)
        setDisconnectButton(root.findViewById(R.id.button_disconnect))
        return root
    }
}