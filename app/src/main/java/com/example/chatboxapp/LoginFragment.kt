package com.example.chatboxapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.chatboxapp.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
    lateinit var auth: FirebaseAuth
    private val viewModel: LoginViewModel by activityViewModels()

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
        val lst = mapOf(
            "email" to binding?.inputEmail,
            "pass" to binding?.inputPass
        )

        val errorText = mapOf(
            binding?.emailLayout to viewModel.emailErrorText,
            binding?.passLayout to viewModel.passErrorText
        )


        binding?.run {
            inputEmail.isClickedChange(viewModel.emailErrorText)
            inputPass.isClickedChange(viewModel.passErrorText)

            lst.forEach { (key, value) -> setText(key, value) }
            errorText.forEach { (key, value) -> setErrorText(key, value) }

            inputEmail.doOnTextChanged { text, _, _, _ ->
                viewModel.data["email"] = MutableStateFlow(text.toString())
                if (text?.isNotEmpty() == true)
                    viewModel.emailErrorText.value = null
            }

            inputPass.doOnTextChanged { text, _, _, _ ->
                viewModel.data["pass"] = MutableStateFlow(text.toString())
                if (text?.isNotEmpty() == true)
                    viewModel.passErrorText.value = null
            }

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

    private fun TextInputEditText.isClickedChange(
        errorText: MutableStateFlow<String?>
    ) {
        this.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && this.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), this.text.toString(), Toast.LENGTH_SHORT).show()
                errorText.value = "The field can not be empty"
            }
        }
    }

    private fun setText(key: String, et: TextInputEditText?) {
        lifecycleScope.launchWhenCreated {
            viewModel.data[key]?.collectLatest { user ->
                et?.setText(user)
                et?.setSelection(et.length())
            }
        }
    }

    private fun setErrorText(textInputLayout: TextInputLayout?, flow: MutableStateFlow<String?>) {
        lifecycleScope.launchWhenCreated {
            flow.collectLatest { text ->
                if (text != null && text.isNotEmpty()) textInputLayout?.endIconMode =
                    TextInputLayout.END_ICON_CUSTOM
                binding?.passLayout?.isPasswordVisibilityToggleEnabled = true
                textInputLayout?.error = text
            }
        }
    }
}