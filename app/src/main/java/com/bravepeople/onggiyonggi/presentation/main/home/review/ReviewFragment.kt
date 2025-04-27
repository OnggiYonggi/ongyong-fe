package com.bravepeople.onggiyonggi.presentation.main.home.review

import android.animation.ValueAnimator
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
import com.bravepeople.onggiyonggi.data.toStore
import com.bravepeople.onggiyonggi.databinding.FragmentReviewBinding
import com.bravepeople.onggiyonggi.extension.GetStoreTimeState
import com.bravepeople.onggiyonggi.presentation.main.home.store_register.StoreRegisterActivity
import com.bravepeople.onggiyonggi.presentation.main.home.review.review_detail.ReviewDetailActivity
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.ReviewRegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding: FragmentReviewBinding
        get() = requireNotNull(_binding) { "review fragment is null" }

    private val reviewViewModel: ReviewViewModel by activityViewModels()
    private lateinit var reviewAdapter: ReviewAdapter
    private var isExpanded = false

    private var initialY = 0f
    private var downY = 0f
    private val minHeightToShow = 300f

    companion object {
        fun newInstance(search: Search): ReviewFragment {
            val fragment = ReviewFragment()
            val args = Bundle().apply {
                putParcelable("search", search)
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
        val getData = arguments?.getParcelable<Search>("search")
        setupBottomSheetBehavior()
        getStoreTime()
        setupUI(getData)
        Timber.d("Fragment height: ${binding.root.height}")
    }

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
            reviewViewModel.getStoreTimeState.collect{getStoreTimeState->
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

    private fun setupUI(data: Search?) {
        if (data == null) return
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

        reviewViewModel.searchStoreTime(data.name)
    }


    private fun setupRecyclerView(getData: Search) {
        reviewAdapter = ReviewAdapter(
            clickReview = {review -> clickReview(review)},
        )
        binding.rvReviews.adapter = reviewAdapter
        binding.rvReviews.setOnTouchListener { _, event ->
            !isExpanded  // isExpanded가 false면 true를 반환해서 터치 막음
        }
        loadStoreAndReviews(getData)
    }

    private fun loadStoreAndReviews(getData: Search) {
        val store = getData.toStore()
        store?.let {
            reviewAdapter.setReviewList(reviewViewModel.getReviewList())
        } ?: Timber.e("Store is null")
    }

    private fun clickNewRegister() {
        Timber.d("clicknemregister")
        val intent = Intent(requireContext(), StoreRegisterActivity::class.java)
        intent.putExtra("type", "new")
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
    }

    private fun clickReview(review: Review) {
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


/*
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.Search
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.data.toStore
import com.bravepeople.onggiyonggi.databinding.FragmentReviewBinding
import com.bravepeople.onggiyonggi.presentation.review.review_detail.ReviewDetailActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

class ReviewFragment:BottomSheetDialogFragment(), ReviewClickListener {
    private var _binding: FragmentReviewBinding?=null
    private val binding: FragmentReviewBinding
        get()= requireNotNull(_binding){"receipt fragment is null"}

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var isFirstResume = true

    private val reviewViewModel:ReviewViewModel by activityViewModels()
    private lateinit var reviewAdapter:ReviewAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setDimAmount(0f)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        reviewAdapter=ReviewAdapter(requireContext(), this)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (reviewAdapter.getItemViewType(position) == 0) 2 else 1
            }
        }
        binding.rvReview.layoutManager = gridLayoutManager
        binding.rvReview.adapter=reviewAdapter

        val store = when {
            arguments?.containsKey("search") == true -> {
                val search = arguments?.getParcelable<Search>("search")
                search?.toStore()
            }
            else -> reviewViewModel.getStore()
        }

        if (store == null) {
            Timber.e("Store is null")
            return
        }

        getStore(store)
        getReview()

    }

    private fun getStore(store: StoreOrReceipt.Store) {
        reviewAdapter.setStore(store)
    }

    private fun getReview(){
        val reviews = reviewViewModel.getReviewList()
        reviewAdapter.setReviewList(reviews)
    }

     override fun onStart() {
         super.onStart()

         val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
         bottomSheet?.let {
             bottomSheetBehavior = BottomSheetBehavior.from(it).apply {
                 peekHeight = (resources.displayMetrics.heightPixels * 0.25).toInt()
                 isFitToContents = true
                 skipCollapsed = false
                 isHideable = false
                 state = BottomSheetBehavior.STATE_COLLAPSED
             }

             binding.rvReview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                 override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                     val isAtTop = !recyclerView.canScrollVertically(-1)
                     bottomSheetBehavior?.isDraggable = isAtTop
                 }
             })
         }
     }

    override fun onResume() {
        super.onResume()
        if (!isFirstResume) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            reviewAdapter.setReviewList(reviewViewModel.getNewList())
        }
        isFirstResume = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onReviewClick(review: Review) {
        val intent= Intent(requireContext(), ReviewDetailActivity::class.java)
        intent.putExtra("review", review)
        startActivity(intent)
        requireActivity().overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.stay_still
        )
    }

    companion object {
        fun newInstance(search: Search): ReviewFragment {
            val fragment = ReviewFragment()
            val args = Bundle().apply {
                putParcelable("search", search)
            }
            fragment.arguments = args
            return fragment
        }
    }

}*/
