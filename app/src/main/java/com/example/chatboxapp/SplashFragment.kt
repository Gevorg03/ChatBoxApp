package com.example.chatboxapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var mainObj: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()

//        Handler(Looper.myLooper()!!).postDelayed({
//            findNavController().navigate(R.id.action_splashFragment_to_FirstFragment)
//        }, 5000)

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            if (auth.currentUser != null) {
                Toast.makeText(requireContext(), auth.currentUser!!.displayName, Toast.LENGTH_SHORT)
                    .show()
                mainObj.mainViewModel.currentFragment.value = MainFragment()
                activity?. supportFragmentManager?.beginTransaction()
                    ?.add(R.id.fragmnet_container, MainFragment())
                    ?.commit()
            } else {
                mainObj.mainViewModel.currentFragment.value = LoginFragment()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.fragmnet_container, LoginFragment())
                    ?.commit()
            }
        }
    }
}