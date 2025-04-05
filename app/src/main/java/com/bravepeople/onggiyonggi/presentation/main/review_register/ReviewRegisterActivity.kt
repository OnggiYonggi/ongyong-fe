package com.bravepeople.onggiyonggi.presentation.main.review_register

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.findNavController
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ActivityReviewBinding
import timber.log.Timber

class ReviewRegisterActivity:AppCompatActivity() {
    private  lateinit var binding: ActivityReviewBinding
    private val reviewRegisterViewModel: ReviewRegisterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setting()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val navController = findNavController(R.id.fcv_review)
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.nav_graph)

        val bundle = Bundle().apply {
            putSerializable("photoType", PhotoType.RECEIPT)
        }

        navGraph.setStartDestination(R.id.photoFragment)
        navController.setGraph(navGraph, bundle)
    }


    private fun init(){
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // 아이콘을 어둡게 (밝은 배경일 때)
            isAppearanceLightNavigationBars = true  // 네비게이션 바 아이콘도 어둡게
        }

        val registerActivity=intent.getStringExtra("registerActivity")
        if(registerActivity!=null) reviewRegisterViewModel.setBeforeActivity(registerActivity)
    }

}