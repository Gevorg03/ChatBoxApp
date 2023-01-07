package com.example.chatboxapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.chatboxapp.databinding.FragmentRegistrationBinding
import com.example.chatboxapp.fragments.LoginFragment
import com.example.chatboxapp.viewModels.RegistrationViewModel
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

//@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private var binding: FragmentRegistrationBinding? = null
    private val viewModel: RegistrationViewModel by activityViewModels()

    lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private var isFailed: Boolean = false

    private val databaseReference = FirebaseDatabase.getInstance()
        .getReferenceFromUrl("https://chatboxapp-ecfe7-default-rtdb.firebaseio.com/")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        //registrationViewModel = ViewModelProvider(requireActivity())[RegistrationViewModel::class.java]

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        return FragmentRegistrationBinding.inflate(inflater, container, false)
            .also { binding = it }.root

    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val lst = mapOf(
            "user" to binding?.inputFullname,
            "email" to binding?.inputEmail,
            "phone" to binding?.inputPhone,
            "pass" to binding?.inputPass
        )

        val helperText = mapOf(
            binding?.fullnameLayout to viewModel.fullnameHelperText,
            binding?.emailLayout to viewModel.emailHelperText,
            binding?.phoneLayout to viewModel.phoneHelperText,
            binding?.passLayout to viewModel.passHelperText
        )

        val errorText = mapOf(
            binding?.fullnameLayout to viewModel.fullnameErrorText,
            binding?.emailLayout to viewModel.emailErrorText,
            binding?.phoneLayout to viewModel.phoneErrorText,
            binding?.passLayout to viewModel.passErrorText
        )

        binding?.run {
            ccp.registerCarrierNumberEditText(inputPhone)

            inputFullname.isClickedChange(
                viewModel.fullnameHelperText, viewModel.fullnameErrorText
            )
            inputEmail.isClickedChange(
                viewModel.emailHelperText, viewModel.emailErrorText
            )
            inputPhone.isClickedChange(
                viewModel.phoneHelperText, viewModel.phoneErrorText
            )
            inputPass.isClickedChange(
                viewModel.passHelperText, viewModel.passErrorText
            )

            lst.forEach { (key, value) -> setText(key, value) }
            helperText.forEach { (key, value) -> setHelperText(key, value) }
            errorText.forEach { (key, value) -> setErrorText(key, value) }

            inputFullname.doOnTextChanged { text, _, _, _ ->
                viewModel.data["user"] = MutableStateFlow(text.toString())
                if (text.toString().length < 5) {
                    viewModel.fullnameHelperText.value = null
                    viewModel.fullnameErrorText.value = "Min length is 5"
                } else {
                    viewModel.fullnameHelperText.value = "Strong username"
                    viewModel.fullnameErrorText.value = null
                }
            }

            inputEmail.doAfterTextChanged { text ->
                viewModel.data["email"] = MutableStateFlow(text.toString())
                if (text.toString().trim().matches(emailPattern.toRegex()) && text.toString()
                        .isNotEmpty()
                ) {
                    viewModel.emailHelperText.value = "Valid Email"
                    viewModel.emailErrorText.value = null
                } else {
                    viewModel.emailHelperText.value = null
                    viewModel.emailErrorText.value = "Invalid email"
                }
            }

            inputPhone.doOnTextChanged { text, _, _, _ ->
                viewModel.data["phone"] = MutableStateFlow(text.toString())
                if (!ccp.isValidFullNumber) {
                    viewModel.phoneHelperText.value = null
                    viewModel.phoneErrorText.value = "Invalid phone number"
                } else {
                    viewModel.phoneHelperText.value = "Valid phone number"
                    viewModel.phoneErrorText.value = null
                }
            }

            inputPass.doOnTextChanged { text, _, _, _ ->
                viewModel.data["pass"] = MutableStateFlow(text.toString())
                if (isValidPassword(text.toString()) != "Ok") {
                    viewModel.passHelperText.value = null
                    viewModel.passErrorText.value = isValidPassword(text.toString())
                } else {
                    viewModel.passHelperText.value = "Strong password"
                    viewModel.passErrorText.value = null
                }
            }

            tvLogin.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmnet_container, LoginFragment())?.commit()
            }
            btnRegister.setOnClickListener {
                isFailed = false
                isFieldEmpty(fullnameLayout)
                isFieldEmpty(emailLayout)
                isFieldEmpty(phoneLayout)
                isFieldEmpty(passLayout)

                if (!isFailed) {
                    progressBar.visibility = View.VISIBLE
                    val phone =
                        "${ccp.selectedCountryCodeWithPlus}${inputPhone.text.toString()}"
                    val db = FirebaseDatabase.getInstance()
                    val ref = db.getReference("users")
                    ref.addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.value.toString().contains(phone)) {
                                auth.fetchSignInMethodsForEmail(inputEmail.text.toString())
                                    .addOnCompleteListener { task: Task<SignInMethodQueryResult> ->
                                        val isNewUser =
                                            task.result.signInMethods?.isEmpty()
                                        println(isNewUser)
                                        if (isNewUser == true) {
                                            existEmail(inputEmail.text.toString())
                                            auth.createUserWithEmailAndPassword(
                                                inputEmail.text.toString(),
                                                inputPass.text.toString()
                                            )
                                                .addOnCompleteListener { task: Task<AuthResult> ->
                                                    if (task.isSuccessful) {
                                                        val uid = auth.currentUser?.uid
                                                        databaseReference.child("users")
                                                            .child(uid.toString())
                                                            .child("username")
                                                            .setValue(inputFullname.text.toString())
                                                        databaseReference.child("users")
                                                            .child(uid.toString())
                                                            .child("phone")
                                                            .setValue(phone)
                                                        helperText.values.forEach { v ->
                                                            v.value = ""
                                                        }
                                                        errorText.values.forEach { v ->
                                                            v.value = ""
                                                        }
                                                        viewModel.data.values.forEach { v ->
                                                            v.value = ""
                                                        }
                                                        val us =
                                                            FirebaseAuth.getInstance().currentUser
                                                        us?.sendEmailVerification()
                                                        auth.signOut()
                                                        activity?.supportFragmentManager?.beginTransaction()
                                                            ?.replace(
                                                                R.id.fragmnet_container,
                                                                LoginFragment()
                                                            )?.commit()
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Successfully registered, please, check your email end verify to login",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        emailLayout.requestFocus()
                                                        emailLayout.error =
                                                            "Invalid Email"
                                                        emailLayout.helperText = null
                                                    }
                                                }
                                        } else {
                                            emailLayout.requestFocus()
                                            emailLayout.error =
                                                "The email already exists"
                                            emailLayout.helperText = null
                                        }
                                    }
                            } else {
                                phoneLayout.requestFocus()
                                phoneLayout.error =
                                    "The phone already exists"
                                phoneLayout.helperText = null
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
                progressBar.visibility = View.GONE
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setHelperText(textInputLayout: TextInputLayout?, flow: MutableStateFlow<String?>) {
        lifecycleScope.launchWhenCreated {
            flow.collectLatest { text ->
                if (text != null && text.isNotEmpty())
                    textInputLayout?.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding?.passLayout?.isPasswordVisibilityToggleEnabled = true
                textInputLayout?.helperText = text
            }
        }
    }

    private fun setErrorText(textInputLayout: TextInputLayout?, flow: MutableStateFlow<String?>) {
        lifecycleScope.launchWhenCreated {
            flow.collectLatest { text ->
                if (text != null && text.isNotEmpty())
                    textInputLayout?.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding?.passLayout?.isPasswordVisibilityToggleEnabled = true
                textInputLayout?.error = text
            }
        }
    }

    private fun TextInputEditText.isClickedChange(
        helperText: MutableStateFlow<String?>,
        errorText: MutableStateFlow<String?>
    ) {
        this.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && this.text.toString().isEmpty()) {
                helperText.value = null
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

    private fun isValidPassword(password: String): String {
        return when {
            password.length < 8 -> "Min len is "
            password.firstOrNull { it.isDigit() } == null -> "It must have at least 1 digit"
            password.filter { it.isLetter() }
                .firstOrNull { it.isUpperCase() } == null -> "It must have at least 1 uppercase "
            password.filter { it.isLetter() }
                .firstOrNull { it.isLowerCase() } == null -> "It must have at least 1 lowercase"
            password.firstOrNull { !it.isLetterOrDigit() } == null -> "It must have one special character like"
            else -> "Ok"
        }
    }

    fun showKeyboard(et: TextInputEditText) {
        et.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        //imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun existEmail(email: String) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task: Task<SignInMethodQueryResult> ->
                val isNewUser = task.result.signInMethods?.isNotEmpty()
                println(isNewUser)
            }
    }

    private fun isFieldEmpty(textInputLayout: TextInputLayout) {
        if (textInputLayout.error == null && textInputLayout.helperText == null) {
            textInputLayout.error = "The field can not be empty"
            textInputLayout.helperText = null
            isFailed = true
        }
    }
}

@IgnoreExtraProperties
data class User(val phone: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}