package com.bravepeople.onggiyonggi.presentation

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.bravepeople.onggiyonggi.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.presentation.mypage.MyPageFragment
import com.bravepeople.onggiyonggi.presentation.main.character.CharacterFragment
import com.bravepeople.onggiyonggi.presentation.main.character.CharacterGachaActivity
import com.bravepeople.onggiyonggi.presentation.main.home.HomeFragment
import com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewFragment
import com.bravepeople.onggiyonggi.presentation.main.my.MyFragment
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
        clickBottomNavigation()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // 아이콘을 어둡게 (밝은 배경일 때)
            isAppearanceLightNavigationBars = true  // 네비게이션 바 아이콘도 어둡게
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bnvMain) { view, insets ->
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = navBarHeight
            }
            insets
        }
    }

    private fun setFirstFragment() {
        binding.bnvMain.selectedItemId = binding.bnvMain.menu.getItem(0).itemId
        replaceFragment(HomeFragment())
        clickBottomNavigation()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTag = fragment::class.java.simpleName
        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        val homeFragment = supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)
        val characterFragment = supportFragmentManager.findFragmentByTag(CharacterFragment::class.java.simpleName)
        val myFragment = supportFragmentManager.findFragmentByTag(MyFragment::class.java.simpleName)

        supportFragmentManager.beginTransaction().apply {
            homeFragment?.let { hide(it) }
            characterFragment?.let { hide(it) }
            myFragment?.let { hide(it) }

            // 해당 Fragment가 스택에 없으면 새로 추가
            if (existingFragment == null) {

                add(R.id.fcv_main, fragment, fragmentTag)
            } else {
                // 이미 존재하면 그 Fragment를 보여줌
                show(existingFragment)
            }

            commit()
        }
    }
    
    private fun clickBottomNavigation(){
        binding.bnvMain.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_home->{
                    replaceFragment(HomeFragment())
                    true
                }
                /*R.id.menu_character->{
                    replaceFragment(CharacterFragment())
                    true
                }*/
                R.id.menu_my->{
                    replaceFragment(MyPageFragment())
                    true
                }
                else->{
                    false
                }
            }
        }
    }

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