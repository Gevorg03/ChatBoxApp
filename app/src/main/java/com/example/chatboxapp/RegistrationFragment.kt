package com.example.chatboxapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap.Config
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.chatboxapp.databinding.FragmentRegistrationBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RegistrationFragment : Fragment() {
    private var binding: FragmentRegistrationBinding? = null
    private val viewModel: RegistrationViewModel by activityViewModels()

    lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("barev")
    }


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val lst = mapOf(
            "user" to binding?.inputFullname,
            "email" to binding?.inputEmail,
            "phone" to binding?.inputPhone,
            "password" to binding?.inputPass
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

        val viewModelComponents = listOf(viewModel.fullnameHelperText)

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
                val postRef = Firebase.database.reference.child("users")
                val postRef1 = databaseReference.child("users").child("")

                if (fullnameLayout.error != null || fullnameLayout.helperText != null) {
                    if (emailLayout.error != null || emailLayout.helperText != null) {
                        if (phoneLayout.error != null || phoneLayout.helperText != null) {
                            if (passLayout.error != null || passLayout.helperText != null) {
                                progressBar.visibility = View.VISIBLE
                                val phone =
                                    "${ccp.selectedCountryCodeWithPlus}${inputPhone.text.toString()}"
                                postRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        if (!dataSnapshot.child(phone).exists()) {
                                            databaseReference.child("users")
                                                .addListenerForSingleValueEvent(object :
                                                    ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
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
                                                                    auth.signOut()
                                                                    helperText.values.forEach { v -> v.value = "" }
                                                                    errorText.values.forEach { v -> v.value = "" }
                                                                    viewModel.data.values.forEach{ v -> v.value = ""}
                                                                    val us =
                                                                        FirebaseAuth.getInstance().currentUser
                                                                    us?.sendEmailVerification()
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
                                                                    emailLayout.error =
                                                                        "The email already exists"
                                                                    emailLayout.helperText = null
//                                                                    Toast.makeText(
//                                                                        requireContext(),
//                                                                        "The email already exists",
//                                                                        Toast.LENGTH_SHORT
//                                                                    ).show()
                                                                    showKeyboard(inputEmail)
                                                                }
                                                            }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                    }
                                                })
                                        } else {
//                                            Toast.makeText(
//                                                requireContext(),
//                                                "Phone already is registered",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
                                            showKeyboard(
                                                inputPhone,
                                            )
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })

                                progressBar.visibility = View.GONE
                            } else {
                                showKeyboard(inputPass)
                            }
                        } else {
                            showKeyboard(inputPhone)
                        }
                    } else {
                        showKeyboard(inputEmail)
                    }
                } else {
                    showKeyboard(inputFullname)
                }
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
                if (text != null && text.isNotEmpty()) textInputLayout?.endIconMode =
                    TextInputLayout.END_ICON_CUSTOM
                binding?.passLayout?.isPasswordVisibilityToggleEnabled = true
                textInputLayout?.helperText = text
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

    private fun existEmail(email: String): Boolean? {
        var response: Boolean? = null

        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { p0 ->
            println("p0 is $p0")
            if (p0.isSuccessful) {
                val check = !p0.result.signInMethods?.isEmpty()!!
                println("check is ${check}")
                response = check
            }
        }
        return response
    }
}



