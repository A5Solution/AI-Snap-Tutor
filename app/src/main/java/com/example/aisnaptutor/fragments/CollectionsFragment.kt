package com.example.aisnaptutor.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aisnaptutor.AppDatabase
import com.example.aisnaptutor.adapters.CollectionAdapter
import com.example.aisnaptutor.OnCollectionItemClick
import com.example.aisnaptutor.R
import com.example.aisnaptutor.databinding.FragmentCollectionsBinding
import com.example.aisnaptutor.interfaces.ChatMessageDao
import ke.derrick.imagetotext.viewmodels.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectionsFragment : Fragment(), OnCollectionItemClick {
    private val binding by lazy {
        FragmentCollectionsBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: CollectionAdapter
    private lateinit var chatMessageDao: ChatMessageDao
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        chatMessageDao = AppDatabase.getDatabase(requireContext()).chatMessageDao()

        GlobalScope.launch(Dispatchers.IO) {
            val events = chatMessageDao.getAllChatMessages()
            withContext(Dispatchers.Main) {
                if (events.isEmpty()) {

                } else {
                    adapter = CollectionAdapter(
                        requireActivity(),
                        this@CollectionsFragment,
                        events
                    )
                    binding.collectionRecView.adapter = adapter
                    binding.collectionRecView.layoutManager =
                        LinearLayoutManager(requireActivity())
                }
            }
        }
        binding.btnBack.setOnClickListener {
            fragmentManager?.popBackStackImmediate()
        }

        return binding.root
    }

    override fun onItemClick(text: String) {
        viewModel.collectionText = text
        findNavController().navigate(R.id.action_collectionsFragment_to_collectionDetailFragment)
    }
}