package com.bravepeople.onggiyonggi.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bravepeople.onggiyonggi.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.presentation.home.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var  binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
    }

    private fun initBinds(){
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setting()
    }

    private fun setting(){
        setFirstFragment()
        /*binding.btnReview.setOnClickListener{
            val reviewFragment=ReviewFragment()
            reviewFragment.show(supportFragmentManager, "ReviewFragment")

            *//*val intent=Intent(this, ReviewActivity::class.java)
            startActivity(intent)*//*
        }*/
    }

    private fun replaceFragment(fragment:Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_main, fragment)
            .commit()
    }

    private fun setFirstFragment(){
        binding.bnvMain.selectedItemId=binding.bnvMain.menu.getItem(0).itemId
        replaceFragment(HomeFragment())
    }

    /*private fun clickBottomNavigation(){
        binding.bnvMain.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_home->{
                    replaceFragment(HomeFragment(())
                    true
                }
                R.id.menu_character->{
                    replaceFragment(CharacterFragment())
                    true
                }
                R.id.menu_my->{
                    replaceFragment(MyFragment())
                    true
                }
                else->{
                    false
                }
            }
        }
    }*/
}