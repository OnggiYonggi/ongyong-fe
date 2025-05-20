package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import android.animation.Animator
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentPhotoLoadingBinding
import com.bravepeople.onggiyonggi.extension.home.register.DeleteState
import com.bravepeople.onggiyonggi.extension.home.register.ReceiptState
import kotlinx.coroutines.launch
import timber.log.Timber

class PhotoLoadingFragment:Fragment() {
    private var _binding:FragmentPhotoLoadingBinding?=null
    private val binding:FragmentPhotoLoadingBinding
        get()= requireNotNull(_binding){"receipt fragment is null"}

    private val reviewRegisterViewModel: ReviewRegisterViewModel by activityViewModels()
    private val args: PhotoLoadingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setting()
        //loadingEffect()
        deleteStore()
        clickCancel()
    }

    private fun setting(){
        lifecycleScope.launch {
            reviewRegisterViewModel.receiptState.collect{state->
                when(state){
                    is ReceiptState.Success->{
                        showSecondAnimation()
                    }
                    is ReceiptState.Loading->{
                        showFirstAnimation()
                    }
                    is ReceiptState.Error->{
                        Timber.e("receipt state error!")
                    }
                }
            }
        }

        val uri = args.uri
        binding.ivReceipt.load(uri)
        reviewRegisterViewModel.receipt(requireContext(), Uri.parse(uri))
    }

    private fun showFirstAnimation() {
        binding.ivReceipt.load(reviewRegisterViewModel.getReceipt()) {
            transformations()
            listener(
                onSuccess = { _, _ ->
                    binding.ivReceipt.setColorFilter(
                        Color.parseColor("#80000000"),
                        PorterDuff.Mode.SRC_OVER
                    )
                }
            )
        }

        binding.lavLoading.apply {
            alpha = 1f
            visibility = View.VISIBLE
            playAnimation()
        }
    }

    private fun showSecondAnimation() {
        // 1단계: 첫 번째 로딩 애니메이션 사라지게
        binding.lavLoading.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                binding.lavLoading.visibility = View.INVISIBLE
            }
            .start()

        // 2단계: 완료 애니메이션 나타나게
        binding.lavComplete.apply {
            alpha = 0f
            visibility = View.VISIBLE
            playAnimation()

            animate()
                .alpha(1f)
                .setDuration(500)
                .start()

            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(p0: Animator) {
                    navigateToNext()
                }

                override fun onAnimationStart(p0: Animator) {}
                override fun onAnimationCancel(p0: Animator) {}
                override fun onAnimationRepeat(p0: Animator) {}
            })
        }
    }

    private fun navigateToNext() {
        val photoType = args.photoType
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loadingFragment, true)
            .build()

        when (photoType) {
            PhotoType.RECEIPT -> {
                val action = PhotoLoadingFragmentDirections.actionLoadingToPhoto(PhotoType.FOOD)
                findNavController().navigate(action, navOptions)
            }
            else -> {
                val action = PhotoLoadingFragmentDirections.actionLoadingToWrite()
                findNavController().navigate(action, navOptions)
            }
        }
    }


    private fun loadingEffect(){
        binding.ivReceipt.load(reviewRegisterViewModel.getReceipt()) {
            transformations()
            listener(
                onSuccess = { _, _ ->
                    binding.ivReceipt.setColorFilter(
                        Color.parseColor("#80000000"), // 반투명 검정 (어둡게)
                        PorterDuff.Mode.SRC_OVER
                    )
                }
            )
        }

        Handler(Looper.getMainLooper()).postDelayed({

            binding.lavLoading.animate()
                .alpha(0f)
                .setDuration(500) // 서서히 0.5초 동안 사라짐
                .withEndAction {
                    binding.lavLoading.visibility = View.INVISIBLE
                }
                .start()

            binding.lavComplete.apply {
                alpha = 0f
                visibility = View.VISIBLE
                playAnimation()
                animate()
                    .alpha(1f)
                    .setDuration(500) // 0.5초 동안 나타남
                    .start()

                addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(p0: Animator) {
                        val photoType = args.photoType  // enum 값 사용 가능!
                        Timber.d("photoType: ${photoType}")
                        when(photoType){
                            PhotoType.RECEIPT ->{
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.loadingFragment, true)
                                    .build()

                                val action = PhotoLoadingFragmentDirections.actionLoadingToPhoto(
                                    PhotoType.FOOD
                                )
                                findNavController().navigate(action, navOptions)
                            }
                            else-> {
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.loadingFragment, true)
                                    .build()
                                val action = PhotoLoadingFragmentDirections.actionLoadingToWrite()
                                findNavController().navigate(action, navOptions)
                            }
                        }

                    }
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}
                })
            }



        }, 3000) // 3초 딜레이


    }

    private fun deleteStore(){
        lifecycleScope.launch {
            reviewRegisterViewModel.deleteState.collect{state->
                when(state){
                    is DeleteState.Success->{
                        requireActivity().finish()
                    }
                    is DeleteState.Loading->{}
                    is DeleteState.Error->{
                        Timber.e("delete state error!")
                    }
                }
            }
        }

    }

    private fun clickCancel(){
        binding.btnCancel.setOnClickListener {
            if(reviewRegisterViewModel.storeId.value!= null) reviewRegisterViewModel.delete()
            else requireActivity().finish()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            if(reviewRegisterViewModel.storeId.value!= null) reviewRegisterViewModel.delete()
            else requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}