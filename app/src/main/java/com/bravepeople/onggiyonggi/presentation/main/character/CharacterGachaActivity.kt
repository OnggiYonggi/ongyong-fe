package com.bravepeople.onggiyonggi.presentation.main.character

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import coil.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseGetPetDto
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterGachaBinding
import com.bravepeople.onggiyonggi.extension.character.GetPetState
import com.bravepeople.onggiyonggi.extension.character.RandomPetState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CharacterGachaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCharacterGachaBinding

    private val characterGachaViewModel: CharacterGachaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
    }

    private fun initBinds() {
        binding = ActivityCharacterGachaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setting()
    }

    private fun setting() {
        Timber.d("gachaActivity")
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true  // 아이콘을 어둡게 (밝은 배경일 때)
            isAppearanceLightNavigationBars = true  // 네비게이션 바 아이콘도 어둡게
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

            binding.tvName.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = statusBarHeight
            }

            insets
        }

        val token = intent.getStringExtra("accessToken")
        if (token != null) {
            startRollingBall(token)
            clickNextButton()
        }
    }

    private fun startRollingBall(token: String) {
        binding.ivBall.post {
            val startX = -binding.ivBall.width.toFloat()

            // 화면 중앙까지 이동할 translationX 계산
            val screenCenterX = resources.displayMetrics.widthPixels / 2f
            val ballCenterX = binding.ivBall.left + binding.ivBall.width / 2f
            val centerX = screenCenterX - ballCenterX

            val totalDistance = centerX - startX
            val maxRotation = 1440f

            val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1000
                interpolator = DecelerateInterpolator(3f)

                addUpdateListener { animation ->
                    val progress = animation.animatedValue as Float

                    // 이동
                    val currentX = startX + (totalDistance * progress)
                    binding.ivBall.translationX = currentX

                    // 회전 (시계 방향 + 점점 느려지게)
                    val currentRotation = maxRotation * progress
                    binding.ivBall.rotation = currentRotation
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.ivBall.postDelayed({
                            showExplosionEffects(token)
                        }, 50)
                    }
                })
            }

            animator.start()
        }
    }

    private fun showExplosionEffects(token: String) {
        binding.ivBall.visibility = View.GONE

        with(binding.lavExplode) {
            visibility = View.VISIBLE
            speed = 1.3f
            playAnimation()
        }

        with(binding.lavFog) {
            visibility = View.VISIBLE
            speed = 2.5f
            playAnimation()
        }

        lifecycleScope.launch {
            val state =
                characterGachaViewModel.randomPetState.first { it is RandomPetState.Success || it is RandomPetState.Error }
            when (state) {
                is RandomPetState.Success -> {
                    Timber.d("random pet success!")
                    binding.lavExplode.addAnimatorListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            showCharacterUI(state.randomPetDto)
                        }
                    })
                }

                is RandomPetState.Loading -> {
                    with(binding) {
                        lavExplode.visibility = View.VISIBLE
                        lavFog.visibility = View.VISIBLE
                        lavExplode.playAnimation()
                        lavFog.playAnimation()
                    }
                }

                is RandomPetState.Error -> {
                    Timber.e("random pet state error!")

                }
            }
        }
       /* binding.lavExplode.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                lifecycleScope.launch {
                    val state =
                        characterGachaViewModel.randomPetState.first { it is RandomPetState.Success || it is RandomPetState.Error }
                    when (state) {
                        is RandomPetState.Success -> {
                            Timber.d("random pet success!")
                            // 폭발 효과 숨기기
                            with(binding) {
                                lavExplode.cancelAnimation()
                                lavFog.cancelAnimation()
                                lavExplode.visibility = View.GONE
                                lavFog.visibility = View.GONE

                                // 캐릭터 정보 UI 세팅
                                tvName.text =
                                    state.randomPetDto.data!!.naturalMonumentCharacter.name
                                ivCharacter.load(state.randomPetDto.data!!.naturalMonumentCharacter.imageUrl)
                                tvDescription.text =
                                    state.randomPetDto.data!!.naturalMonumentCharacter.description

                                // 카드 및 캐릭터 등장 애니메이션
                                with(clCardFront) {
                                    visibility = View.VISIBLE
                                    alpha = 0f
                                    scaleX = 0.5f
                                    scaleY = 0.5f
                                }
                                clCardFront.animate()
                                    .alpha(1f)
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(500)
                                    .start()
                                ivCharacter.animate()
                                    .alpha(1f)
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(500)
                                    .start()

                                btnNext.visibility = View.VISIBLE

                                clCardFront.setOnClickListener {
                                    flipToBack()
                                }
                                clCardBack.setOnClickListener {
                                    flipToFront()
                                }
                            }
                        }

                        is RandomPetState.Loading -> {
                            with(binding) {
                                lavExplode.visibility = View.VISIBLE
                                lavFog.visibility = View.VISIBLE
                                lavExplode.playAnimation()
                                lavFog.playAnimation()
                            }
                        }

                        is RandomPetState.Error -> {
                            Timber.e("random pet state error!")

                        }
                    }
                }
                *//* lifecycleScope.launch {
                     characterViewModel.getPetState.collect{state->
                         when(state) {
                             is GetPetState.Success -> {
                                 Timber.d("get pet success!")
                                 // 폭발 효과 숨기기
                                 with(binding) {
                                     lavExplode.cancelAnimation()
                                     lavFog.cancelAnimation()
                                     lavExplode.visibility = View.GONE
                                     lavFog.visibility = View.GONE

                                     // 캐릭터 정보 UI 세팅
                                     tvName.text = state.getPetDto.data!!.naturalMonumentCharacter.name
                                     ivCharacter.load(state.getPetDto.data!!.naturalMonumentCharacter.imageUrl)
                                     tvDescription.text = state.getPetDto.data!!.naturalMonumentCharacter.description

                                     // 카드 및 캐릭터 등장 애니메이션
                                     with(clCardFront) {
                                         visibility = View.VISIBLE
                                         alpha = 0f
                                         scaleX = 0.5f
                                         scaleY = 0.5f
                                     }
                                     clCardFront.animate()
                                         .alpha(1f)
                                         .scaleX(1f)
                                         .scaleY(1f)
                                         .setDuration(500)
                                         .start()
                                     ivCharacter.animate()
                                         .alpha(1f)
                                         .scaleX(1f)
                                         .scaleY(1f)
                                         .setDuration(500)
                                         .start()

                                     btnNext.visibility = View.VISIBLE

                                     clCardFront.setOnClickListener {
                                         flipToBack()
                                     }
                                     clCardBack.setOnClickListener {
                                         flipToFront()
                                     }
                                 }
                             }
                             is GetPetState.Loading-> {
                                 // 상태가 로딩 혹은 에러면 애니메이션 계속 보여주기
                                 with(binding) {
                                     lavExplode.visibility = View.VISIBLE
                                     lavFog.visibility = View.VISIBLE
                                     lavExplode.playAnimation()
                                     lavFog.playAnimation()
                                 }
                             }
                             is GetPetState.Error->{
                                 Timber.e( "getPetState error")
                             }
                         }
                     }
                 }*//*
            }
        })*/

        characterGachaViewModel.randomPet(token)
    }

    private fun showCharacterUI(pet: ResponseGetPetDto){
        with(binding) {
            lavExplode.removeAllAnimatorListeners()
            lavFog.removeAllAnimatorListeners()
            lavExplode.cancelAnimation()
            lavFog.cancelAnimation()
            lavExplode.visibility = View.GONE
            lavFog.visibility = View.GONE

            // 캐릭터 정보 UI 세팅
            tvName.text =
                pet.data!!.naturalMonumentCharacter.name
            ivCharacter.load(pet.data!!.naturalMonumentCharacter.imageUrl)
            tvDescription.text =
                pet.data!!.naturalMonumentCharacter.description

            // 카드 및 캐릭터 등장 애니메이션
            with(clCardFront) {
                visibility = View.VISIBLE
                alpha = 0f
                scaleX = 0.5f
                scaleY = 0.5f
            }
            clCardFront.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .start()
            ivCharacter.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .start()

            btnNext.visibility = View.VISIBLE

            clCardFront.setOnClickListener {
                flipToBack()
            }
            clCardBack.setOnClickListener {
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

    private fun clickNextButton() {
        binding.btnNext.setOnClickListener {
            setResult(RESULT_OK, intent)
            finish()
            overridePendingTransition(0, R.anim.slide_out_left)
        }
    }
}
