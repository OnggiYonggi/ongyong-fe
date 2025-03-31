package com.bravepeople.onggiyonggi.presentation.review

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.Search
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.data.toStore
import com.bravepeople.onggiyonggi.databinding.FragmentReviewBinding
import com.bravepeople.onggiyonggi.presentation.home.ReviewBottomSheetListener
import com.bravepeople.onggiyonggi.presentation.review.review_detail.ReviewDetailActivity
import timber.log.Timber

class ReviewFragment : Fragment(), ReviewClickListener {
    private var _binding: FragmentReviewBinding? = null
    private val binding:FragmentReviewBinding
        get() = requireNotNull(_binding){"review fragment is null"}

    private val reviewViewModel: ReviewViewModel by activityViewModels()
    private lateinit var reviewAdapter: ReviewAdapter

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
        setupBottomSheetBehavior()
        setupRecyclerView()
        loadStoreAndReviews()
        Timber.d("Fragment height: ${binding.root.height}")
    }

    private fun setupBottomSheetBehavior() {
        val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
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
                        if (v.translationY > v.height * 0.3f) {
                            parentFragmentManager.popBackStack()
                        } else {
                            v.animate().translationY(0f).start()

                            // 전체화면 전환
                            (view?.parent as? View)?.let { parentView ->
                                parentView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                                parentView.requestLayout()
                            }
                        }
                        dragging = false
                    }
                }
                return true
            }
        })
    }


    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter(requireContext(), this)
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (reviewAdapter.getItemViewType(position) == 0) 2 else 1
            }
        }
        binding.rvReview.layoutManager = gridLayoutManager
        binding.rvReview.adapter = reviewAdapter
    }

    private fun loadStoreAndReviews() {
        val store = arguments?.getParcelable<Search>("search")?.toStore() ?: reviewViewModel.getStore()
        store?.let {
            reviewAdapter.setStore(it)
            reviewAdapter.setReviewList(reviewViewModel.getReviewList())
        } ?: Timber.e("Store is null")
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
