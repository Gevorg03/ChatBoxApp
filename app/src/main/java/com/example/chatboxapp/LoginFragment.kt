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
import androidx.lifecycle.lifecycleScope
import com.example.chatboxapp.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var mainObjViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()

        Toast.makeText(requireContext(), mainObjViewModel.toString(), Toast.LENGTH_SHORT).show()

        lifecycleScope.launchWhenCreated {
            mainObjViewModel.currentFragment.collectLatest { fragment ->
//                Toast.makeText(requireContext(), fragment.toString(), Toast.LENGTH_SHORT).show()
                if (!fragment.toString().contains("Splash")) {
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragmnet_container, fragment)
                        ?.commit()
                }
            }
        }

        return FragmentLoginBinding.inflate(inflater, container, false)
            .also { binding = it }.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.run {
            signUp.setOnClickListener {
                mainObjViewModel.currentFragment.value = RegistrationFragment()
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