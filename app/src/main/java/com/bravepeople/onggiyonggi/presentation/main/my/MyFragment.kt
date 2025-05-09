package com.bravepeople.onggiyonggi.presentation.main.my

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import coil3.load
import coil3.transform.CircleCropTransformation
import coil3.request.transformations
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.databinding.FragmentMyBinding
import com.bravepeople.onggiyonggi.presentation.main.home.review.review_detail.ReviewDetailActivity

class MyFragment : Fragment(R.layout.fragment_my) {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!

    private enum class SortType { LATEST, LIKE }

    private var currentSort = SortType.LATEST
    private lateinit var myReviewAdapter: MyReviewAdapter

    private val dummyReviews = listOf(
        Review(
            id = 1,
            imageResId = R.drawable.img_review1,
            likeCount = 12,
            date = "2024-04-18",
            userName = "유찬연",
            reviewDate = "2024.04.18",
            profile = R.drawable.img_user2,
            food = R.drawable.img_review1
        ),
        Review(
            id = 2,
            imageResId = R.drawable.img_review2,
            likeCount = 5,
            date = "2024-04-16",
            userName = "유찬연",
            reviewDate = "2024.04.16",
            profile = R.drawable.img_user2,
            food = R.drawable.img_review2
        ),
        Review(
            id = 3,
            imageResId = R.drawable.img_review3,
            likeCount = 22,
            date = "2024-04-12",
            userName = "유찬연",
            reviewDate = "2024.04.12",
            profile = R.drawable.img_user2,
            food = R.drawable.img_review3
        )
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivProfile.load(R.drawable.img_user2) {
            transformations(CircleCropTransformation())
        }

        binding.ivEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.ivSetting.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
        }

        myReviewAdapter = MyReviewAdapter { review ->
            val intent = Intent(requireContext(), ReviewDetailActivity::class.java)
            intent.putExtra("review", review)
            startActivity(intent)
        }
        binding.rvReviewImages.adapter = myReviewAdapter
        binding.rvReviewImages.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.tvSortLatest.setOnClickListener {
            currentSort = SortType.LATEST
            updateReviewList()
            highlightSelected(binding.tvSortLatest, binding.tvSortLike)
        }

        binding.tvSortLike.setOnClickListener {
            currentSort = SortType.LIKE
            updateReviewList()
            highlightSelected(binding.tvSortLike, binding.tvSortLatest)
        }


        binding.root.post {
            myReviewAdapter = MyReviewAdapter { review ->
                val intent = Intent(requireContext(), ReviewDetailActivity::class.java)
                intent.putExtra("review", review)
                startActivity(intent)
            }
            binding.rvReviewImages.adapter = myReviewAdapter
            binding.rvReviewImages.layoutManager = GridLayoutManager(requireContext(), 3)
            updateReviewList()
            binding.tvReviewTitle.text = "내 리뷰 ${dummyReviews.size}개"

            highlightSelected(binding.tvSortLatest, binding.tvSortLike)
        }
    }

    private fun highlightSelected(selected: TextView, unselected: TextView) {
        selected.setTypeface(null, Typeface.BOLD)
        selected.setTextColor(Color.parseColor("#000000"))

        unselected.setTypeface(null, Typeface.NORMAL)
        unselected.setTextColor(Color.parseColor("#888888"))
    }

    private fun updateReviewList() {
        val sortedList = when (currentSort) {
            SortType.LATEST -> dummyReviews.sortedByDescending { it.date }
            SortType.LIKE -> dummyReviews.sortedByDescending { it.likeCount }
        }
        myReviewAdapter.setReviewList(sortedList)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}
