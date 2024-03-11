package com.example.aisnaptutor.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.akexorcist.localizationactivity.core.LanguageSetting.setLanguage
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.aisnaptutor.R
import com.example.aisnaptutor.SharePref
import com.example.aisnaptutor.databinding.ActivityMainBinding
import com.example.aisnaptutor.databinding.ExitDialogLayoutBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var isHomeScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            isHomeScreen = destination.id == R.id.homeFragment
        }
    }

    override fun onResume() {
//        setLanguage(SharePref.getString("language", "en"))
        super.onResume()
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (isHomeScreen) {
            exitDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun exitDialog() {
        try {
            val builder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val bind = ExitDialogLayoutBinding.inflate(inflater)
            builder.setView(bind.root)
            val dialog = builder.create()
            dialog.show()

            bind.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            bind.btnExit.setOnClickListener {
                finishAffinity()
            }
        } catch (e: Exception) {
            // Log the error or handle it in some other way
            e.printStackTrace()
        }
    }
}