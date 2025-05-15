package com.bravepeople.onggiyonggi.presentation.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ActivityLoginBinding
import com.bravepeople.onggiyonggi.extension.login.LoginState
import com.bravepeople.onggiyonggi.presentation.MainActivity
import com.bravepeople.onggiyonggi.presentation.login.signup.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var passwordVisible = false

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doLogin()
        setupListeners()
        setupSignUpText()
    }

    private fun setupListeners() {
        binding.btnTogglePasswd.setOnClickListener {
            passwordVisible = !passwordVisible
            val inputType = if (passwordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etPasswd.inputType = inputType
            binding.etPasswd.setSelection(binding.etPasswd.text.length)
        }

        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString()
            val pw = binding.etPasswd.text.toString()

            if (id == "") {
                binding.tvIdError.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.login_id_null)
                }
            } else if (pw == "") {
                binding.tvPwError.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.login_pw_null)
                }
            } else {
                loginViewModel.login(id, pw)
            }
        }

        binding.tvGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun setupSignUpText() {
        val fullText = "아직 회원가입을 하지 않으셨나요? 회원가입"
        val spannable = SpannableString(fullText)

        val start = fullText.lastIndexOf("회원가입")
        val end = start + "회원가입".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // 밑줄 제거
            }
        }

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#2962FF")),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvGoToSignUp.text = spannable
        binding.tvGoToSignUp.movementMethod = LinkMovementMethod.getInstance()
        binding.tvGoToSignUp.highlightColor = Color.TRANSPARENT

    }

    private fun doLogin() {
        val intent = Intent(this, MainActivity::class.java)
        lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Success -> {
                        binding.tvIdError.visibility = View.GONE
                        binding.tvPwError.visibility = View.GONE
                        //Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                        intent.putExtra("accessToken", state.loginDto.data.accessToken)
                        startActivity(intent)
                        finish()
                        /*if (state.loginDto.success) {
                            binding.tvIdError.visibility = View.GONE
                            binding.tvPwError.visibility = View.GONE
                            //Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                            finish()
                        } else {
                            when (state.loginDto.status) {
                                "MEMBER404" -> {
                                    binding.tvIdError.apply {
                                        visibility = View.VISIBLE
                                        text = getString(R.string.login_id_error)
                                    }
                                }
                                "MEMBER401"->{
                                    binding.tvPwError.apply {
                                        visibility = View.VISIBLE
                                        text = getString(R.string.login_pw_error)
                                    }
                                }
                            }
                        }*/

                        Timber.d("error: ${binding.tvIdError.text}")
                    }

                    is LoginState.Loading -> {}
                    is LoginState.Error -> {
                        Timber.e("login state error!")
                        when (state.message) {
                            "MEMBER404" -> {
                                binding.tvIdError.apply {
                                    visibility = View.VISIBLE
                                    text = getString(R.string.login_id_error)
                                }
                            }

                            "MEMBER401" -> {
                                binding.tvPwError.apply {
                                    visibility = View.VISIBLE
                                    text = getString(R.string.login_pw_error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
