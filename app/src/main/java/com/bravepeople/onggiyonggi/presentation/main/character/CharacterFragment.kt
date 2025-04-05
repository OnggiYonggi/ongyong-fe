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
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import coil.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentCharacterBinding
import timber.log.Timber

class CharacterFragment : Fragment() {
    private var _binding: FragmentCharacterBinding? = null
    private val binding: FragmentCharacterBinding
        get() = requireNotNull(_binding) { "receipt fragment is null" }

    private val REQUEST_CODE_GACHA = 1001

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
        setting()
    }

    private fun setting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val marginTop = statusBarHeight + 20

            binding.btnCollection.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = marginTop
            }

            insets
        }

        binding.btnGacha.setOnClickListener {
            startGachaAnimation()
        }
    }

    private fun startGachaAnimation() {
        val  eggViews = listOf(
            binding.ivEggBlue,
            binding.ivEggGreen,
            binding.ivEggOrange,
            binding.ivEggYellow,
            binding.ivEggPurple
        )
        val eggAnimations = eggViews.map { egg ->
            // X축 설정
            val rangeX = (8..20).random().toFloat()
            val delay = (0..150).random().toLong()
            val duration = (80..130).random().toLong()
            val repeatCount = (3..6).random()

            val shakeX = ObjectAnimator.ofFloat(
                egg,
                "translationX",
                egg.translationX - rangeX,
                egg.translationX + rangeX
            ).apply {
                this.duration = duration
                this.startDelay = delay
                this.repeatCount = repeatCount
                this.repeatMode = ValueAnimator.REVERSE
            }

            // Y축 설정
            val rangeY = (5..15).random().toFloat()
            val shakeY = ObjectAnimator.ofFloat(
                egg,
                "translationY",
                egg.translationY - rangeY,
                egg.translationY + rangeY
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

        val latest = eggAnimations.maxByOrNull {
            it.childAnimations.maxOf { anim ->
                (anim as ObjectAnimator).startDelay + anim.duration * anim.repeatCount
            }
        }

        latest?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Timber.d("addlistener end")
                val intent = Intent(requireContext(), CharacterGachaActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_GACHA)
            }
        })

        AnimatorSet().apply {
            playTogether(eggAnimations)
            start()
        }
    }

    private fun updateCharacterData(image:Int?, name: String?, description: String?) {
        Timber.d("update Character data")
        with(binding){
            ivCharacter.load(image)
            tvName.text=name
            tvDescription.text = description

            ivGachaMachine.visibility=View.INVISIBLE
            ivEggPurple.visibility=View.INVISIBLE
            ivEggGreen.visibility=View.INVISIBLE
            ivEggOrange.visibility=View.INVISIBLE
            ivEggBlue.visibility=View.INVISIBLE
            ivEggYellow.visibility=View.INVISIBLE
            btnGacha.visibility=View.INVISIBLE

            clCardFront.visibility=View.VISIBLE
            btnCollection.visibility=View.VISIBLE

            clCardFront.setOnClickListener{
                flipToBack()
            }
            clCardBack.setOnClickListener{
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GACHA && data != null) {
            val characterName = data.getStringExtra("character_name")
            val characterDescription = data.getStringExtra("character_description")
            val characterImage = data.getIntExtra("character_image", R.drawable.ic_flying_squirrel)

            updateCharacterData(characterImage, characterName, characterDescription)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}