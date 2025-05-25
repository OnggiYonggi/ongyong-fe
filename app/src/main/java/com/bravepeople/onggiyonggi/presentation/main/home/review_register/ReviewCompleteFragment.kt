package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentReviewCompleteBinding
import com.bravepeople.onggiyonggi.extension.character.GetPetState
import com.bravepeople.onggiyonggi.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ReviewCompleteFragment:Fragment() {
    private var _binding:FragmentReviewCompleteBinding?=null
    private val binding:FragmentReviewCompleteBinding
        get()= requireNotNull(_binding){"receipt fragment is null"}

    private val reviewCompleteViewModel: ReviewCompleteViewModel by viewModels()
    private val reviewRegisterViewModel: ReviewRegisterViewModel by activityViewModels()

    private val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        Timber.d("review complete fragment!")

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            // 상단 여백 적용
            binding.root.setPadding(10, statusBarHeight, 10, 0)

            // 하단 버튼 위 여백 적용
            binding.btnEnd.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = navBarHeight + 10.dp
            }

            insets
        }

        /*val lottieView=binding.lavEarth
        lottieView.setAnimation(R.raw.earth)
        lottieView.playAnimation()*/

        setCharacter()
        setReview()

        binding.btnGoCharacter.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("goTo", "character")
            }
            startActivity(intent)
        }

        binding.btnEnd.setOnClickListener{
            endFragment()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            endFragment()
        }
    }

    private fun setCharacter(){
        lifecycleScope.launch {
            reviewCompleteViewModel.getPetState.collect{state->
                when(state){
                    is GetPetState.Success->{
                        val pet = state.getPetDto.data!!.naturalMonumentCharacter
                        val name = pet.name
                        val percentage = "7%"
                        val text=context?.getString(R.string.review_complete_character_likeability, name, percentage)
                        val spannable=SpannableString(text)

                        val start=text!!.indexOf(percentage)
                        val end=start + percentage.length

                        spannable.setSpan(
                            ForegroundColorSpan(Color.RED),
                            start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        binding.tvReviewCharacter.text=spannable
                        binding.ivCharacter.load(pet.imageUrl)
                    }
                    is GetPetState.Loading->{}
                    is GetPetState.Error->{
                        Timber.e("get pet state error!!")
                    }
                }
            }
        }
        val token = reviewRegisterViewModel.accessToken.value?:return
        reviewCompleteViewModel.getPet(token)
        /*val name=reviewCompleteViewModel.name
        val percentage = "7%"
        val text=context?.getString(R.string.review_complete_character_likeability, name, percentage)
        val spannable=SpannableString(text)

        val start=text!!.indexOf(percentage)
        val end=start + percentage.length

        spannable.setSpan(
            ForegroundColorSpan(Color.RED),
            start, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvReviewCharacter.text=spannable*/
    }

    private fun setReview(){
        with(binding){
            val reviewText = arguments?.getString("content")
            val photo = arguments?.getString("uri")
            tvMyReviewStore.text=arguments?.getString("storeName")
            tvMyReviewContent.text=reviewText
            ivMyReviewPhoto.load(Uri.parse(photo))
        }
    }

    private fun endFragment(){
            val beforeActivity = reviewRegisterViewModel.getBeforeActivity()
            val storeId = reviewRegisterViewModel.storeId.value

            if (beforeActivity == null) {
                requireActivity().finish()
            } else {
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    putExtra("openFragment", "store")
                    putExtra("storeId", storeId)
                    putExtra("fromReview", true)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                requireActivity().finish()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}