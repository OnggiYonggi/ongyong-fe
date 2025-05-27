package com.bravepeople.onggiyonggi.presentation.main.character

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseCollectionDto
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterCollectionBinding
import com.bravepeople.onggiyonggi.extension.character.AllCharacterState
import com.bravepeople.onggiyonggi.extension.character.CollectionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CharacterCollectionActivity:AppCompatActivity() {
    private lateinit var binding:ActivityCharacterCollectionBinding
    private val characterCollectionViewModel:CharacterCollectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds(){
        binding=ActivityCharacterCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting(){
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.WHITE     // 하단 네비게이션 바 배경 흰색

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // 아이콘을 어둡게 (밝은 배경일 때)
            isAppearanceLightNavigationBars = true  // 네비게이션 바 아이콘도 어둡게
        }

        showCollections()
        clickBackButton()
    }

    private fun showCollections(){
        val collectionAdapter= CharacterCollectionAdapter(
            clickCharacterIndex = {index, collectionList ->
                Timber.d("activity에서 캐릭터 데이터 받음: ${collectionList}")
                clickCharacter(index, collectionList)
            }
        )
        binding.rvCollection.adapter=collectionAdapter

        lifecycleScope.launch {
            combine(
                characterCollectionViewModel.collectionState,
                characterCollectionViewModel.allCharacterState
            ) { collectionState, allCharacterState ->
                collectionState to allCharacterState
            }.collect { (collectionState, allCharacterState) ->
                when {
                    collectionState is CollectionState.Success && allCharacterState is AllCharacterState.Success -> {
                        // 둘 다 성공했을 때만 처리
                        collectionAdapter.getList(collectionState.collectionDto.data, allCharacterState.characterDto.data)
                    }
                    collectionState is CollectionState.Error-> {
                        Timber.e("error: ${collectionState.message}")
                    }
                    allCharacterState is AllCharacterState.Error->{
                        Timber.e("error: ${allCharacterState.message}")
                    }
                    // 나머지 경우는 로딩 상태거나 아직 준비 안 된 상태
                }
            }
        }

        val token=intent.getStringExtra("accessToken")
        token?.let {
            characterCollectionViewModel.allCharacter(token)
            characterCollectionViewModel.collection(token)
        }

    }

    private fun clickCharacter(index:Int,collectionList: List<ResponseCollectionDto.Data.CharacterResponseDto>){
        val intent=Intent(this, CharacterCollectionDetailActivity::class.java)
        intent.putExtra("startIndex", index)
        intent.putParcelableArrayListExtra(
            "collectionList",
            ArrayList(collectionList) // 꼭 ArrayList로 변환해야 함!!
        )
        startActivity(intent)
        this.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.stay_still
        )
    }

    private fun clickBackButton(){
        binding.btnBack.setOnClickListener{
            finish()
            overridePendingTransition( R.anim.stay_still, R.anim.slide_out_right)
        }

        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
        }
    }
}