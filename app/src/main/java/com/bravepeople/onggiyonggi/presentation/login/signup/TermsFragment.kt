package com.bravepeople.onggiyonggi.presentation.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bravepeople.onggiyonggi.databinding.FragmentTermsBinding

class TermsFragment : Fragment() {
    private var _binding: FragmentTermsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title") ?: "약관"
        val content = arguments?.getString("content") ?: "내용을 불러올 수 없습니다."

        binding.tvTitle.text = title
        binding.tvContent.text = content

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
