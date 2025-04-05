package com.bravepeople.onggiyonggi.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bravepeople.onggiyonggi.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.presentation.main.home.HomeFragment
import com.bravepeople.onggiyonggi.presentation.main.review.ReviewFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
    }

    private fun initBinds() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setting()
    }

    private fun setting() {
        setFirstFragment()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // 아이콘을 어둡게 (밝은 배경일 때)
            isAppearanceLightNavigationBars = true  // 네비게이션 바 아이콘도 어둡게
        }
    }

    private fun setFirstFragment() {
        binding.bnvMain.selectedItemId = binding.bnvMain.menu.getItem(0).itemId
        replaceFragment(HomeFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_main, fragment)
            .commit()
    }

    private fun openReviewFragmentWithStore(store: StoreOrReceipt.Store) {
        val bundle = Bundle().apply {
            putSerializable("search", store)
        }

        val reviewFragment = ReviewFragment().apply {
            arguments = bundle
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_review, reviewFragment)
            .commit()
    }

    /*private fun clickBottomNavigation(){
        binding.bnvMain.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_home->{
                    replaceFragment(HomeFragment(())
                    true
                }
                R.id.menu_character->{
                    replaceFragment(CharacterFragment())
                    true
                }
                R.id.menu_my->{
                    replaceFragment(MyFragment())
                    true
                }
                else->{
                    false
                }
            }
        }
    }*/

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val openFragment = intent.getStringExtra("openFragment")
        val fromReview = intent.getBooleanExtra("fromReview", false)

        if (openFragment == "review" && fromReview) {
            val fragment = supportFragmentManager.findFragmentById(R.id.fcv_main)
            if (fragment is HomeFragment) {
                fragment.openReviewFragment()
            }
        }
    }


}