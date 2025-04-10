package com.bravepeople.onggiyonggi.presentation.main.character

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Character
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterCollectionBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

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
            clickCharacterIndex = {index ->
                Timber.d("activity에서 캐릭터 데이터 받음: ${index}")
                clickCharacter(index)
            }
        )
        binding.rvCollection.adapter=collectionAdapter
        collectionAdapter.getList(characterCollectionViewModel.getCollectionList())
    }

    private fun clickCharacter(index: Int){
        val intent=Intent(this, CharacterCollectionDetailActivity::class.java)
        intent.putExtra("startIndex", index)
        intent.putExtra("characterList", ArrayList(characterCollectionViewModel.getCollectionList()))
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