package com.bravepeople.onggiyonggi.presentation.main.character

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterCollectionDetailBinding
import kotlinx.serialization.json.Json

class CharacterCollectionDetailActivity:AppCompatActivity() {
    private lateinit var binding:ActivityCharacterCollectionDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds(){
        binding=ActivityCharacterCollectionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting(){
        val json = intent.getStringExtra("character_json")
        val character = json?.let { Json.decodeFromString<Character>(it) }
        clickBackButton()
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