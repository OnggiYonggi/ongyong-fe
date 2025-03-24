package com.bravepeople.onggiyonggi.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bravepeople.onggiyonggi.databinding.ActivityMainBinding
import com.bravepeople.onggiyonggi.presentation.review.ReviewFragment

class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setting()
    }

    private fun init(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting(){
        binding.btnReview.setOnClickListener{
            val reviewFragment=ReviewFragment()
            reviewFragment.show(supportFragmentManager, "ReviewFragment")

            /*val intent=Intent(this, ReviewActivity::class.java)
            startActivity(intent)*/
        }
    }
}