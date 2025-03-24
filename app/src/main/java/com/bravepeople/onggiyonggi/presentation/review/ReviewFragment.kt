package com.bravepeople.onggiyonggi.presentation.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.databinding.FragmentReviewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

class ReviewFragment:BottomSheetDialogFragment() {
    private var _binding: FragmentReviewBinding?=null
    private val binding: FragmentReviewBinding
        get()= requireNotNull(_binding){"receipt fragment is null"}

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var isFirstResume = true

    private val reviewViewModel:ReviewViewModel by activityViewModels()

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
        val reviewAdapter=ReviewAdapter()
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (reviewAdapter.getItemViewType(position) == 0) 2 else 1
            }
        }
        binding.rvReview.layoutManager = gridLayoutManager
        binding.rvReview.adapter=reviewAdapter

        val store = reviewViewModel.getStore()
        val reviews = reviewViewModel.getReviewList()
        Timber.d( "store = $store, reviews = ${reviews.size}")

        reviewAdapter.setReviewList(store, reviews)
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
        }
        isFirstResume = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}