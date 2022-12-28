package com.example.chatboxapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.example.chatboxapp.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
    val mainViewModel: MainViewModel by activityViewModels()
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()

        return FragmentLoginBinding.inflate(inflater, container, false)
            .also { binding = it }.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.run {
            signUp.setOnClickListener {
//                val fm = activity?.supportFragmentManager
//                fm?.beginTransaction()
//                    ?.replace(R.id.fragmnet_container, RegistrationFragment())
//                    ?.commit()
//                fm?.popBackStack()
                mainViewModel.currentFragment.value = RegistrationFragment()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmnet_container, mainViewModel.currentFragment.value)
                    ?.commit()
            }
            signIn.setOnClickListener {
                auth.signInWithEmailAndPassword(
                    inputEmail.text.toString(),
                    inputPass.text.toString()
                )
                    .addOnCompleteListener { p0 ->
                        if (p0.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser

                            if (user?.isEmailVerified == true) {
                                Toast.makeText(requireContext(), "Logined", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please check your email",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "no user", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }
        super.onViewCreated(view, savedInstanceState)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}