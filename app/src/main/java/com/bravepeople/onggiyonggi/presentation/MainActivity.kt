package com.bravepeople.onggiyonggi.presentation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.bravepeople.onggiyonggi.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.extension.home.GetEnumState
import com.bravepeople.onggiyonggi.presentation.main.my.MyFragment
import com.bravepeople.onggiyonggi.presentation.main.character.CharacterFragment
import com.bravepeople.onggiyonggi.presentation.main.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel:MainViewModel by viewModels()
    private lateinit var navController: NavController
    /*private val homeFragment = HomeFragment()
    private val characterFragment = CharacterFragment()
    private val myFragment = MyFragment()*/

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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.WHITE     // 하단 네비게이션 바 배경 흰색

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

        val token=intent.getStringExtra("accessToken")
        if (token != null) {
            mainViewModel.saveToken(token)
        }else mainViewModel.saveToken("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ4Iiwicm9sZSI6IlJPTEVfQ1VTVE9NRVIiLCJpYXQiOjE3NDgwOTUxOTQsImV4cCI6MTc0ODIxNjE1NH0.UIgiZynqJSP145PHSZnwiffnRY-kKjfxQzSODYJvhcM")

        lifecycleScope.launch {
            mainViewModel.accessToken.observe(this@MainActivity){
                lifecycleScope.launch {
                    mainViewModel.getEnumState.collect{state->
                        when(state){
                            is GetEnumState.Success->{
                                Timber.d("get enum state success!")
                            }
                            is GetEnumState.Loading->{}

                            is GetEnumState.Error->{
                                Timber.e("get enum state error!")
                            }
                        }
                    }
                }

                mainViewModel.getEnum()
            }
        }

        setFirstFragment()
        clickBNV()
        //clickBottomNavigation()
    }

    private fun setFirstFragment() {
        // 네비게이션 컨트롤러 초기화
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv_main) as NavHostFragment
        navController = navHostFragment.navController

        // BottomNavigationView와 NavController 연결
        binding.bnvMain.setupWithNavController(navController)
        /*binding.bnvMain.selectedItemId = binding.bnvMain.menu.getItem(0).itemId
        replaceFragment(homeFragment)
        clickBottomNavigation()*/
    }

    private fun clickBNV(){
        binding.bnvMain.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination?.id

            if (currentDestination == item.itemId) {
                // 같은 메뉴 다시 클릭 시 해당 Fragment 새로고침 호출
                val fragment =
                    supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull()
                when (item.itemId) {
                    R.id.homeFragment -> if (fragment is HomeFragment) fragment.refreshData()
                    R.id.characterFragment -> if (fragment is CharacterFragment) fragment.refreshData()
                    R.id.myFragment -> if (fragment is MyFragment) fragment.refreshData()
                }
                true
            } else {
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
    }

    /*private fun replaceFragment(fragment: Fragment) {
        val fragmentTag = fragment::class.java.simpleName
        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        val homeFragment = supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)
        val characterFragment = supportFragmentManager.findFragmentByTag(CharacterFragment::class.java.simpleName)
        val myFragment = supportFragmentManager.findFragmentByTag(MyFragment::class.java.simpleName)

        supportFragmentManager.beginTransaction().apply {
            homeFragment?.let { hide(it) }
            characterFragment?.let { hide(it) }
            myFragment?.let { hide(it) }

            if (existingFragment == null) {
                add(R.id.fcv_main, fragment, fragmentTag)
            } else {
                show(existingFragment)
            }

            commit()
        }
    }

    private fun clickBottomNavigation() {
        binding.bnvMain.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_home -> {
                    replaceFragment(homeFragment)
                    true
                }
                R.id.menu_character -> {
                    replaceFragment(characterFragment)
                    true
                }
                R.id.menu_my -> {
                    replaceFragment(myFragment)
                    true
                }
                else -> false
            }
        }
    }*/

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val openFragment = intent.getStringExtra("openFragment")
        val fromReview = intent.getBooleanExtra("fromReview", false)
        val goTo = intent.getStringExtra("goTo")

        if (openFragment == "review" && fromReview) {
            val fragment = supportFragmentManager.findFragmentById(R.id.fcv_main)
            if (fragment is HomeFragment) {
                fragment.openReviewFragment()
            }
        } else if (goTo == "character") {
            binding.bnvMain.selectedItemId = R.id.characterFragment
        }
    }
}