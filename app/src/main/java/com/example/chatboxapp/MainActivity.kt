package com.example.chatboxapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.example.chatboxapp.databinding.ActivityMainBinding
import com.example.chatboxapp.fragments.LoginFragment
import com.google.firebase.auth.FirebaseAuth

//@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    val mainViewModel: MainViewModel by viewModels()
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        auth = FirebaseAuth.getInstance()

        //mainViewModel.currentFragment.value = SplashFragment()
//        lifecycleScope.launchWhenCreated {
//            mainViewModel.currentFragment.collectLatest { fragment ->
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragmnet_container, fragment)
//                    .commit()
//            }
//        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmnet_container, CurrFragment.fragment).commit()
        if (CurrFragment.isDialogOpen)
            LoginFragment().showRecoverPasswordDialog(this)

    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmnet_container, CurrFragment.fragment).commit()
//
//
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}