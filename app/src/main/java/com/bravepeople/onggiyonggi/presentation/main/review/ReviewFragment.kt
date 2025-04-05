package com.bravepeople.onggiyonggi.presentation.main.review

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.Search
import com.bravepeople.onggiyonggi.data.toStore
import com.bravepeople.onggiyonggi.databinding.FragmentReviewBinding
import com.bravepeople.onggiyonggi.presentation.main.home.register.StoreRegisterActivity
import com.bravepeople.onggiyonggi.presentation.main.review.review_detail.ReviewDetailActivity
import timber.log.Timber

class ReviewFragment : Fragment(), ReviewClickListener {
    private var _binding: FragmentReviewBinding? = null
    private val binding: FragmentReviewBinding
        get() = requireNotNull(_binding) { "review fragment is null" }

    private val reviewViewModel: ReviewViewModel by activityViewModels()
    private lateinit var reviewAdapter: ReviewAdapter
    private var isExpanded = false

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
        setupUI(getData)
        Timber.d("Fragment height: ${binding.root.height}")
    }

    private fun setupBottomSheetBehavior() {
        val parent = view?.parent as? View
        binding.root.post {
            parent?.layoutParams?.height = (resources.displayMetrics.heightPixels * 0.22).toInt()
            parent?.requestLayout()
        }

        binding.root.setOnTouchListener(object : View.OnTouchListener {
            private var downY = 0f
            private var dragging = false

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downY = event.rawY
                        dragging = false
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val dy = event.rawY - downY
                        if (dy > 30 || dy < -30) dragging = true
                        if (dragging) {
                            v.translationY = dy.coerceAtLeast(0f)
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        v.animate().translationY(0f).start()
                        val parent = v.parent as? View ?: return true
                        if (v.translationY > v.height * 0.22f) {
                            // 아래로 드래그 → 원래 높이로 되돌아감 (30%)
                            val targetHeight =
                                (resources.displayMetrics.heightPixels * 0.22).toInt()
                            animateHeight(parent, targetHeight)

                            // bar 위치도 원래대로 (기본 마진으로 복귀)
                            val layoutParams =
                                binding.bar.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.topMargin = 20.dpToPx() // 초기 마진 값
                            binding.bar.layoutParams = layoutParams
                            isExpanded = false
                        } else {
                            // 위로 드래그 → 전체화면 확장
                            val screenHeight = resources.displayMetrics.heightPixels
                            val statusBarHeight = getStatusBarHeight()
                            val availableHeight = screenHeight - statusBarHeight
                            animateHeight(parent, availableHeight)

                            v/*al layoutParams =
                                binding.bar.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.topMargin =
                                statusBarHeight */
                            //binding.bar.layoutParams = layoutParams
                            isExpanded = true
                        }
                        dragging = false
                    }
                }
                return true
            }
        })
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

    private fun setupUI(data: Search?) {
        if (data == null) return
        val isBan = data.isBan

        with(binding) {
            clBan.visibility = if (isBan) View.VISIBLE else View.GONE
            rvReview.visibility = if (isBan) View.GONE else View.VISIBLE

            if (isBan) {
                clBan.minHeight = (resources.displayMetrics.heightPixels * 0.6).toInt()
                btnRegister.setOnClickListener {
                    clickNewRegister()
                }
            } else {
                setupRecyclerView(data)
            }
        }
    }


    private fun setupRecyclerView(getData: Search) {
        reviewAdapter = ReviewAdapter(getData.isBan, this)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (reviewAdapter.isGridItem(position)) 1 else 2
            }
        }

        binding.rvReview.layoutManager = gridLayoutManager
        binding.rvReview.adapter = reviewAdapter
        binding.rvReview.setOnTouchListener { _, event ->
            !isExpanded  // isExpanded가 false면 true를 반환해서 터치 막음
        }
        loadStoreAndReviews(getData)
    }

    private fun loadStoreAndReviews(getData: Search) {
        val store = getData.toStore()
        store?.let {
            reviewAdapter.setStore(it)
            reviewAdapter.setReviewList(reviewViewModel.getReviewList())
        } ?: Timber.e("Store is null")
    }

    private fun clickNewRegister() {
        val intent = Intent(requireContext(), StoreRegisterActivity::class.java)
        intent.putExtra("type", "new")
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
    }

    override fun onReviewClick(review: Review) {
        val intent = Intent(requireContext(), ReviewDetailActivity::class.java)
        intent.putExtra("review", review)
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
