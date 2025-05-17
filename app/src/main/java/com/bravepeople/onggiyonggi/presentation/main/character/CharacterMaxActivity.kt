package com.bravepeople.onggiyonggi.presentation.main.character

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseGetPetDto
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterMaxBinding
import com.bravepeople.onggiyonggi.extension.character.AddMaxState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CharacterMaxActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCharacterMaxBinding

    private val characterMaxViewModel: CharacterMaxViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds() {
        binding = ActivityCharacterMaxBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        playRewardAnimation()
        onBackPressedDispatcher.addCallback(this) {
            // 아무 것도 하지 않으면 뒤로가기 무시됨
            Timber.d("뒤로가기 눌렀지만 막았음")
        }

        getPet()
    }

    private fun getPet() {
        val pet = intent.getParcelableExtra<ResponseGetPetDto.Data>("character")
        if (pet != null) {
            with(binding) {
                ivCharacter.load(pet.naturalMonumentCharacter.imageUrl)
                tvCollection.text =
                    getString(R.string.character_max_collection, pet.naturalMonumentCharacter.name)
            }
        }

        lifecycleScope.launch {
            characterMaxViewModel.addMaxState.collect { state ->
                when (state) {
                    is AddMaxState.Success -> {
                        slideUpCard()
                    }

                    is AddMaxState.Loading -> {}
                    is AddMaxState.Error -> {
                        Timber.e("add max state error!!")
                    }
                }
            }
        }
    }

    private fun playRewardAnimation() {
        binding.lottieSparkle.playAnimation()

        val rotateAnimator = ObjectAnimator.ofFloat(binding.clCard, "rotationY", 0f, 360f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
        }

        // 확대 애니메이션
        val scaleXAnimator = ObjectAnimator.ofFloat(binding.clCard, "scaleX", 0f, 1f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
        }
        val scaleYAnimator = ObjectAnimator.ofFloat(binding.clCard, "scaleY", 0f, 1f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
        }


        // 애니메이션 세트로 실행
        AnimatorSet().apply {
            playTogether(rotateAnimator, scaleXAnimator, scaleYAnimator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // 확대 후 위로 슬라이드 및 텍스트 페이드인 실행
                    val token = intent.getStringExtra("accessToken")
                    token?.let {
                        characterMaxViewModel.addMax(token)
                    }
                }
            })
            start()
        }
    }

    private fun slideUpCard() {
        // 위로 슬라이드 애니메이션
        val slideAnimator =
            ObjectAnimator.ofFloat(binding.clCard, "translationY", 0f, -200f).apply {
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
            }

        // 텍스트 페이드인 애니메이션
        binding.tvCollection.visibility = View.VISIBLE
        val fadeInAnimator = ObjectAnimator.ofFloat(binding.tvCollection, "alpha", 0f, 1f).apply {
            duration = 500
        }


        // 애니메이션 세트로 실행
        AnimatorSet().apply {
            playTogether(slideAnimator, fadeInAnimator)
            start()
        }

        binding.root.postDelayed({
            setResult(RESULT_OK)
            finish()
            overridePendingTransition(0, R.anim.slide_out_down)
        }, 1500)

        /*binding.clCard.postDelayed({
            moveCardToCollection()
        }, 1500)*/
    }

    /*   private fun moveCardToCollection() {
           // 카드와 버튼의 절대 위치 얻기
           val cardLoc = IntArray(2)
           val btnLoc = IntArray(2)
           binding.clCard.getLocationOnScreen(cardLoc)
           binding.btnCollection.getLocationOnScreen(btnLoc)

           // 카드와 버튼의 중심 좌표 계산
           val cardCenterX = cardLoc[0] + binding.clCard.width / 2f
           val cardCenterY = cardLoc[1] + binding.clCard.height / 2f

           val btnCenterX = btnLoc[0] + binding.btnCollection.width / 2f
           val btnCenterY = btnLoc[1] + binding.btnCollection.height / 2f

           // translationX/Y 값은 뷰가 현재 위치에서 얼마나 이동할지를 의미
           // 중심 좌표의 차이만큼 이동
           val moveX = btnCenterX - cardCenterX
           val moveY = btnCenterY - cardCenterY

           val rotate = ObjectAnimator.ofFloat(binding.clCard, "rotation", 0f, 360f)
           val scaleX = ObjectAnimator.ofFloat(binding.clCard, "scaleX", 1.5f, 0.2f)
           val scaleY = ObjectAnimator.ofFloat(binding.clCard, "scaleY", 1.5f, 0.2f)
           val translationX = ObjectAnimator.ofFloat(binding.clCard, "translationX", 0f, moveX)
           val translationY = ObjectAnimator.ofFloat(binding.clCard, "translationY", 0f, moveY)

           AnimatorSet().apply {
               playTogether(rotate, scaleX, scaleY, translationX, translationY)
               duration = 1000
               interpolator = AccelerateDecelerateInterpolator()

               addListener(object : AnimatorListenerAdapter() {
                   override fun onAnimationEnd(animation: Animator) {
                       Timber.d("카드가 컬렉션으로 이동 완료됨!")
                       Timber.d("card center: $cardCenterX, $cardCenterY")
                       Timber.d( "btn center: $btnCenterX, $btnCenterY")
                   }
               })
               start()
           }
       }*/
}