package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.compose.animation.slideIn
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewEnumDto
import com.bravepeople.onggiyonggi.databinding.FragmentWriteReviewBinding
import com.bravepeople.onggiyonggi.extension.character.LevelUpState
import com.bravepeople.onggiyonggi.extension.home.GetEnumState
import com.bravepeople.onggiyonggi.extension.home.GetStoreDetailState
import com.bravepeople.onggiyonggi.extension.home.register.DeleteState
import com.bravepeople.onggiyonggi.extension.home.register.ReceiptState
import com.bravepeople.onggiyonggi.extension.home.register.RegisterReviewState
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

    private val args: WriteReviewFragmentArgs by navArgs()

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
        val uri = args.photoUri
        Timber.d("photoUri: $uri")
        binding.ivFood.load(Uri.parse(uri))

        setStore()
        setReceipt()
        getEnum(uri)
    }

    private fun setStore() {
        lifecycleScope.launch {
            reviewRegisterViewModel.storeDetailState.collect { state ->
                when (state) {
                    is GetStoreDetailState.Success -> {
                        Timber.d("store name: ${state.storeDto.data.name}, store address: ${state.storeDto.data.address}")
                        writeReviewViewModel.saveStoreName(state.storeDto.data.name)
                        with(binding) {
                            tvStoreName.text = state.storeDto.data.name.replace(Regex("<.*?>"), "")
                            tvStoreAddress.text = state.storeDto.data.address
                        }
                    }

                    is GetStoreDetailState.Loading -> {}
                    is GetStoreDetailState.Error -> {
                        Timber.e("get store detail state error!")
                    }
                }
            }
        }

        reviewRegisterViewModel.storeDetail()
    }

    private fun setReceipt() {
        lifecycleScope.launchWhenCreated {
            reviewRegisterViewModel.receiptState.collect { state ->
                if (state is ReceiptState.Success) {
                    val receipt = state.receiptDto.data

                    binding.tvDate.text = receipt.date

                    val receiptInfoAdapter = ReceiptInfoAdapter()
                    binding.rvItems.adapter = receiptInfoAdapter
                    receiptInfoAdapter.getList(receipt.items)
                }
            }
        }
    }

    private fun getEnum(uri: String) {
        val accesstoken = reviewRegisterViewModel.accessToken.value!!

        lifecycleScope.launch {
            writeReviewViewModel.enumState.collect{state->
                when(state){
                    is GetEnumState.Success->{
                        setReview(state.enumDto.data, uri)
                    }
                    is GetEnumState.Loading->{

                    }
                    is GetEnumState.Error->{

                    }
                }
            }
        }
        writeReviewViewModel.getEnum(accesstoken)
    }

    private fun setReview(data: ResponseReviewEnumDto.Data, uri: String) {
        writeReviewAdapter = WriteReviewAdapter(
            complete = { selectedAnswer, text ->
                registerReview(uri, selectedAnswer, text)
            },
            onFocusKeyBoard = { position ->
                pendingFocusPosition = position
            }
        )
        binding.rvWriteReview.adapter = writeReviewAdapter
        writeReviewAdapter.setEnumData(data)
        writeReviewAdapter.setInitialQuestion(requireContext(), data)

        scrollFragment()
    }

   private fun registerReview(uri: String, selectedAnswer: MutableMap<Int, String?>, content: String){
       lifecycleScope.launch {
           writeReviewViewModel.registerReviewState.collect{state->
               when(state){
                   is RegisterReviewState.Success->{
                       levelUp(content, uri)
                   }
                   is RegisterReviewState.Loading->{

                   }
                   is RegisterReviewState.Error->{
                       Timber.e("register review state error!")
                   }
               }
           }
       }

       val accessToken = reviewRegisterViewModel.accessToken.value
       val storeId = reviewRegisterViewModel.storeId.value
       val photoId = reviewRegisterViewModel.photoId.value

       writeReviewViewModel.registerReview(accessToken!!, storeId!!, uri, photoId!!, content, selectedAnswer[0]!!, selectedAnswer[1]!!, selectedAnswer[2]!!, selectedAnswer[3]!! )
   }

    private fun levelUp(content: String, uri:String){
        lifecycleScope.launch {
            writeReviewViewModel.levelUpState.collect { state ->
                when (state) {
                    is LevelUpState.Success -> {
                        val storeName = writeReviewViewModel.storeName.value!!.replace(Regex("<.*?>"), "")
                        val action =
                            WriteReviewFragmentDirections.actionWriteToComplete(uri, storeName, content)
                        findNavController().navigate(action)
                    }

                    is LevelUpState.Loading -> {}
                    is LevelUpState.Error -> {
                        Timber.e("level up state error")
                    }
                }
            }
        }
        val token = reviewRegisterViewModel.accessToken.value ?: return
        writeReviewViewModel.levelUp(token)
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
            if (reviewRegisterViewModel.newStore.value == true)
                showDeleteConfirmDialog()
            else requireActivity().finish()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Timber.e("storeId: ${reviewRegisterViewModel.storeId.value}")
            if (reviewRegisterViewModel.newStore.value == true)
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