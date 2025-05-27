package com.bravepeople.onggiyonggi.presentation.main.character

import android.graphics.Color
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseCollectionDto
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterCollectionDetailBinding

class CharacterCollectionDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCharacterCollectionDetailBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var characterList: List<ResponseCollectionDto.Data.CharacterResponseDto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds() {
        binding = ActivityCharacterCollectionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.WHITE     // 하단 네비게이션 바 배경 흰색

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // 아이콘을 어둡게 (밝은 배경일 때)
            isAppearanceLightNavigationBars = true  // 네비게이션 바 아이콘도 어둡게
        }

        val index = intent.getIntExtra("index", -1)
        setLists(index)
        clickBackButton()
    }

    private fun setLists(index: Int) {
        characterList = intent.getParcelableArrayListExtra<ResponseCollectionDto.Data.CharacterResponseDto>("collectionList")!!
        val startIndex = intent.getIntExtra("startIndex", index)

        val collectedCharacters = characterList.filter { it.imageURL.isNotEmpty() }
        val startCharacter = characterList.getOrNull(startIndex)
        val collectedIndex = collectedCharacters.indexOfFirst { it == startCharacter }.coerceAtLeast(0)

        val characterCollectionDetailAdapter = CharacterCollectionDetailAdapter(this)
        viewPager = binding.vpCollection
        viewPager.adapter = characterCollectionDetailAdapter

        characterCollectionDetailAdapter.getList(collectedCharacters)

        // 필터링된 리스트 기준으로 index 설정
        viewPager.setCurrentItem(collectedIndex, false)
    }

    private fun clickBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
        }

        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
        }
    }
}