package com.bravepeople.onggiyonggi.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentMypageBinding
import com.bumptech.glide.Glide

class MyPageFragment : Fragment(R.layout.fragment_mypage) {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
            .load(R.drawable.img_user2)
            .circleCrop()
            .into(binding.ivProfile)

        binding.ivEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.ivSetting.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }

        val dummyReviews = listOf(
            R.drawable.img_review1,
            R.drawable.img_review2,
            R.drawable.img_review3,
        )

        val adapter = ReviewImageAdapter(dummyReviews)
        binding.rvReviewImages.adapter = adapter

        binding.rvReviewImages.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.tvReviewTitle.text = "내 리뷰 ${dummyReviews.size}개"
    }

}
