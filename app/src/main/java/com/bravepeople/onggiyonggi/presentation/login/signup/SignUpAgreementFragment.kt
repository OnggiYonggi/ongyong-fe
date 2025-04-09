package com.bravepeople.onggiyonggi.presentation.login.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentSignupAgreementBinding

class SignUpAgreementFragment : Fragment() {
    private var _binding: FragmentSignupAgreementBinding? = null
    private val binding get() = requireNotNull(_binding){"sign up agreement fragment is null"}

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

        binding.item1.tvTitle.text=getString(R.string.sign_up_agreement_terms_of_use)
        binding.item2.tvTitle.text=getString(R.string.sign_up_agreement_user_info_agree)
        binding.item3.tvTitle.text=getString(R.string.sign_up_agreement_user_third_party_info_agree)
        binding.item4.tvTitle.text=getString(R.string.sign_up_agreement_location_agree)

        val agreementItems = listOf(binding.item1, binding.item2, binding.item3, binding.item4)
        agreementItems.forEach { item ->
            item.root.setOnClickListener {
                toggleAgreement(item.root)
            }
        }

        binding.ivCheckAll.setOnClickListener { view ->
            val checkAll = view as ImageView
            val isChecked = checkAll.tag as? Boolean ?: false

            val newIcon = if (!isChecked) R.drawable.ic_check_green else R.drawable.ic_check_gray
            checkAll.setImageResource(newIcon)
            checkAll.tag = !isChecked

            val itemViews = listOf(
                binding.item1, binding.item2, binding.item3, binding.item4
            )
            itemViews.forEach { item ->
                val iv = item.ivCheck
                iv.setImageResource(newIcon)
                iv.tag = !isChecked
            }

            updateNextButtonState()
        }

        binding.btnNext.setOnClickListener {
            val requiredItems = listOf(R.id.item1, R.id.item2)
            val allRequiredChecked = requiredItems.all { id ->
                val item = binding.root.findViewById<View>(id)
                val isChecked = item.findViewById<ImageView>(R.id.iv_check).tag as? Boolean ?: false
                isChecked
            }

            if (allRequiredChecked) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fcv_sign_up, SignUpFormFragment())
                    .addToBackStack(null)
                    .commit()
            } else {
                context?.let { }
            }

        }


        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            requireActivity().finish()
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
        val requiredItems = listOf(R.id.item1, R.id.item2)
        val allRequiredChecked = requiredItems.all { id ->
            val item = binding.root.findViewById<View>(id)
            val isChecked = item.findViewById<ImageView>(R.id.iv_check).tag as? Boolean ?: false
            isChecked
        }

        binding.btnNext.isEnabled = allRequiredChecked
        binding.btnNext.alpha = if (allRequiredChecked) 1f else 0.5f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

