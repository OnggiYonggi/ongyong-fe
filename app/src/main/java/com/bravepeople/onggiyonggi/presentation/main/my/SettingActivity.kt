package com.bravepeople.onggiyonggi.presentation.main.my

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bravepeople.onggiyonggi.databinding.ActivitySettingBinding
import com.bravepeople.onggiyonggi.presentation.login.LoginActivity

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPushAlarm.setOnClickListener {
            val switch = binding.switchPushAlarm
            switch.isChecked = !switch.isChecked
        }

        binding.btnMarketing.setOnClickListener {
            val switch = binding.switchMarketing
            switch.isChecked = !switch.isChecked
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvWithdraw.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("회원탈퇴")
                .setMessage("정말로 회원탈퇴를 진행하시겠습니까?\n이 작업은 되돌릴 수 없습니다.")
                .setPositiveButton("탈퇴") { _, _ ->

                    Toast.makeText(this, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("취소", null)
                .show()
        }

    }
}
