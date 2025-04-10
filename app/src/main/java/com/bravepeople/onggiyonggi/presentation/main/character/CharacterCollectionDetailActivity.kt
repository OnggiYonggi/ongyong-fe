package com.bravepeople.onggiyonggi.presentation.main.character

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import coil3.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Character
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterCollectionDetailBinding
import kotlinx.serialization.json.Json
import timber.log.Timber

class CharacterCollectionDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCharacterCollectionDetailBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var characterList: List<Character>

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
        val index = intent.getIntExtra("index", -1)
        setLists(index)
        clickBackButton()
    }

    private fun setLists(index: Int) {
        characterList = intent.getSerializableExtra("characterList") as List<Character>
        val startIndex = intent.getIntExtra("startIndex", index)

        val collectedCharacters = characterList.filter { it.collected }
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