package com.bravepeople.onggiyonggi.presentation.main.character

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Character
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCollectionDto
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterCollectionBinding
import com.bravepeople.onggiyonggi.extension.character.CollectionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
            characterCollectionViewModel.collectionState.collect{state->
                when(state){
                    is CollectionState.Success->{
                        collectionAdapter.getList(state.collectionDto.data)
                    }
                    is CollectionState.Loading->{}
                    is CollectionState.Error->{
                        Timber.e("collection state error!")
                    }
                }
            }
        }

        val token=intent.getStringExtra("accessToken")
        token?.let {
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