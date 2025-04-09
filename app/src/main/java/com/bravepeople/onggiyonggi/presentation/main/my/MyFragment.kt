package com.bravepeople.onggiyonggi.presentation.main.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentMyBinding

class MyFragment : Fragment(R.layout.fragment_my) {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivProfile.load(R.drawable.img_user2) {
            transformations(CircleCropTransformation())
        }

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

        val myReviewAdapter = MyReviewAdapter()
        binding.rvReviewImages.adapter = myReviewAdapter
        myReviewAdapter.getImageList(dummyReviews)

        binding.rvReviewImages.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.tvReviewTitle.text = "내 리뷰 ${dummyReviews.size}개"
    }

}
