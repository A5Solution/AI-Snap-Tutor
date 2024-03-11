package com.example.aisnaptutor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.aisnaptutor.databinding.FragmentCollectionDetailBinding
import com.example.aisnaptutor.databinding.FragmentCollectionsBinding
import ke.derrick.imagetotext.viewmodels.SharedViewModel

class CollectionDetailFragment : Fragment() {
    private val binding by lazy {
        FragmentCollectionDetailBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        binding.btnBack.setOnClickListener {
            fragmentManager?.popBackStackImmediate()
        }

        binding.txtShow.setText(viewModel.collectionText)

        binding.imgCopy.setOnClickListener {
            val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", viewModel.collectionText)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(requireActivity(), "Text copied", Toast.LENGTH_SHORT).show()
        }
        binding.imgShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, viewModel.collectionText)
            }
            startActivity(Intent.createChooser(sendIntent, "Share via"))
        }

        return binding.root
    }
}