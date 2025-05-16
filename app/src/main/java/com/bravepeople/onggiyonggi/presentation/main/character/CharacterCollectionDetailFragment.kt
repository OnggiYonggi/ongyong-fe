package com.bravepeople.onggiyonggi.presentation.main.character

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.bravepeople.onggiyonggi.data.Character
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCollectionDto
import com.bravepeople.onggiyonggi.databinding.FragmentCharacterCollectionDetailBinding
import timber.log.Timber

class CharacterCollectionDetailFragment : Fragment() {
    private var _binding:FragmentCharacterCollectionDetailBinding?= null
    private val binding:FragmentCharacterCollectionDetailBinding
        get() = requireNotNull(_binding) {"character collection detail framgent is null"}

    companion object {
        fun newInstance(character: ResponseCollectionDto.Data.CharacterResponseDto): CharacterCollectionDetailFragment {
            val fragment = CharacterCollectionDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("character", character)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var character: ResponseCollectionDto.Data.CharacterResponseDto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        character = arguments?.getParcelable<ResponseCollectionDto.Data.CharacterResponseDto>("character")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentCharacterCollectionDetailBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        setCards()
    }

    private fun setCards() {
        with(binding) {
            tvName.text = character.name
            ivCharacter.load(character.imageURL)
            tvDescription.text = character.description

            clCardFront.setOnClickListener {
                Timber.d("click front")
                flipToBack()
            }
            clCardBack.setOnClickListener {
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
}