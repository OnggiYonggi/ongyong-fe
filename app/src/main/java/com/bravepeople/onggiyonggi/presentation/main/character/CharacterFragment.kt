package com.bravepeople.onggiyonggi.presentation.main.character

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import coil3.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentCharacterBinding
import timber.log.Timber

class CharacterFragment : Fragment() {
    private var _binding: FragmentCharacterBinding? = null
    private val binding: FragmentCharacterBinding
        get() = requireNotNull(_binding) { "receipt fragment is null" }

    private var affectionLevel: Int = 0
    private val REQUEST_CODE_GACHA = 1001
    private val REQUEST_CODE_MAX = 1002

    private lateinit var cachedAnimatorSet: AnimatorSet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharacterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       setupSkeletonUI()
        setting()
    }

   private fun setupSkeletonUI() {
        with(binding) {
            // 처음에는 가챠 머신만
            ivGachaMachine.visibility = View.VISIBLE
            btnGacha.visibility = View.GONE
            ivEggBlue.visibility = View.GONE
            ivEggGreen.visibility = View.GONE
            ivEggOrange.visibility = View.GONE
            ivEggYellow.visibility = View.GONE
            ivEggPurple.visibility = View.GONE
            btnCollection.visibility = View.GONE
            tvName.visibility = View.GONE
            clCardFront.visibility = View.GONE
            clCardBack.visibility = View.GONE
            tvAffectionTitle.visibility = View.GONE
            tvAffectionPercent.visibility = View.GONE
            pbAffection.visibility = View.GONE
            btnIncrease.visibility = View.GONE
        }

        // Fragment가 화면에 올라오고 나서 (1프레임 후)
        binding.root.post {
            with(binding) {
                btnGacha.visibility = View.VISIBLE
                ivEggBlue.visibility = View.VISIBLE
                ivEggGreen.visibility = View.VISIBLE
                ivEggOrange.visibility = View.VISIBLE
                ivEggYellow.visibility = View.VISIBLE
                ivEggPurple.visibility = View.VISIBLE
                btnCollection.visibility = View.VISIBLE
                tvName.visibility = View.VISIBLE
            }
        }
    }


    private fun setting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val marginTop = statusBarHeight + 50

            binding.btnCollection.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = marginTop
            }

            insets
        }
        binding.tvAffectionPercent.text = getString(R.string.character_affection_percent, 0)

        cachedAnimatorSet=createAnimatorSet()
        startGachaAnimation()

        increaseAffection()
        clickCollection()
    }

    private fun createAnimatorSet(): AnimatorSet {
        val eggViews = listOf(
            binding.ivEggBlue,
            binding.ivEggGreen,
            binding.ivEggOrange,
            binding.ivEggYellow,
            binding.ivEggPurple
        )

        val eggAnimations = eggViews.map { egg ->
            val rangeX = (8..20).random().toFloat()
            val rangeY = (5..15).random().toFloat()

            val delay = (0..150).random().toLong()
            val duration = (80..130).random().toLong()
            val repeatCount = (3..6).random()

            val shakeX = ObjectAnimator.ofFloat(
                egg, "translationX",
                egg.translationX - rangeX, egg.translationX + rangeX
            ).apply {
                this.duration = duration
                this.startDelay = delay
                this.repeatCount = repeatCount
                this.repeatMode = ValueAnimator.REVERSE
            }

            val shakeY = ObjectAnimator.ofFloat(
                egg, "translationY",
                egg.translationY - rangeY, egg.translationY + rangeY
            ).apply {
                this.duration = duration
                this.startDelay = delay
                this.repeatCount = repeatCount
                this.repeatMode = ValueAnimator.REVERSE
            }

            AnimatorSet().apply {
                playTogether(shakeX, shakeY)
            }
        }

        val animatorSet = AnimatorSet().apply {
            playTogether(eggAnimations)
        }

        // 모든 애니메이션이 끝난 후
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Timber.d("addlistener end")
                val intent = Intent(requireContext(), CharacterGachaActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_GACHA)
            }
        })

        return animatorSet
    }


    private fun startGachaAnimation() {
        binding.btnGacha.setOnClickListener {
            cachedAnimatorSet.start()
        }
    }

    private fun updateCharacterData(image: Int?, name: String?, description: String?) {
        Timber.d("update Character data")
        with(binding) {
            ivCharacter.load(image)
            tvName.text = name
            tvDescription.text = description

            isGacha(false)

            clCardFront.setOnClickListener {
                flipToBack()
            }
            clCardBack.setOnClickListener {
                flipToFront()
            }
        }
    }

    private fun isGacha(isGacha: Boolean) {
        with(binding) {
            if (isGacha) {
                ivGachaMachine.visibility = View.VISIBLE
                ivEggPurple.visibility = View.VISIBLE
                ivEggGreen.visibility = View.VISIBLE
                ivEggOrange.visibility = View.VISIBLE
                ivEggBlue.visibility = View.VISIBLE
                ivEggYellow.visibility = View.VISIBLE
                btnGacha.visibility = View.VISIBLE

                tvName.visibility = View.INVISIBLE
                clCardFront.visibility = View.INVISIBLE
                btnCollection.visibility = View.INVISIBLE
                tvAffectionTitle.visibility = View.INVISIBLE
                tvAffectionPercent.visibility = View.INVISIBLE
                pbAffection.visibility = View.INVISIBLE
                btnIncrease.visibility = View.INVISIBLE
            } else {
                ivGachaMachine.visibility = View.INVISIBLE
                ivEggPurple.visibility = View.INVISIBLE
                ivEggGreen.visibility = View.INVISIBLE
                ivEggOrange.visibility = View.INVISIBLE
                ivEggBlue.visibility = View.INVISIBLE
                ivEggYellow.visibility = View.INVISIBLE
                btnGacha.visibility = View.INVISIBLE

                tvName.visibility = View.VISIBLE
                clCardFront.visibility = View.VISIBLE
                btnCollection.visibility = View.VISIBLE
                tvAffectionTitle.visibility = View.VISIBLE
                tvAffectionPercent.visibility = View.VISIBLE
                pbAffection.visibility = View.VISIBLE
                btnIncrease.visibility = View.VISIBLE
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

    private fun increaseAffection() {
        binding.btnIncrease.setOnClickListener {
            if (affectionLevel < 100) {
                affectionLevel += 20
                if (affectionLevel > 100) affectionLevel = 100
                if (affectionLevel >= 100) {
                    setAffectionProgressWithAnimation(affectionLevel) {
                        val intent = Intent(requireContext(), CharacterMaxActivity::class.java)
                        startActivityForResult(intent, REQUEST_CODE_MAX)
                    }
                } else {
                    setAffectionProgressWithAnimation(affectionLevel)
                }
                setAffectionProgressWithAnimation(affectionLevel)
                binding.tvAffectionPercent.text =
                    getString(R.string.character_affection_percent, affectionLevel)
            }
        }
    }

    private fun setAffectionProgressWithAnimation(
        targetProgress: Int,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        val progressAnimator = ObjectAnimator.ofInt(
            binding.pbAffection,
            "progress",
            binding.pbAffection.progress,
            targetProgress
        ).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
        }

        // 텍스트 애니메이션 (호감도 % 증가)
        val textAnimator = ValueAnimator.ofInt(binding.pbAffection.progress, targetProgress).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                val currentValue = animator.animatedValue as Int
                binding.tvAffectionPercent.text =
                    getString(R.string.character_affection_percent, currentValue)
            }
        }

        AnimatorSet().apply {
            playTogether(progressAnimator, textAnimator)
            onAnimationEnd?.let {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        it() // 애니 끝나면 콜백 실행
                    }
                })
            }
            start()
        }
    }

    private fun clickCollection() {
        binding.btnCollection.setOnClickListener {
            startActivity(Intent(requireActivity(), CharacterCollectionActivity::class.java))
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.stay_still
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GACHA && data != null) {
            val characterName = data.getStringExtra("character_name")
            val characterDescription = data.getStringExtra("character_description")
            val characterImage =
                data.getIntExtra("character_image", R.drawable.character_flying_squirrel)

            updateCharacterData(characterImage, characterName, characterDescription)
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_MAX) {
            isGacha(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}