package com.bravepeople.onggiyonggi.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bravepeople.onggiyonggi.databinding.FragmentSearchBinding
import com.bravepeople.onggiyonggi.presentation.home.SearchRecentAdapter
import com.bravepeople.onggiyonggi.presentation.home.SearchViewModel
import com.bravepeople.onggiyonggi.presentation.review.ReviewAdapter
import com.bravepeople.onggiyonggi.presentation.review.ReviewViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior

class SearchFragment:Fragment() {
    private var _binding: FragmentSearchBinding?=null
    private val binding: FragmentSearchBinding
        get()= requireNotNull(_binding){"receipt fragment is null"}

    private val searchViewModel:SearchViewModel by viewModels()

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
        val searchRecentAdapter=SearchRecentAdapter(requireContext(),
            clickStore = {search ->

            },
            clickDelete = {search -> })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}