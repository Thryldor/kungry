package ca.ulaval.ima.mp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.api.APIService
import ca.ulaval.ima.mp.api.createHandler

class AccountFragment : Fragment() {

    private fun showInfos(root: View, name: String, email: String, nb_eval: Int) {
        (root.findViewById(R.id.name_info) as TextView).text = name
        (root.findViewById(R.id.email_info) as TextView).text = email
        (root.findViewById(R.id.eval_info) as TextView).text = nb_eval.toString()
    }

    private fun getAccountInfos(root: View) {
        APIService.me(createHandler { result ->
            try {
                val account = result.getResult()
                val name = account.first_name!! + " " + account.last_name!!
                val email = account.email!!
                val nb_eval = account.total_review_count!!

                showInfos(root, name, email, nb_eval)
            } catch (e: APIService.AuthenticationFailuredException) {
                val toast = Toast.makeText(
                    activity,
                    e.wrapper?.error?.display,
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        })
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

        getAccountInfos(root)
        setDisconnectButton(root.findViewById(R.id.button_disconnect))
        return root
    }
}