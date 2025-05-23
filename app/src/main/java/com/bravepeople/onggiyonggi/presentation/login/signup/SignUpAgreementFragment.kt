package com.bravepeople.onggiyonggi.presentation.login.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentSignupAgreementBinding
import com.bravepeople.onggiyonggi.presentation.signup.TermsFragment

class SignUpAgreementFragment : Fragment() {
    private var _binding: FragmentSignupAgreementBinding? = null
    private val binding get() = requireNotNull(_binding) { "sign up agreement fragment is null" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.isEnabled = false
        binding.btnNext.alpha = 0.5f

        binding.item1.tvTitle.text = getString(R.string.sign_up_agreement_terms_of_use)
        binding.item2.tvTitle.text = getString(R.string.sign_up_agreement_user_info_agree)
        binding.item3.tvTitle.text = getString(R.string.sign_up_agreement_user_third_party_info_agree)
        binding.item4.tvTitle.text = getString(R.string.sign_up_agreement_location_agree)

        binding.item1.tvView.setOnClickListener {
            val content = readRawTextFile(R.raw.terms_of_service)
            navigateToTerms("서비스 이용 약관", content)
        }

        binding.item2.tvView.setOnClickListener {
            val content = readRawTextFile(R.raw.privacy_collect_use)
            navigateToTerms("개인정보 수집/이용 동의", content)
        }

        binding.item3.tvView.setOnClickListener {
            val content = readRawTextFile(R.raw.privacy_thirdparty)
            navigateToTerms("개인정보 제3자 제공 동의", content)
        }

        binding.item4.tvView.setOnClickListener {
            val content = readRawTextFile(R.raw.location_service)
            navigateToTerms("위치 기반 서비스 제공 동의", content)
        }

        val agreementItems = listOf(binding.item1, binding.item2, binding.item3, binding.item4)
        agreementItems.forEach { item ->
            item.root.setOnClickListener {
                toggleAgreement(item.root)
                updateAllAgreeState()
                updateNextButtonState()
            }
        }

        binding.ivCheckAll.setOnClickListener { view ->
            val checkAll = view as ImageView
            val isChecked = checkAll.tag as? Boolean ?: false

            val newIcon = if (!isChecked) R.drawable.ic_check_green else R.drawable.ic_check_gray
            checkAll.setImageResource(newIcon)
            checkAll.tag = !isChecked

            agreementItems.forEach { item ->
                val iv = item.ivCheck
                iv.setImageResource(newIcon)
                iv.tag = !isChecked
            }

            updateNextButtonState()
        }

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_signUpAgreementFragment_to_signUpFormFragment)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun toggleAgreement(itemView: View) {
        val imgCheck = itemView.findViewById<ImageView>(R.id.iv_check)
        val isChecked = imgCheck.tag as? Boolean ?: false
        imgCheck.setImageResource(
            if (!isChecked) R.drawable.ic_check_green else R.drawable.ic_check_gray
        )
        imgCheck.tag = !isChecked
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        val allChecked = listOf(
            binding.item1.ivCheck.tag,
            binding.item2.ivCheck.tag,
            binding.item3.ivCheck.tag,
            binding.item4.ivCheck.tag
        ).all { it == true }

        binding.btnNext.isEnabled = allChecked
        binding.btnNext.alpha = if (allChecked) 1f else 0.5f
    }

    private fun updateAllAgreeState() {
        val allChecked = listOf(
            binding.item1.ivCheck.tag,
            binding.item2.ivCheck.tag,
            binding.item3.ivCheck.tag,
            binding.item4.ivCheck.tag
        ).all { it == true }

        val newIcon = if (allChecked) R.drawable.ic_check_green else R.drawable.ic_check_gray
        binding.ivCheckAll.setImageResource(newIcon)
        binding.ivCheckAll.tag = allChecked
    }

    private fun readRawTextFile(@RawRes resId: Int): String {
        val inputStream = resources.openRawResource(resId)
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun navigateToTerms(title: String, content: String) {
        val fragment = TermsFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
                putString("content", content)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fcv_sign_up, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
