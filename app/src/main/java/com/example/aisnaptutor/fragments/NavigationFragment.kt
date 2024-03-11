package com.example.aisnaptutor.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.aisnaptutor.R
import com.example.aisnaptutor.databinding.FragmentNavigationBinding


class NavigationFragment : Fragment() {
    private val binding by lazy {
        FragmentNavigationBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.parentRemoveAd.setOnClickListener {

        }
        binding.parentAppLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_navigationFragment_to_languagesFragment)
        }
        binding.parentShareApp.setOnClickListener {
            shareApp(requireContext())
        }
        binding.parentRate.setOnClickListener {
            openPlayStoreForRating(requireContext())
        }
        binding.parentPrivacy.setOnClickListener {
            openPrivacyPolicy(
                        requireContext(),
                        "https://sites.google.com/view/aisnaptutor/home"
                    )
        }
        binding.parenMoreApps.setOnClickListener {
            openPrivacyPolicy(
                requireContext(),
                "https://play.google.com/store/apps/developer?id=Sparx+Developer"
            )
        }
        binding.btnBack.setOnClickListener {
            fragmentManager?.popBackStackImmediate()
        }
        return binding.root
    }

    private fun contactUs() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:shahzaibm968@gmail.com")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of the email")
        startActivity(intent)

    }

    fun openPlayStoreForRating(context: Context) {
        val appPackageName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (e: android.content.ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    fun openPrivacyPolicy(context: Context, link: String) {
        val privacyPolicyUrl = link // Replace with your actual URL

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(privacyPolicyUrl)

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Handle the case where no app can handle the intent
            Log.e("PrivacyPolicy", "No browser app found to open privacy policy")
        }
    }

    fun shareApp(context: Context) {
        val appName = context.getString(R.string.app_name) // Use app_name string resource

        // Use app store URL directly (if available and appropriate)
        val appStoreUrl =
            "https://play.google.com/store/apps/details?id=com.sparx.agecalculator.birthdaycount.agetracker.dob.faceage.camera.app"

        // Construct fallback generic message if app store URL not available
        val message = if (appStoreUrl.isNotBlank()) {
            "Hey! Check out this awesome app, $appName:\n$appStoreUrl"
        } else {
            "Hey! Check out this cool app, $appName. Install it now!"
        }

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)

        val chooserIntent = Intent.createChooser(shareIntent, "Share $appName")
        context.startActivity(chooserIntent)
    }
}