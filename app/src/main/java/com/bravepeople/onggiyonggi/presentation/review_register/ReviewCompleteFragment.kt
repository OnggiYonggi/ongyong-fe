package com.bravepeople.onggiyonggi.presentation.review_register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bravepeople.onggiyonggi.databinding.FragmentReviewCompleteBinding
import timber.log.Timber

class ReviewCompleteFragment:Fragment() {
    private var _binding:FragmentReviewCompleteBinding?=null
    private val binding:FragmentReviewCompleteBinding
        get()= requireNotNull(_binding){"receipt fragment is null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        Timber.d("review complete fragment!")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}