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
import coil3.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Character
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterCollectionDetailBinding
import kotlinx.serialization.json.Json
import timber.log.Timber

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
        if (character != null) {
            setCards(character)
        }
        clickBackButton()
    }

    private fun setCards(character:com.bravepeople.onggiyonggi.data.Character){
        with(binding){
            tvName.text=character.name
            ivCharacter.load(character.image)
            tvDescription.text=character.description

            clCardFront.setOnClickListener{
                Timber.d("click front")
                flipToBack()
            }
            clCardBack.setOnClickListener{
                Timber.d("click back")
                flipToFront()
            }
        }
    }

    private fun flipToBack() {
        val outAnimator = ObjectAnimator.ofFloat(binding.clCardFront, "rotationY", 0f, 90f)
        val inAnimator = ObjectAnimator.ofFloat(binding.clCardBack, "rotationY", -90f, 0f)
        outAnimator.duration = 300
        inAnimator.duration = 300

        outAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.clCardFront.visibility = View.GONE
                binding.clCardBack.visibility = View.VISIBLE
            }
        })

        AnimatorSet().apply {
            playSequentially(outAnimator, inAnimator)
            start()
        }
    }

    private fun flipToFront() {
        val outAnimator = ObjectAnimator.ofFloat(binding.clCardBack, "rotationY", 0f, 90f)
        val inAnimator = ObjectAnimator.ofFloat(binding.clCardFront, "rotationY", -90f, 0f)
        outAnimator.duration = 300
        inAnimator.duration = 300

        outAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.clCardBack.visibility = View.GONE
                binding.clCardFront.visibility = View.VISIBLE

            }
        })

        AnimatorSet().apply {
            playSequentially(outAnimator, inAnimator)
            start()
        }
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