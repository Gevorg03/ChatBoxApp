package com.example.chatboxapp

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//@AndroidEntryPoint
class SplashFragment() : Fragment() {
    lateinit var auth: FirebaseAuth

//    @Inject
//    lateinit var mainObjViewModel: MutableStateFlow<Fragment>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
//        auth.signOut()

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            if (auth.currentUser != null) {
                CurrFragment.fragment = MainFragment()

            } else {
                CurrFragment.fragment = LoginFragment()
            }
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmnet_container, CurrFragment.fragment)?.commit()
        }
    }
}