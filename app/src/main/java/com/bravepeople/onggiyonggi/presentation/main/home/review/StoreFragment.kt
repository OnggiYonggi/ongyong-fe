package com.bravepeople.onggiyonggi.presentation.main.home.review

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.Search
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseReviewDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseStoreDetailDto
import com.bravepeople.onggiyonggi.data.toStore
import com.bravepeople.onggiyonggi.databinding.FragmentReviewBinding
import com.bravepeople.onggiyonggi.domain.model.StoreRank
import com.bravepeople.onggiyonggi.extension.GetStoreTimeState
import com.bravepeople.onggiyonggi.extension.home.GetReviewState
import com.bravepeople.onggiyonggi.extension.home.GetStoreDetailState
import com.bravepeople.onggiyonggi.presentation.main.home.store_register.StoreRegisterActivity
import com.bravepeople.onggiyonggi.presentation.main.home.review.review_detail.ReviewDetailActivity
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.ReviewRegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class StoreFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding: FragmentReviewBinding
        get() = requireNotNull(_binding) { "review fragment is null" }

    private val storeViewModel: StoreViewModel by activityViewModels()
    private lateinit var reviewAdapter: StoreAdapter
    private var isExpanded = false

    private var initialY = 0f
    private var downY = 0f
    private val minHeightToShow = 300f

    companion object {
        fun newInstance(storeId: Int, token:String): StoreFragment {
            val fragment = StoreFragment()
            val args = Bundle().apply {
                putInt("storeId", storeId)
                putString("accessToken", token)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        val storeId = arguments?.getInt("storeId")
        val token=arguments?.getString("accessToken")
        setupBottomSheetBehavior()
        getStoreTime()
        if (token != null && storeId!=null) {
                setupUI(token, storeId)
        }
        Timber.d("Fragment height: ${binding.root.height}")
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBottomSheetBehavior() {
        val parent = view?.parent as? View
        val velocityTracker = VelocityTracker.obtain()

        binding.root.post {
            val visibleHeight = binding.vLine.bottom + binding.vLine.marginBottomPx()
            parent?.layoutParams?.height = visibleHeight
            parent?.requestLayout()
        }

        binding.root.setOnTouchListener { v, event ->
            val parentView = v.parent as? View ?: return@setOnTouchListener false

            velocityTracker.addMovement(event)

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    velocityTracker.clear()
                    velocityTracker.addMovement(event)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    velocityTracker.computeCurrentVelocity(1000) // px/sec
                    val velocityY = velocityTracker.yVelocity

                    val screenHeight = resources.displayMetrics.heightPixels
                    val statusBarHeight = getStatusBarHeight()
                    val fullHeight = screenHeight - statusBarHeight - 30.dpToPx()
                    val minHeight = visibleContentMinHeight()

                    val thresholdVelocity = 200f // px/sec

                    if (velocityY < -thresholdVelocity) {
                        // 위로 스와이프 → 확장
                        animateHeight(parentView, fullHeight)
                        isExpanded = true
                    } else if (velocityY > thresholdVelocity) {
                        // 아래로 스와이프 → 축소
                        animateHeight(parentView, minHeight)
                        isExpanded = false
                    }
                }
            }
            true
        }
    }

    private fun View.marginBottomPx(): Int {
        val params = layoutParams as? ViewGroup.MarginLayoutParams
        return params?.bottomMargin ?: 0
    }

    private fun visibleContentMinHeight(): Int {
        return binding.vLine.bottom + binding.vLine.marginBottomPx()
    }

    private fun animateHeight(view: View, targetHeight: Int, duration: Long = 300) {
        val initialHeight = view.height
        val animator = ValueAnimator.ofInt(initialHeight, targetHeight)
        animator.duration = duration
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            view.layoutParams.height = animatedValue
            view.requestLayout()
        }
        animator.start()
    }

    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    // 추후 가게 추가 api까지 끝나면 서버에서 time도 받아오므로 없어질 메서드.
    private fun getStoreTime(){
        val dayMap = mapOf(
            "Monday" to "월요일",
            "Tuesday" to "화요일",
            "Wednesday" to "수요일",
            "Thursday" to "목요일",
            "Friday" to "금요일",
            "Saturday" to "토요일",
            "Sunday" to "일요일"
        )

        lifecycleScope.launch {
            storeViewModel.getStoreTimeState.collect{getStoreTimeState->
                when(getStoreTimeState){
                    is GetStoreTimeState.Success->{
                        val time = getStoreTimeState.searchDto.places
                            .firstOrNull { it.regularOpeningHours != null }
                            ?.regularOpeningHours?.weekdayDescriptions

                        val translatedTime = time?.map { line ->
                            var newLine = line
                            dayMap.forEach { (eng, kor) ->
                                newLine = newLine.replace(eng, kor)
                            }
                            newLine
                        }

                        binding.tvStoreHours.text = translatedTime?.joinToString("\n")
                    }
                    is GetStoreTimeState.Error->{
                        Timber.e("error: ${getStoreTimeState.message}")
                    }
                    is GetStoreTimeState.Loading->{
                        Timber.d("get store time state loading")
                    }
                }
            }
        }
    }

    private fun setupUI(accessToken:String, id:Int) {
        lifecycleScope.launch {
            storeViewModel.getDetailState.collect{state->
                when(state){
                    is GetStoreDetailState.Success->{
                        storeViewModel.searchStoreTime(state.storeDto.data.name)

                        val store = state.storeDto.data
                        val isBan = StoreRank.from(store.storeRank) == StoreRank.BAN

                        with(binding) {
                            clBan.visibility = if (isBan) View.VISIBLE else View.GONE
                            clReviews.visibility = if (isBan) View.GONE else View.VISIBLE

                            tvStoreName.text=store.name
                            tvStoreAddress.text=store.address

                            if (isBan) {
                                clBan.minHeight = (resources.displayMetrics.heightPixels * 0.6).toInt()
                                btnRegister.setOnClickListener {
                                    clickNewRegister()
                                }
                            } else {
                                setupRecyclerView(accessToken, id)
                                btnAdd.setOnClickListener{
                                    writeReview()
                                }
                            }
                        }
                    }
                    is GetStoreDetailState.Loading->{}
                    is GetStoreDetailState.Error->{
                        Timber.e("get store detail state error!!")
                    }
                }
            }
        }

        storeViewModel.getDetail(accessToken,id)
       /* if (data == null) return
        val isBan = data.isBan

        with(binding) {
            clBan.visibility = if (isBan) View.VISIBLE else View.GONE
            clReviews.visibility = if (isBan) View.GONE else View.VISIBLE

            if (isBan) {
                clBan.minHeight = (resources.displayMetrics.heightPixels * 0.6).toInt()
                btnRegister.setOnClickListener {
                    clickNewRegister()
                }
            } else {
                setupRecyclerView(data)
                btnAdd.setOnClickListener{
                    writeReview()
                }
            }
        }

        storeViewModel.searchStoreTime(data.name)*/
    }


    private fun setupRecyclerView(accessToken: String, id: Int) {
        lifecycleScope.launch {
            storeViewModel.getReviewState.collect{state->
                when(state){
                    is GetReviewState.Success->{
                        reviewAdapter = StoreAdapter(
                            clickReview = {review -> clickReview(review)},
                        )
                        binding.rvReviews.adapter = reviewAdapter
                        binding.rvReviews.setOnTouchListener { _, event ->
                            !isExpanded  // isExpanded가 false면 true를 반환해서 터치 막음
                        }

                        state.reviewDto.data.contents?.let { reviewAdapter.setReviewList(it) }
                    }
                    is GetReviewState.Loading->{}
                    is GetReviewState.Error->{
                        Timber.e("get review state error!!")
                    }
                }
            }
        }

        storeViewModel.getReviews(accessToken, id)
    }

    private fun clickNewRegister() {
        Timber.d("clicknemregister")
        val intent = Intent(requireContext(), StoreRegisterActivity::class.java)
        intent.putExtra("type", "new")
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
    }

    private fun clickReview(review: ResponseReviewDto.Data.ReviewContent) {
        val intent = Intent(requireContext(), ReviewDetailActivity::class.java)
        intent.putExtra("review", review)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
    }

    private fun writeReview(){
        Timber.d("writeReview")
        val intent = Intent(requireContext(), ReviewRegisterActivity::class.java)
        intent.putExtra("type", "registerActivity")
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}