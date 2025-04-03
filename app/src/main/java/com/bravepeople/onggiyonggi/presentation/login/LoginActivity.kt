package com.bravepeople.onggiyonggi.presentation.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bravepeople.onggiyonggi.databinding.ActivityLoginBinding
import com.bravepeople.onggiyonggi.presentation.MainActivity
import com.bravepeople.onggiyonggi.presentation.signup.SignUpActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            if (id == "test" && pw == "1234") {
                binding.tvLoginError.visibility = View.GONE
                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                binding.tvLoginError.visibility = View.VISIBLE
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun setupSignUpText() {
        val fullText = "아직 회원가입을 하지 않으셨나요? 회원가입"
        val spannable = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#2962FF")
                ds.isUnderlineText = false
            }
        }

        val start = fullText.indexOf("회원가입")
        val end = start + "회원가입".length
        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvGoToSignUp.text = spannable
        binding.tvGoToSignUp.movementMethod = LinkMovementMethod.getInstance()
        binding.tvGoToSignUp.highlightColor = android.graphics.Color.TRANSPARENT
    }
}
