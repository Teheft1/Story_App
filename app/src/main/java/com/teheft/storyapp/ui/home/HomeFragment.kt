package com.teheft.storyapp.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.datastore.dataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.teheft.storyapp.data.Result
import com.teheft.storyapp.data.pref.dataStore
import com.teheft.storyapp.databinding.FragmentHomeBinding
import com.teheft.storyapp.utils.StoriesViewAdapter
import com.teheft.storyapp.utils.ViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val homeViewModel by viewModels<HomeViewModel> {
        context?.let { ViewModelFactory.getInstance(it, it.dataStore) }!!
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storiesViewAdapter = StoriesViewAdapter(requireContext())

//        lifecycleScope.launch {
//            binding.progressBar.visibility = View.VISIBLE
        homeViewModel.getStories()
        homeViewModel.result.observe(viewLifecycleOwner){result ->
                if(result != null){
                    when(result){
                        is Result.Error -> {
                            Log.d("home", result.error.toString())
                            binding.progressBar.visibility = View.GONE
                            AlertDialog.Builder(requireActivity())
                                .setTitle("Error")
                                .setMessage(result.error)
                                .setPositiveButton("Coba Lagi"){dialog,_ ->
                                    homeViewModel.getStories()
                                    dialog.dismiss()
                                }
                                .setNegativeButton("Tutup"){dialog,_ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val stories = result.data
                            storiesViewAdapter.submitList(stories)
                        }
                    }
                }
//            }
        }
        binding.rvActive.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storiesViewAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}