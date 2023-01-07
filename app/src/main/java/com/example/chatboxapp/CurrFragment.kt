package com.example.chatboxapp

import androidx.fragment.app.Fragment
import com.example.chatboxapp.fragments.SplashFragment

object CurrFragment {
    var fragment: Fragment = SplashFragment()
    var isDialogOpen: Boolean = false
}