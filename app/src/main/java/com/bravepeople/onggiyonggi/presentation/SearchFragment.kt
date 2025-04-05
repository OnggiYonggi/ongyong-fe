package com.bravepeople.onggiyonggi.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bravepeople.onggiyonggi.databinding.FragmentSearchBinding
import com.bravepeople.onggiyonggi.presentation.main.home.search.SearchRecentAdapter
import com.bravepeople.onggiyonggi.presentation.main.home.search.SearchViewModel

class SearchFragment:Fragment() {
    private var _binding: FragmentSearchBinding?=null
    private val binding: FragmentSearchBinding
        get()= requireNotNull(_binding){"receipt fragment is null"}

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        val searchRecentAdapter= SearchRecentAdapter(requireContext(),
            clickStore = {search ->

            },
            clickDelete = {search -> })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}