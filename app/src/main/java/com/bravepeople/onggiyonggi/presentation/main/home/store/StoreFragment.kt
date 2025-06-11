package com.bravepeople.onggiyonggi.presentation.main.home.store

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentStoreBinding
import com.bravepeople.onggiyonggi.domain.model.StoreRank
import com.bravepeople.onggiyonggi.extension.GetStoreTimeState
import com.bravepeople.onggiyonggi.extension.home.GetEnumState
import com.bravepeople.onggiyonggi.extension.home.GetReviewState
import com.bravepeople.onggiyonggi.extension.home.GetStoreDetailState
import com.bravepeople.onggiyonggi.presentation.MainViewModel
import com.bravepeople.onggiyonggi.presentation.main.home.store_register.StoreRegisterActivity
import com.bravepeople.onggiyonggi.presentation.main.home.store.review_detail.ReviewDetailActivity
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.ReviewRegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class StoreFragment : Fragment() {
    private var _binding: FragmentStoreBinding? = null
    private val binding: FragmentStoreBinding
        get() = requireNotNull(_binding) { "review fragment is null" }

    private val mainViewModel: MainViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by viewModels()
    private lateinit var reviewAdapter: StoreAdapter
    private var isExpanded = false

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
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        storeViewModel.clearReviewState()
        storeViewModel.clearStoreDetailState()

        val storeId = arguments?.getInt("storeId")
        val token=arguments?.getString("accessToken")

        setupBottomSheetBehavior()
        //getStoreTime()
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
        Timber.d("store id in set up ui: $id")
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

                            tvStoreName.text=store.name.replace(Regex("<.*?>"), "")
                            tvStoreAddress.text=store.address
                            tvStoreHours.text=store.businessHours

                            stopSkeletonForStoreInfo()

                            if (isBan) {
                                clBan.minHeight = (resources.displayMetrics.heightPixels * 0.6).toInt()
                                btnRegister.setOnClickListener {
                                    clickFirstReview(accessToken, id)
                                }
                            } else {
                                setupRecyclerView(accessToken, id)
                                btnAdd.setOnClickListener{
                                    writeReview(accessToken, id)
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
    }

    private fun stopSkeletonForStoreInfo() = with(binding) {
        tvStoreName.setBackgroundColor(Color.TRANSPARENT)
        shimmerStoreName.stopShimmer()
        shimmerStoreName.hideShimmer()

        tvStoreAddress.setBackgroundColor(Color.TRANSPARENT)
        shimmerStoreAddress.stopShimmer()
        shimmerStoreAddress.hideShimmer()

        tvStoreHours.setBackgroundColor(Color.TRANSPARENT)
        shimmerStoreHours.stopShimmer()
        shimmerStoreHours.hideShimmer()
    }


    private fun setupRecyclerView(accessToken: String, id: Int) {
        storeViewModel.getReviews(accessToken, id)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            storeViewModel.getReviewState.collect { state ->
                Timber.d("🌀 reviewState collected: $state")

                when (state) {
                    is GetReviewState.Success -> {
                        reviewAdapter = StoreAdapter(
                            clickReview = { review -> clickReview(accessToken, review.id, id) }
                        )
                        binding.rvReviews.adapter = reviewAdapter
                        binding.rvReviews.setOnTouchListener { _, _ -> !isExpanded }

                        state.reviewDto.data.contents?.let {
                            reviewAdapter.setReviewList(it)
                        }
                    }

                    is GetReviewState.Loading -> {
                        Timber.d("review state loading...")
                    }

                    is GetReviewState.Error -> {
                        Timber.e("get review state error!!")
                    }
                }
            }
        }
    }

    private fun clickFirstReview(accessToken:String, storeId: Int) {
        Timber.d("clicknewregister")

        val intent = Intent(requireContext(), ReviewRegisterActivity::class.java)
        intent.putExtra("fromNewRegister", "new")
        intent.putExtra("storeId", storeId)
        intent.putExtra("accessToken", accessToken)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
    }

    private fun clickReview(accessToken:String, reviewId:Int, storeId:Int) {
        val getEnumState = mainViewModel.getEnumState.value

        if(getEnumState is GetEnumState.Success){
            val intent = Intent(requireContext(), ReviewDetailActivity::class.java)
            intent.putExtra("accessToken", accessToken)
            intent.putExtra("reviewId", reviewId)
            intent.putExtra("storeId", storeId)
            intent.putExtra("enum", getEnumState.enumDto.data)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
        }else{
            Timber.e("store get enum state error!")
        }
    }

    private fun writeReview(accessToken: String, storeId:Int) {
        Timber.d("writeReview")
        val intent = Intent(requireContext(), ReviewRegisterActivity::class.java)
        intent.putExtra("type", "registerActivity")
        intent.putExtra("accessToken", accessToken)
        intent.putExtra("storeId", storeId)
        intent.putExtra("fromNewRegister", false)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
    }

    private fun collapseFragment() {
        // 애니메이션 효과로 Fragment를 아래로 사라지게 처리
        val parentView = view?.parent as? View ?: return
        val targetHeight = 0 // 아래로 사라지게 하기 위해 height를 0으로 설정

        // 애니메이션 시작
        val animator = ValueAnimator.ofInt(parentView.height, targetHeight)
        animator.duration = 300 // 애니메이션 지속 시간 설정 (300ms)
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            parentView.layoutParams.height = animatedValue
            parentView.requestLayout()
        }
        animator.start()

        // 애니메이션 종료 후 Fragment 제거
        animator.doOnEnd {
            val fragmentManager = parentFragmentManager // HomeFragment의 FragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            // "StoreFragment"가 이미 존재하는지 확인
            val fragment = fragmentManager.findFragmentByTag("StoreFragment")
            if (fragment != null && fragment.isAdded) {
                fragmentTransaction.remove(fragment)
                fragmentTransaction.commitNow()
            } else {
                Timber.e("Fragment not found or not added yet")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val accessToken = arguments?.getString("accessToken")
        val storeId = arguments?.getInt("storeId")

        if (accessToken != null && storeId != null) {
            storeViewModel.getReviews(accessToken, storeId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}