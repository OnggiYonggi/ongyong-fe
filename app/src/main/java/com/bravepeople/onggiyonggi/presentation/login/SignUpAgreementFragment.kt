package com.bravepeople.onggiyonggi.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentSignupAgreementBinding

class SignUpAgreementFragment : Fragment() {
    private var _binding: FragmentSignupAgreementBinding? = null
    private val binding get() = _binding!!

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

        val item1 = binding.root.findViewById<View>(R.id.item1)
        val item2 = binding.root.findViewById<View>(R.id.item2)
        val item3 = binding.root.findViewById<View>(R.id.item3)
        val item4 = binding.root.findViewById<View>(R.id.item4)

        item1.findViewById<TextView>(R.id.tv_title).text = "서비스 이용 약관"
        item2.findViewById<TextView>(R.id.tv_title).text = "개인정보 수집/이용 동의"
        item3.findViewById<TextView>(R.id.tv_title).text = "개인정보 제3자 정보제공 동의"
        item4.findViewById<TextView>(R.id.tv_title).text = "위치 기반 서비스 제공 동의"

        listOf(item1, item2, item3, item4).forEach { item ->
            item.setOnClickListener {
                toggleAgreement(item)
            }
        }

        binding.root.findViewById<ImageView>(R.id.iv_check_all).setOnClickListener { view ->
            val checkAll = view as ImageView
            val isChecked = checkAll.tag as? Boolean ?: false

            val newIcon = if (!isChecked) R.drawable.ic_check_green else R.drawable.ic_check_gray
            checkAll.setImageResource(newIcon)
            checkAll.tag = !isChecked

            val itemViews = listOf(
                binding.root.findViewById<View>(R.id.item1),
                binding.root.findViewById<View>(R.id.item2),
                binding.root.findViewById<View>(R.id.item3),
                binding.root.findViewById<View>(R.id.item4)
            )
            itemViews.forEach { item ->
                val iv = item.findViewById<ImageView>(R.id.iv_check)
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
                    .replace(R.id.container, SignUpFormFragment())
                    .addToBackStack(null)
                    .commit()
            } else {
                context?.let { }
            }

        }


        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()

            requireActivity().findViewById<View>(R.id.login).visibility = View.VISIBLE
            requireActivity().findViewById<View>(R.id.container).visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

}

