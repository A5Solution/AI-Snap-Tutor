package com.example.aisnaptutor.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aisnaptutor.R
import com.example.aisnaptutor.SharePref
import com.example.aisnaptutor.databinding.FragmentLanguagesBinding

class LanguagesFragment : Fragment() {
    private val binding by lazy {
        FragmentLanguagesBinding.inflate(layoutInflater)
    }
    var langugage: String? = "en"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setView()
        binding.parentFrench.setOnClickListener {
            setView()
            langugage = "fr"
            binding.radioFrench.visibility = View.VISIBLE
            binding.parentFrench.setBackgroundResource(R.drawable.bg_language)
        }
        binding.parentJapanese.setOnClickListener {
            setView()
            langugage = "ja"
            binding.radioJapenese.visibility = View.VISIBLE
            binding.parentJapanese.setBackgroundResource(R.drawable.bg_language)
        }
        binding.parentSpanish.setOnClickListener {
            setView()
            langugage = "es"
            binding.radioSpanish.visibility = View.VISIBLE
            binding.parentSpanish.setBackgroundResource(R.drawable.bg_language)
        }
        binding.parentEnglish.setOnClickListener {
            setView()
            binding.radioEnglish.visibility = View.VISIBLE
            binding.parentEnglish.setBackgroundResource(R.drawable.bg_language)
        }


        binding.btnDone.setOnClickListener {
            SharePref.putString("language", langugage!!)
            fragmentManager?.popBackStackImmediate()
        }
        return binding.root
    }


    fun setView()
    {
        binding.parentChinese.setBackgroundResource(R.drawable.bg_language_not_selected)
        binding.parentEnglish.setBackgroundResource(R.drawable.bg_language_not_selected)
        binding.parentFrench.setBackgroundResource(R.drawable.bg_language_not_selected)
        binding.parentKorean.setBackgroundResource(R.drawable.bg_language_not_selected)
        binding.parentJapanese.setBackgroundResource(R.drawable.bg_language_not_selected)
        binding.parentSpanish.setBackgroundResource(R.drawable.bg_language_not_selected)

        binding.radioChinese.visibility = View.GONE
        binding.radioEnglish.visibility = View.GONE
        binding.radioFrench.visibility = View.GONE
        binding.radioKorean.visibility = View.GONE
        binding.radioJapenese.visibility = View.GONE
        binding.radioSpanish.visibility = View.GONE
    }
}