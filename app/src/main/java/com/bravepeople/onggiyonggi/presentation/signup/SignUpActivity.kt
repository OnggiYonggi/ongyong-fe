package com.bravepeople.onggiyonggi.presentation.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bravepeople.onggiyonggi.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignUpAgreementFragment())
                .commit()
        }
    }
}
