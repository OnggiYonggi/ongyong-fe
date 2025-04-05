package com.bravepeople.onggiyonggi.presentation.login.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_signup)
        setting()
    }

    private fun setting(){
            supportFragmentManager.beginTransaction()
                .add(R.id.fcv_sign_up, SignUpAgreementFragment())
                .commit()
    }
}
