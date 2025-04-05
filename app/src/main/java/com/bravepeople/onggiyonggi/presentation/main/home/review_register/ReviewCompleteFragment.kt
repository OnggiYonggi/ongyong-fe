package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentReviewCompleteBinding
import com.bravepeople.onggiyonggi.presentation.MainActivity
import timber.log.Timber

class ReviewCompleteFragment:Fragment() {
    private var _binding:FragmentReviewCompleteBinding?=null
    private val binding:FragmentReviewCompleteBinding
        get()= requireNotNull(_binding){"receipt fragment is null"}

    private val reviewCompleteViewModel: ReviewCompleteViewModel by viewModels()
    private val reviewRegisterViewModel: ReviewRegisterViewModel by activityViewModels()

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

        val lottieView=binding.lavEarth
        lottieView.setAnimation(R.raw.earth)
        lottieView.playAnimation()

        setCharacter()
        setReview()
        clickEndButton()
    }

    private fun setCharacter(){
        val name=reviewCompleteViewModel.name
        val percentage=reviewCompleteViewModel.likeability.toString()+"%"
        val text=context?.getString(R.string.review_complete_character_likeability, name, percentage)
        val spannable=SpannableString(text)

        val start=text!!.indexOf(percentage)
        val end=start + percentage.length

        spannable.setSpan(
            ForegroundColorSpan(Color.RED),
            start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvReviewCharacter.text=spannable
    }

    private fun setReview(){
        with(binding){
            tvMyReviewStore.text=reviewCompleteViewModel.storeName
            tvMyReviewContent.text=reviewCompleteViewModel.review
        }
    }

    private fun clickEndButton(){
        binding.btnEnd.setOnClickListener{
            val beforeActivity = reviewRegisterViewModel.getBeforeActivity()
            val store = reviewRegisterViewModel.getStore()

            if (beforeActivity == null) {
                requireActivity().finish()
            } else {
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    putExtra("openFragment", "review")
                    putExtra("store", store)
                    putExtra("fromReview", true)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}