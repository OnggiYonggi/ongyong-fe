package com.bravepeople.onggiyonggi.presentation.login.signup

import androidx.core.widget.addTextChangedListener
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.FragmentSignupFormBinding
import com.bravepeople.onggiyonggi.extension.signup.CheckIdState
import com.bravepeople.onggiyonggi.extension.signup.CheckNickNameState
import com.bravepeople.onggiyonggi.extension.signup.SignUpState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.sign

@AndroidEntryPoint
class SignUpFormFragment : Fragment() {
    private var _binding: FragmentSignupFormBinding? = null
    private val binding get() = _binding!!

    private val signUpViewModel: SignUpViewModel by viewModels()

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


        /* binding.btnCheckEmail.setOnClickListener {
             val inputEmail = binding.etEmail.text.toString().trim()

             if (inputEmail.isEmpty()) {
                 binding.tvEmailCheckResult.apply {
                     text = "이메일을 입력해주세요."
                     visibility = View.VISIBLE
                     setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                 }
                 return@setOnClickListener
             }

             if (inputEmail == "test@example.com") {
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
         }*/



        checkID()
        checkPassWord()
        checkNickName()

        //binding.etPasswd.addTextChangedListener { checkFieldsFilled() }
        //binding.etNickname.addTextChangedListener { checkFieldsFilled() }
        //binding.etEmail.addTextChangedListener { checkFieldsFilled() }

