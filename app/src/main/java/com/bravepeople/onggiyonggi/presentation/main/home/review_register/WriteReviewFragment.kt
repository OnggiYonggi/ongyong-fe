package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil3.load
import com.bravepeople.onggiyonggi.data.StoreAndDateAndPhoto
import com.bravepeople.onggiyonggi.databinding.FragmentWriteReviewBinding
import com.bravepeople.onggiyonggi.extension.character.LevelUpState
import com.bravepeople.onggiyonggi.extension.home.register.DeleteState
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_adapter.ReceiptInfoAdapter
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_adapter.WriteReviewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class WriteReviewFragment : Fragment() {
    private var _binding: FragmentWriteReviewBinding? = null
    private val binding: FragmentWriteReviewBinding
        get() = requireNotNull(_binding) { "receipt fragment is null" }

    private val reviewRegisterViewModel: ReviewRegisterViewModel by activityViewModels()
    private val writeReviewViewModel: WriteReviewViewModel by viewModels()
    private lateinit var writeReviewAdapter: WriteReviewAdapter
    private var pendingFocusPosition: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.nsvWriteReview) { view, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val systemBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom

            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                if (imeVisible) imeHeight else systemBarHeight
            )

            /*if (!imeVisible) {
                binding.rvWriteReview.findFocus()?.clearFocus()
                pendingFocusPosition = null // 키보드 내려갔으면 pending 초기화
            } else {
                // 키보드 올라오면 이제 스크롤
                pendingFocusPosition?.let { position ->
                    binding.rvWriteReview.postDelayed({
                        val layoutManager = binding.rvWriteReview.layoutManager as? LinearLayoutManager
                        val view = layoutManager?.findViewByPosition(position)

                        if (view != null) {
                            val extraPadding = 200 // px
                            binding.nsvWriteReview.smoothScrollTo(
                                0,
                                view.top + binding.rvWriteReview.top - extraPadding
                            )
                        }
                    }, 80) // 약간만 딜레이 주는게 좋아 (키보드 올라올 때까지)
                    pendingFocusPosition = null // 한번 하고 초기화
                }
            }*/
            if (imeVisible) {
                // 키보드가 올라왔을 때
                val focusedView = view.findFocus()

                focusedView?.let { target ->
                    view.post {
                        val location = IntArray(2)
                        target.getLocationOnScreen(location)
                        val targetBottom = location[1] + target.height

                        val screenHeight = view.resources.displayMetrics.heightPixels
                        val keyboardTop = screenHeight - imeHeight

                        val scrollAmount = targetBottom - keyboardTop

                        if (scrollAmount > 0) {
                            binding.nsvWriteReview.smoothScrollBy(0, scrollAmount + 100) // 약간 더 여유
                        }
                    }
                }
            }

            insets
        }


        setInfos()
        retakePhoto()

        deleteStore()
        clickCancel()
    }

    private fun setInfos() {
        val store = reviewRegisterViewModel.getStore()
        val receipt = reviewRegisterViewModel.getReceiptFoods()
        with(binding) {
            ivFood.load(store.image)
            tvStoreName.text = store.name
            tvStoreAddress.text = store.address

            tvDate.text = receipt[0].date
        }

        val receiptInfoAdapter = ReceiptInfoAdapter()
        binding.rvItems.adapter = receiptInfoAdapter
        receiptInfoAdapter.getList(receipt)

        writeReviewAdapter = WriteReviewAdapter(
            complete = { text ->
                lifecycleScope.launch {
                    writeReviewViewModel.levelUpState.collect { state ->
                        when (state) {
                            is LevelUpState.Success -> {
                                val action =
                                    WriteReviewFragmentDirections.actionWriteToComplete(text)
                                findNavController().navigate(action)
                            }

                            is LevelUpState.Loading -> {}
                            is LevelUpState.Error -> {
                                Timber.e("level up state error")
                            }
                        }
                    }
                }
                val token = reviewRegisterViewModel.accessToken.value ?: return@WriteReviewAdapter
                writeReviewViewModel.levelUp(token)
            },
            onFocusKeyBoard = { position ->
                pendingFocusPosition = position
                /* binding.rvWriteReview.post {
                     val layoutManager = binding.rvWriteReview.layoutManager as? LinearLayoutManager
                     val view = layoutManager?.findViewByPosition(position)

                     if (view != null) {
                         // 키보드 때문에 가려지는 걸 막기 위해 약간 더 여유있게 내려줌
                         val extraPadding = 200 // ← 이 값 조정 가능 (px단위)

                         binding.nsvWriteReview.smoothScrollTo(
                             0,
                             view.top + binding.rvWriteReview.top - extraPadding
                         )
                     }
                 }*/
            }
        )
        binding.rvWriteReview.adapter = writeReviewAdapter
        writeReviewAdapter.getList(reviewRegisterViewModel.getSelectQuestion(requireContext()))
        writeReviewAdapter.setOnScrollRequestListener { position ->
            val targetView = binding.rvWriteReview.layoutManager?.findViewByPosition(position)
            if (targetView != null) {
                binding.nsvWriteReview.smoothScrollTo(
                    0,
                    targetView.top + binding.rvWriteReview.top
                )
            }
        }

        scrollFragment()
    }

    private fun scrollFragment() {
        writeReviewAdapter.setOnScrollRequestListener { position ->
            binding.rvWriteReview.post {
                val layoutManager = binding.rvWriteReview.layoutManager as? LinearLayoutManager
                val targetView = layoutManager?.findViewByPosition(position)

                if (targetView != null) {
                    // NestedScrollView를 타겟 뷰 위치로 스크롤
                    binding.nsvWriteReview.smoothScrollTo(
                        0,
                        targetView.top + binding.rvWriteReview.top
                    )
                } else {
                    // 타겟 뷰가 attach 안되어 있으면 강제로 attach
                    layoutManager?.scrollToPosition(position)
                    binding.rvWriteReview.post {
                        val view = layoutManager?.findViewByPosition(position)
                        view?.let {
                            binding.nsvWriteReview.smoothScrollTo(
                                0,
                                it.top + binding.rvWriteReview.top
                            )
                        }
                    }
                }
            }
        }

        /*writeReviewAdapter.setOnScrollLockRequestListener {
            val layoutManager = binding.rvWrite.layoutManager as LinearLayoutManager
            val firstView = layoutManager.getChildAt(0)
            val topPosition = layoutManager.findFirstVisibleItemPosition()
            val offset = firstView?.top ?: 0

            binding.rvWrite.post {
                layoutManager.scrollToPositionWithOffset(topPosition, offset)

                // NestedScrollView도 같이 이동
                binding.nestedScrollView.post {
                    binding.nestedScrollView.smoothScrollTo(
                        0,
                        (binding.rvWrite.top + (firstView?.top ?: 0))
                    )
                }
            }
        }*/


    }

    private fun retakePhoto() {
        binding.btnRephoto.setOnClickListener {
            val action = WriteReviewFragmentDirections.actionWriteToPhoto(PhotoType.FOOD)
            findNavController().navigate(action)
        }
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
            Timber.e("storeId: ${reviewRegisterViewModel.storeId.value}")
            if (reviewRegisterViewModel.storeId.value != null)
                showDeleteConfirmDialog()
            else requireActivity().finish()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Timber.e("storeId: ${reviewRegisterViewModel.storeId.value}")
            if (reviewRegisterViewModel.storeId.value != null)
                showDeleteConfirmDialog()
            else requireActivity().finish()
        }
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("리뷰를 삭제할까요?")
            .setMessage("작성 중인 리뷰는 저장되지 않고 삭제됩니다.")
            .setPositiveButton("삭제") { _, _ ->
                reviewRegisterViewModel.delete()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}