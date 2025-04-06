package com.bravepeople.onggiyonggi.presentation.main.character

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ActivityCharacterGachaBinding
import timber.log.Timber

class CharacterGachaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCharacterGachaBinding

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

            binding.btnBack.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = statusBarHeight
            }

            insets
        }

        startRollingBall()
        clickBackButton()
        clickNextButton()
    }

    private fun startRollingBall() {
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
                            showExplosionEffects()
                        }, 50)
                    }
                })
            }

            animator.start()
        }
    }


    private fun showExplosionEffects() {
        binding.ivBall.visibility = View.GONE

        // 폭발 + 안개 보여주기
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

        // 폭발 끝나면 캐릭터 등장
        binding.lavExplode.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                with(binding) {
                    lavExplode.visibility = View.GONE
                    lavFog.visibility = View.GONE

                    // 카드 설정
                    with(clCardFront) {
                        visibility = View.VISIBLE
                        alpha = 0f
                        scaleX = 0.5f
                        scaleY = 0.5f
                    }

                    // 동시에 애니메이션 시작
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


                    tvName.text = "하늘 다람쥥"
                    btnBack.visibility = View.VISIBLE
                    btnNext.visibility=View.VISIBLE

                    clCardFront.setOnClickListener {
                        tvDescription.text = "환경을 지키는 귀여운 다람쥐 \uD83C\uDF31"
                        flipToBack()
                    }
                    clCardBack.setOnClickListener {
                        flipToFront()
                    }
                }
            }
        })
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

    private fun clickBackButton() {
        binding.btnBack.setOnClickListener{
            finish()
        }
    }

    private fun clickNextButton(){
        binding.btnNext.setOnClickListener{
            val intent = Intent()
            intent.putExtra("character_name", "하늘 다람쥥")
            intent.putExtra("character_description", "환경을 지키는 귀여운 다람쥐 🌳")
            intent.putExtra("character_image", R.drawable.ic_flying_squirrel)

            setResult(RESULT_OK, intent)
            finish()
            overridePendingTransition(0, R.anim.slide_out_left)
        }
    }
}
