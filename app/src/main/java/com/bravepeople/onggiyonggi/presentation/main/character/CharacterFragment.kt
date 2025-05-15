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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.response_dto.ResponseGetPetDto
import com.bravepeople.onggiyonggi.databinding.FragmentCharacterBinding
import com.bravepeople.onggiyonggi.extension.character.GetPetState
import com.bravepeople.onggiyonggi.extension.character.LevelUpState
import com.bravepeople.onggiyonggi.extension.character.RandomPetState
import com.bravepeople.onggiyonggi.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class CharacterFragment : Fragment() {
    private var _binding: FragmentCharacterBinding? = null
    private val binding: FragmentCharacterBinding
        get() = requireNotNull(_binding) { "receipt fragment is null" }

    private val mainViewModel: MainViewModel by activityViewModels()
    private val characterViewModel: CharacterViewModel by viewModels()

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

        setUpUI()
        setting()
    }

    private fun setting() {
        clickCollection()

        lifecycleScope.launch {
            mainViewModel.accessToken.observe(viewLifecycleOwner) { token ->
                characterViewModel.saveToken(token)
                characterViewModel.getPet()
                cachedAnimatorSet = createAnimatorSet(token)
                startGachaAnimation()
                increaseAffection(token)
            }
        }
    }

    private fun setUpUI() {
        lifecycleScope.launch {
            characterViewModel.getPetState.collect { state ->
                when (state) {
                    is GetPetState.Success -> {
                        Timber.d("setupui - get pet state success~")
                        showCharacter(state.getPetDto.data!!)
                    }
                    is GetPetState.Loading -> {}
                    is GetPetState.Error -> {
                        if (state.message == "PET404") {
                            Timber.d("pet 없음")
                            setupSkeletonUI()
                        }
                    }
                }
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val marginTop = statusBarHeight + 50

            binding.btnCollection.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = marginTop
            }

            insets
        }
        binding.tvAffectionPercent.text = getString(R.string.character_affection_percent, 0)
    }

    private fun showCharacter(pet: ResponseGetPetDto.Data) {
        with(binding) {
            Timber.d("imageUrl: ${pet.naturalMonumentCharacter.imageUrl}")
            binding.ivCharacter.load(pet.naturalMonumentCharacter.imageUrl) {
                listener(
                    onSuccess = { _, _ -> Timber.d("이미지 로드 성공") },
                    onError = { _, throwable -> Timber.e("이미지 로드 실패: ${throwable.toString()}") }
                )
            }

            tvName.text = pet.naturalMonumentCharacter.name
            tvDescription.text = pet.naturalMonumentCharacter.description
            affectionLevel=pet.affinity.toInt()
            setAffectionProgressWithAnimation(affectionLevel)
            binding.tvAffectionPercent.text =
                getString(R.string.character_affection_percent, affectionLevel)

            isGacha(false)

            clCardFront.setOnClickListener {
                flipToBack()
            }
            clCardBack.setOnClickListener {
                flipToFront()
            }
        }
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

    private fun createAnimatorSet(token: String): AnimatorSet {
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
                intent.putExtra("accessToken", token)
                startActivityForResult(intent, REQUEST_CODE_GACHA)
            }
        })

        return animatorSet
    }


    private fun startGachaAnimation() {
        lifecycleScope.launch {
            characterViewModel.randomPetState.collect { state ->
                when (state) {
                    is RandomPetState.Success -> {
                        cachedAnimatorSet.start()
                    }

                    is RandomPetState.Loading -> {}
                    is RandomPetState.Error -> {
                        Timber.e("random pet state error!")
                    }
                }
            }
        }

        binding.btnGacha.setOnClickListener {
            characterViewModel.randomPet()
        }
    }

    private fun updateCharacterData() {
        Timber.d("update Character data")
        lifecycleScope.launch {
            characterViewModel.getPetState.collect { state ->
                when (state) {
                    is GetPetState.Success -> {
                        showCharacter(state.getPetDto.data!!)
                    }
                    is GetPetState.Loading -> {}
                    is GetPetState.Error -> {
                        Timber.e("update character data get pet state error!")
                    }
                }
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

    private fun increaseAffection(token: String) {
        observeLevelUpState(token)
        setupButton()
    }

    private fun observeLevelUpState(token: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                characterViewModel.levelUpState.collect { state ->
                    when (state) {
                        is LevelUpState.Loading -> {
                        }
                        is LevelUpState.Success -> {
                            if (affectionLevel < 100) {
                                Timber.d("affinity: ${state.randomPetDto.data!!.affinity.toInt()}")
                                affectionLevel = state.randomPetDto.data!!.affinity.toInt()
                                if (affectionLevel > 100) affectionLevel = 100

                                setAffectionProgressWithAnimation(affectionLevel) {
                                    if (affectionLevel >= 100) {
                                        val intent = Intent(requireContext(), CharacterMaxActivity::class.java)
                                        intent.putExtra("character", state.randomPetDto.data)
                                        intent.putExtra("accessToken", token)
                                        startActivityForResult(intent, REQUEST_CODE_MAX)
                                    }
                                }
                                binding.tvAffectionPercent.text =
                                    getString(R.string.character_affection_percent, affectionLevel)
                            }
                        }
                        is LevelUpState.Error -> {
                            // 에러 UI 처리 (필요하면)
                        }
                    }
                }
            }

        }
    }

    private fun setupButton() {
        binding.btnIncrease.setOnClickListener {
            characterViewModel.levelUp()  // 버튼 누를 때만 호출
        }
    }


    /*  private fun increaseAffection() {
          lifecycleScope.launch {
              characterViewModel.levelUpState.collect{state->
                  when(state){
                      is LevelUpState.Success->{

                      }
                      is LevelUpState.Loading->{}
                      is LevelUpState.Error->{}
                  }
              }
          }

          binding.btnIncrease.setOnClickListener {
              characterViewModel.levelUp()
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
      }*/

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

    fun refreshData() {
        setting()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_GACHA && data != null) {
            characterViewModel.getPet()
            updateCharacterData()
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_MAX) {
            isGacha(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}