        binding.btnDone.isEnabled = false
        binding.btnDone.alpha = 0.5f
        clickDone()

    }

    private fun checkID() {
        lifecycleScope.launch {
            signUpViewModel.checkIdState.collect { state ->
                when (state) {
                    is CheckIdState.Success -> {
                        if(state.CheckIdDto.data.isExist){
                            binding.tvIdCheckResult.apply {
                                text = getString(R.string.sign_up_id_error)
                                visibility = View.VISIBLE
                                setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.red
                                    )
                                )
                            }
                        }else{
                            binding.tvIdCheckResult.apply {
                                text = getString(R.string.sign_up_id_ok)
                                visibility = View.VISIBLE
                                setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.sign_up_ok_green
                                    )
                                )
                            }
                            signUpViewModel.saveCheckedId(binding.etId.text.toString())
                            checkFieldsConfirmed()
                        }
                    }

                    is CheckIdState.Loading -> {}
                    is CheckIdState.Error -> {
                        binding.tvIdCheckResult.apply {
                            text = getString(R.string.sign_up_id_error)
                            visibility = View.VISIBLE
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        }
                    }
                }
            }
        }

        binding.btnCheckId.setOnClickListener {
            clearFocusAndHideKeyboard()
            signUpViewModel.checkId(binding.etId.text.toString())
        }
        /* binding.etId.addTextChangedListener {
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
             checkFieldsFilled()
         }*/
    }

    private fun checkPassWord() {
        val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\$%^&*()_+=-]).{8,}\$")

        binding.etPasswd.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val pw = binding.etPasswd.text.toString()
                if (!pw.matches(passwordPattern)) {
                    binding.tvPasswdCheck.apply {
                        text = getString(R.string.sign_up_pw_not_ok)
                        visibility = View.VISIBLE
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    }
                }else{
                    binding.tvPasswdCheck.visibility=View.GONE
                }
            }
        }

        binding.etPasswdCheck.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val pw = binding.etPasswd.text.toString()
                val pwCheck = binding.etPasswdCheck.text.toString()

                when {
                    // 비밀번호가 비어 있을 때
                    pw.isEmpty() && pwCheck.isNotEmpty() -> {
                        binding.tvPasswdCheckResult.apply {
                            text = getString(R.string.sign_up_pw_null)
                            visibility = View.VISIBLE
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        }
                    }

                    // 비밀번호 조건이 다를 때
                    !pw.matches(passwordPattern) -> {
                        binding.tvPasswdCheckResult.visibility=View.GONE
                    }

                    // 비밀번호와 확인이 다를 때
                    pw.isNotEmpty() && pwCheck.isNotEmpty() && pw != pwCheck -> {
                        binding.tvPasswdCheckResult.apply {
                            text = getString(R.string.sign_up_pw_error)
                            visibility = View.VISIBLE
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        }
                    }

                    // 성공
                    pw.isNotEmpty() && pw == pwCheck -> {
                        binding.tvPasswdCheckResult.apply {
                            text = getString(R.string.sign_up_pw_ok)
                            visibility = View.VISIBLE
                            setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.sign_up_ok_green)
                            )
                        }
                        signUpViewModel.saveCheckedPassword(binding.etPasswd.text.toString())
                        checkFieldsConfirmed()
                    }

                    else -> {
                        binding.tvPasswdCheckResult.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun checkNickName() {
        lifecycleScope.launch {
            signUpViewModel.checkNickNameState.collect { state ->
                when (state) {
                    is CheckNickNameState.Success -> {
                        if(state.checkNickNameDto.data.isExist){
                            binding.tvNicknameCheckResult.apply {
                                text = getString(R.string.sign_up_nickname_error)
                                visibility = View.VISIBLE
                                setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.red
                                    )
                                )
                            }
                        }else{
                            binding.tvNicknameCheckResult.apply {
                                text = getString(R.string.sign_up_nickname_ok)
                                visibility = View.VISIBLE
                                setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.sign_up_ok_green
                                    )
                                )
                            }
                            signUpViewModel.saveCheckedNickname(binding.etNickname.text.toString())
                            checkFieldsConfirmed()
                        }
                    }

                    is CheckNickNameState.Loading -> {}
                    is CheckNickNameState.Error -> {
                        binding.tvNicknameCheckResult.apply {
                            text = getString(R.string.sign_up_nickname_error)
                            visibility = View.VISIBLE
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        }
                    }
                }
            }
        }

        binding.btnCheckNickname.setOnClickListener{
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etNickname.windowToken, 0)

            clearFocusAndHideKeyboard()
            signUpViewModel.checkNickName(binding.etNickname.text.toString())
        }
    }

    private fun clearFocusAndHideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    private fun clickDone() {
        binding.btnDone.setOnClickListener {
            val idOk = signUpViewModel.isIdConfirmed(binding.etId.text.toString())
            val pwOk = signUpViewModel.isPasswordConfirmed(binding.etPasswd.text.toString())
            val nickOk = signUpViewModel.isNicknameConfirmed(binding.etNickname.text.toString())

            when {
                !idOk -> binding.tvIdCheckResult.apply {
                    text = getString(R.string.sign_up_id_confirm)
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }

                !pwOk -> binding.tvPasswdCheckResult.apply {
                    text = getString(R.string.sign_up_pw_error)
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }

                !nickOk -> binding.tvNicknameCheckResult.apply {
                    text = getString(R.string.sign_up_nickname_confirm)
                    visibility = View.VISIBLE
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.red))

                }

                else -> goNextStep()
            }
        }
    }

    private fun goNextStep(){
        lifecycleScope.launch {
            signUpViewModel.signUpState.collect{state->
                when(state){
                    is SignUpState.Success->{
                        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        requireActivity().finish()
                    }
                    is SignUpState.Loading->{}
                    is SignUpState.Error->{
                        Timber.e("sign up state error!")
                    }
                }
            }
        }

        signUpViewModel.signUp()
    }


    private fun checkFieldsConfirmed() {
        val id = binding.etId.text.toString().trim()
        val pw = binding.etPasswd.text.toString().trim()
        val nickname = binding.etNickname.text.toString().trim()

        val isIdOk = signUpViewModel.isIdConfirmed(id)
        val isPwOk = signUpViewModel.isPasswordConfirmed(pw)
        val isNickOk = signUpViewModel.isNicknameConfirmed(nickname)

        val isConfirmed = isIdOk && isPwOk && isNickOk

        binding.btnDone.isEnabled = isConfirmed
        binding.btnDone.alpha = if (isConfirmed) 1f else 0.5f
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
