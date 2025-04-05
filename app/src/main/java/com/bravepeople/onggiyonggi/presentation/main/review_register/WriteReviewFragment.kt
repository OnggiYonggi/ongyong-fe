package com.bravepeople.onggiyonggi.presentation.main.review_register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bravepeople.onggiyonggi.data.StoreAndDateAndPhoto
import com.bravepeople.onggiyonggi.databinding.FragmentWriteReviewBinding
import com.bravepeople.onggiyonggi.presentation.main.review_register.write_review_adapter.WriteReviewAdapter

class WriteReviewFragment : Fragment() {
    private var _binding: FragmentWriteReviewBinding? = null
    private val binding: FragmentWriteReviewBinding
        get() = requireNotNull(_binding) { "receipt fragment is null" }

    private val reviewRegisterViewModel: ReviewRegisterViewModel by activityViewModels()


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
        val writeReviewAdapter = WriteReviewAdapter(object : OnWriteReviewEventListener {
            override fun onSubmitReviewClicked() {

                val action = WriteReviewFragmentDirections.actionWriteToComplete()
                findNavController().navigate(action)
            }

            override fun rephotoClicked() {
                val action=WriteReviewFragmentDirections.actionWriteToPhoto(PhotoType.FOOD)
                findNavController().navigate(action)
            }
        })
        writeReviewAdapter.setOnScrollRequestListener { position ->
            binding.rvWrite.post {
                binding.rvWrite.smoothScrollToPosition(position)
            }
        }
        writeReviewAdapter.attachRecyclerView(binding.rvWrite)
       /* writeReviewAdapter.setOnScrollLockRequestListener {
            val layoutManager = binding.rvWrite.layoutManager as LinearLayoutManager
            val firstView = layoutManager.getChildAt(0)
            val topPosition = layoutManager.findFirstVisibleItemPosition()
            val offset = firstView?.top ?: 0

            binding.rvWrite.post {
                layoutManager.scrollToPositionWithOffset(topPosition, offset)
            }
        }*/


        binding.rvWrite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWrite.adapter = writeReviewAdapter

        writeReviewAdapter.getData(
            StoreAndDateAndPhoto(
                reviewRegisterViewModel.getStore(),
                reviewRegisterViewModel.getReceiptFoods()[0].date,
                reviewRegisterViewModel.getReviewPhoto()
            ),
            reviewRegisterViewModel.getReceiptFoods(),
            reviewRegisterViewModel.getSelectQuestion(requireContext())
        )
    }
}