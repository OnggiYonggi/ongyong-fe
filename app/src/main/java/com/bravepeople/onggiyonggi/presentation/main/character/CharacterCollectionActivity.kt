package com.bravepeople.onggiyonggi.presentation.main.character

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterCollectionBinding

class CharacterCollectionActivity:AppCompatActivity() {
    private lateinit var binding:ActivityCharacterCollectionBinding

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
        clickBackButton()
    }

    private fun clickBackButton(){
        binding.btnBack.setOnClickListener{
            finish()
            overridePendingTransition( R.anim.stay_still, R.anim.slide_out_right)
        }
    }
}