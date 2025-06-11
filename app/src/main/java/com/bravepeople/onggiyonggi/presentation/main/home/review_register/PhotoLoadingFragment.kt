package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import android.animation.Animator
import android.animation.ValueAnimator
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
import androidx.appcompat.app.AlertDialog
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
import com.bravepeople.onggiyonggi.extension.home.register.PhotoState
import com.bravepeople.onggiyonggi.extension.home.register.ReceiptState
import kotlinx.coroutines.launch
import timber.log.Timber

class PhotoLoadingFragment : Fragment() {
    private var _binding: FragmentPhotoLoadingBinding? = null
    private val binding: FragmentPhotoLoadingBinding
        get() = requireNotNull(_binding) { "receipt fragment is null" }

    private val reviewRegisterViewModel: ReviewRegisterViewModel by activityViewModels()
    private val args: PhotoLoadingFragmentArgs by navArgs()

    private val handler = Handler(Looper.getMainLooper())
    private val progressRunnable = object : Runnable {
        var progress = 0

        override fun run() {
            _binding?.let { binding ->
                if (progress <= 100) {
                    binding.pbCircular.setProgress(progress, false)
                    binding.tvPercentage.text = "$progress%"
                    progress += 1
                    handler.postDelayed(this, 350)
                }
            }
        }
    }

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
        deleteStore()
        clickCancel()
    }

    private fun setting() {
        val photoType = args.photoType
        val uri = args.uri

        if (photoType == PhotoType.RECEIPT) {
            reviewRegisterViewModel.setReceiptStateToLoading()

            lifecycleScope.launch {
                reviewRegisterViewModel.receiptState.collect { state ->
                    when (state) {
                        is ReceiptState.Success -> {
                            finishProgressBar {
                                showSecondAnimation(
                                    photoType,
                                    "state.photoDto.data.url"
                                )
                            }
                        }

                        is ReceiptState.Loading -> {
                            showFirstAnimation()
                            startProgressBar()
                        }

                        is ReceiptState.Error -> {
                            Timber.e("receipt state error!")
                            showRetryDialog(photoType)
                        }
                    }
                }
            }

            binding.ivReceipt.load(uri)
            reviewRegisterViewModel.receipt(requireContext(), Uri.parse(uri))
        } else {
            binding.pbCircular.visibility=View.GONE
            binding.tvPercentage.visibility=View.GONE
            lifecycleScope.launch {
                reviewRegisterViewModel.photoState.collect { state ->
                    when (state) {
                        is PhotoState.Success -> {
                            reviewRegisterViewModel.savePhotoId(state.photoDto.data.id)
                            showSecondAnimation(
                                photoType,
                                state.photoDto.data.url
                            )
                        }

                        is PhotoState.Loading -> {
                            showFirstAnimation()
                        }

                        is PhotoState.Error -> {
                            Timber.e("photo state error!")
                        }
                    }
                }
            }

            binding.ivReceipt.load(uri)
            reviewRegisterViewModel.photo(requireContext(), Uri.parse(uri))
        }
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

    private fun startProgressBar() {
        val percentText = binding.tvPercentage

        percentText.visibility = View.VISIBLE
        binding.pbCircular.apply {
            visibility = View.VISIBLE
            progress = 0
            max = 100
        }

        handler.post(progressRunnable)
    }

    private fun finishProgressBar(onFinished: () -> Unit) {
        val progressBar = binding.pbCircular
        val percentText = binding.tvPercentage

        val startProgress = progressBar.progress
        val endProgress = 100

        val valueAnimator = ValueAnimator.ofInt(startProgress, endProgress)
        valueAnimator.duration = 1000

        valueAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            progressBar.setProgress(animatedValue, false)
            progressBar.invalidate()
            percentText.text = "$animatedValue%"
            percentText.invalidate()
        }

        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                progressBar.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction {
                        progressBar.visibility = View.GONE
                        progressBar.alpha = 1f
                    }
                    .start()

                percentText.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction {
                        percentText.visibility = View.GONE
                        percentText.alpha = 1f
                        onFinished()
                    }
                    .start()
            }

            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        valueAnimator.start()
    }


    private fun showSecondAnimation(photoType: PhotoType, uri: String) {
        binding.lavLoading.animate()
            .alpha(0f)
            .setDuration(500)
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
                .setDuration(500)
                .start()

            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(p0: Animator) {
                    navigateToNext(photoType, uri)
                }

                override fun onAnimationStart(p0: Animator) {}
                override fun onAnimationCancel(p0: Animator) {}
                override fun onAnimationRepeat(p0: Animator) {}
            })
        }
    }

    private fun navigateToNext(photoType: PhotoType, uri: String) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loadingFragment, true)
            .build()

        when (photoType) {
            PhotoType.RECEIPT -> {
                val action = PhotoLoadingFragmentDirections.actionLoadingToPhoto(PhotoType.FOOD)
                findNavController().navigate(action, navOptions)
            }

            else -> {
                val action = PhotoLoadingFragmentDirections.actionLoadingToWrite(uri)
                findNavController().navigate(action, navOptions)
            }
        }
    }

    private fun showRetryDialog(photoType: PhotoType) {
        binding.lavLoading.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                binding.lavLoading.visibility = View.INVISIBLE
            }
            .start()

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loadingFragment, true)
            .build()

        AlertDialog.Builder(requireContext())
            .setTitle("사진 분석 실패")
            .setMessage("영수증 사진을 다시 촬영해주세요.")
            .setPositiveButton("확인") { _, _ ->
                val action = PhotoLoadingFragmentDirections.actionLoadingToPhoto(photoType)
                findNavController().navigate(action, navOptions)
            }
            .setCancelable(false)
            .show()
    }


    private fun deleteStore() {
        lifecycleScope.launch {
            reviewRegisterViewModel.deleteState.collect { state ->
                when (state) {
                    is DeleteState.Success -> {
                        requireActivity().finish()
                    }

                    is DeleteState.Loading -> {}
                    is DeleteState.Error -> {
                        Timber.e("delete state error!")
                    }
                }
            }
        }

    }

    private fun clickCancel() {
        binding.btnCancel.setOnClickListener {
            if (reviewRegisterViewModel.newStore.value == true) reviewRegisterViewModel.delete()
            else requireActivity().finish()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (reviewRegisterViewModel.newStore.value == true) reviewRegisterViewModel.delete()
            else requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(progressRunnable)
        _binding = null
    }
}