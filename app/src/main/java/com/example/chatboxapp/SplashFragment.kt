package com.example.chatboxapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatboxapp.fragments.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

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