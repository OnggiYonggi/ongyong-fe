package com.bravepeople.onggiyonggi.presentation.main.my

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.response_dto.my.ResponseGetMyReviewsDto
import com.bravepeople.onggiyonggi.databinding.FragmentMyBinding
import com.bravepeople.onggiyonggi.extension.home.GetEnumState
import com.bravepeople.onggiyonggi.extension.my.GetMyInfoState
import com.bravepeople.onggiyonggi.extension.my.GetMyReviewsState
import com.bravepeople.onggiyonggi.presentation.MainViewModel
import com.bravepeople.onggiyonggi.presentation.main.home.store.review_detail.ReviewDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MyFragment : Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!

    private enum class SortType { LATEST, LIKE }

    private var currentSort = SortType.LATEST
    private lateinit var myReviewAdapter: MyReviewAdapter

    private val mainViewModel:MainViewModel by activityViewModels()
    private val myViewModel:MyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        binding.ivProfile.load(R.drawable.img_user2) {
            transformations(CircleCropTransformation())
        }

        binding.ivEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.ivSetting.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
        }

        getReviewList()
        clickSort()

        highlightSelected(binding.tvSortLatest, binding.tvSortLike)
    }

    private fun getReviewList() {
        val token = mainViewModel.accessToken.value
        lifecycleScope.launch {
            myViewModel.myInfoState.collect{state->
                when(state){
                    is GetMyInfoState.Success->{
                        binding.tvNickname.text=state.myDto.data
                    }
                    is GetMyInfoState.Loading->{

                    }
                    is GetMyInfoState.Error->{

                    }
                }
            }
        }
        lifecycleScope.launch {
            myViewModel.myReviewsState.collect{state->
                when(state){
                    is GetMyReviewsState.Success->{
                        updateReviewList(token, state.reviewDto.data)
                    }
                    is GetMyReviewsState.Loading->{}
                    is GetMyReviewsState.Error->{
                        Timber.e("my reviews state error!")
                    }
                }
            }
        }

        myViewModel.getMyInfo(token!!)
        myViewModel.getMyReviews(token!!)
    }

    private fun updateReviewList(token: String?, data: List<ResponseGetMyReviewsDto.ReviewData>) {
        val enum = mainViewModel.getEnumState.value
        myReviewAdapter = MyReviewAdapter { reviewId, storeId ->
            if(enum is GetEnumState.Success){
                val intent = Intent(requireContext(), ReviewDetailActivity::class.java)
                intent.putExtra("accessToken", token)
                intent.putExtra("reviewId", reviewId)
                intent.putExtra("storeId", storeId)
                intent.putExtra("enum", enum.enumDto.data)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
            }
        }
        binding.rvReviewImages.adapter = myReviewAdapter
        binding.rvReviewImages.layoutManager = GridLayoutManager(requireContext(), 3)

        val sortedList = when (currentSort) {
            SortType.LATEST -> data.sortedByDescending { it.createdAt }
            SortType.LIKE -> data.sortedByDescending { it.storeId }
        }
        myReviewAdapter.setReviewList(sortedList)
        binding.tvReviewCount.text=getString(R.string.my_review_count, sortedList.size)
    }

    private fun clickSort(){
        binding.tvSortLatest.setOnClickListener {
            currentSort = SortType.LATEST
            //updateReviewList(token, state.reviewDto.data)
            highlightSelected(binding.tvSortLatest, binding.tvSortLike)
        }

        binding.tvSortLike.setOnClickListener {
            currentSort = SortType.LIKE
            //updateReviewList(token, state.reviewDto.data)
            highlightSelected(binding.tvSortLike, binding.tvSortLatest)
        }
    }

    private fun highlightSelected(selected: TextView, unselected: TextView) {
        selected.setTypeface(null, Typeface.BOLD)
        selected.setTextColor(Color.parseColor("#000000"))

        unselected.setTypeface(null, Typeface.NORMAL)
        unselected.setTextColor(Color.parseColor("#888888"))
    }

    fun refreshData(){
        setting()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}
