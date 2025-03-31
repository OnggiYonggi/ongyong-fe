package com.bravepeople.onggiyonggi.presentation.login

import androidx.core.widget.addTextChangedListener
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentSignupFormBinding

class SignUpFormFragment : Fragment() {
    private var _binding: FragmentSignupFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            showExitWarning()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitWarning()
                }
            }
        )

        binding.btnCheckNickname.setOnClickListener {
            val inputNickname = binding.etNickname.text.toString().trim()

            if (inputNickname.isEmpty()) {
                binding.tvNicknameCheckResult.apply {
                    text = "닉네임을 입력해주세요."
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
                return@setOnClickListener
            }

            if (inputNickname == "nickname123") { // 예시 중복 닉네임
                binding.tvNicknameCheckResult.apply {
                    text = "이미 사용 중인 닉네임입니다."
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
            } else {
                binding.tvNicknameCheckResult.apply {
                    text = "사용 가능한 닉네임입니다."
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
        }

        binding.btnCheckEmail.setOnClickListener {
            val inputEmail = binding.etEmail.text.toString().trim()

            if (inputEmail.isEmpty()) {
                binding.tvEmailCheckResult.apply {
                    text = "이메일을 입력해주세요."
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
                return@setOnClickListener
            }

            if (inputEmail == "test@example.com") { // 예시 중복 이메일
                binding.tvEmailCheckResult.apply {
                    text = "이미 사용 중인 이메일입니다."
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
            } else {
                binding.tvEmailCheckResult.apply {
                    text = "사용 가능한 이메일입니다."
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
        }

        binding.etId.addTextChangedListener {
            val inputId = it.toString().trim()

            if (inputId.isEmpty()) {
                binding.tvIdCheckResult.visibility = View.GONE
            } else if (inputId == "test") {
                binding.tvIdCheckResult.apply {
                    text = "사용 불가능한 ID입니다."
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }
            } else {
                binding.tvIdCheckResult.apply {
                    text = "사용 가능한 ID입니다."
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
        }

        binding.btnDone.setOnClickListener {
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            requireActivity().findViewById<View>(R.id.login).visibility = View.VISIBLE
            requireActivity().findViewById<View>(R.id.container).visibility = View.GONE
        }
    }

    private fun showExitWarning() {
        AlertDialog.Builder(requireContext())
            .setTitle("경고")
            .setMessage("입력한 정보가 모두 사라집니다. 그래도 나가시겠습니까?")
            .setPositiveButton("나가기") { _, _ ->
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
