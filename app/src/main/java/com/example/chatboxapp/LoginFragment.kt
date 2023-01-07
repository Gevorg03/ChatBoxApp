package com.example.chatboxapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.chatboxapp.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest


//@Singleton
//@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
    lateinit var auth: FirebaseAuth
    private val viewModel: LoginViewModel by activityViewModels()
    private var isDialogOpen: Boolean = false

//    @Inject
//    lateinit var mainObjViewModel: MutableStateFlow<Fragment>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()



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

            tvForgotPassword.setOnClickListener {
                showRecoverPasswordDialog()
            }

            signUp.setOnClickListener {
                CurrFragment.fragment = RegistrationFragment()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.addToBackStack(null)
                    ?.replace(R.id.fragmnet_container, CurrFragment.fragment)?.commit()
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
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragmnet_container, MainFragment())
                                    ?.commit()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Please check your email",
                                    Toast.LENGTH_SHORT
                                ).show()
                                auth.signOut()
                                //user?.sendEmailVerification()
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

    @SuppressLint("MissingInflatedId")
    fun showRecoverPasswordDialog(context: Context = requireContext()) {
        CurrFragment.isDialogOpen = true
        val passViewModel: ForgetPassViewModel by viewModels()
        val view = LayoutInflater.from(context).inflate(R.layout.forget_pass_dialog, null)
        val inputEmail = view.findViewById<TextInputEditText>(R.id.email_input)
        val layoutEmail = view.findViewById<TextInputLayout>(R.id.email_layout)
        val btnReset = view.findViewById<Button>(R.id.btn_reset)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)

        val dialog = builder.create()
        dialog.setCancelable(false)

//        lifecycleScope.launchWhenCreated {
//            passViewModel.inputEmail.collectLatest { text ->
//                inputEmail.setText(text)
//                inputEmail.setSelection(inputEmail.length())
//            }
//        }

//        setErrorText(layoutEmail, passViewModel.emailErrorText)

        inputEmail.doOnTextChanged { text, _, _, _ ->
            passViewModel.inputEmail.value = text.toString()
        }

        btnReset.setOnClickListener {
            if (inputEmail.text.toString().isEmpty()) {
                passViewModel.emailErrorText.value = "The field can not be empty"
            } else {
                val email = inputEmail.text.toString()
                beginRecovery(email, dialog, context)
            }
        }

        btnCancel.setOnClickListener {
            CurrFragment.isDialogOpen = false
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun beginRecovery(
        email: String,
        dialog: AlertDialog,
        context: Context,
    ) {
        auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    CurrFragment.isDialogOpen = false
                    Toast.makeText(context, "Done sent", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
//                    passViewModel.emailErrorText.value = "There is no email"
                }
            }
    }
}