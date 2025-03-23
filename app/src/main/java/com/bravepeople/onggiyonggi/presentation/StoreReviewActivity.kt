package com.bravepeople.onggiyonggi.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bravepeople.onggiyonggi.databinding.ActivityStoreReviewBinding
import com.bravepeople.onggiyonggi.presentation.model.Review
import com.bravepeople.onggiyonggi.presentation.adapter.ReviewAdapter
import com.bravepeople.onggiyonggi.R


class StoreReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoreReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoreReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val dummyReviews = listOf(
            Review(1, "userA", "2025.03.08", R.drawable.img_user1, R.drawable.img_review1, true),
            Review(2, "userB", "2025.03.08", R.drawable.img_user2, R.drawable.img_review2, false),
            Review(3, "userC", "2025.03.08", R.drawable.img_user3, R.drawable.img_review3, true)
        )

        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reviewRecyclerView.adapter = ReviewAdapter(dummyReviews)
    }
}