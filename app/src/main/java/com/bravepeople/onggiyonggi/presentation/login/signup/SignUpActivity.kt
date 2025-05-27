package com.bravepeople.onggiyonggi.presentation.login.signup

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ActivitySignupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setting()
    }

    private fun setting(){
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.WHITE     // 하단 네비게이션 바 배경 흰색

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // 아이콘을 어둡게 (밝은 배경일 때)
            isAppearanceLightNavigationBars = true  // 네비게이션 바 아이콘도 어둡게
        }
    }
}
