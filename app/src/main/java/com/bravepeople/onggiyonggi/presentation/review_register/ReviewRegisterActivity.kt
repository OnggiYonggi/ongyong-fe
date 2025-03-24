package com.bravepeople.onggiyonggi.presentation.review_register

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ActivityReviewBinding

class ReviewRegisterActivity:AppCompatActivity() {
    private  lateinit var binding: ActivityReviewBinding
    private val reviewViewModel:ReviewRegisterViewModel by viewModels()
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
    }

}