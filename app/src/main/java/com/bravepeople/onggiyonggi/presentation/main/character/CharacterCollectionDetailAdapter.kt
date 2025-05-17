package com.bravepeople.onggiyonggi.presentation.main.character

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseCollectionDto

class CharacterCollectionDetailAdapter(
    activity: FragmentActivity,
): FragmentStateAdapter(activity) {
    private val characters= mutableListOf<ResponseCollectionDto.Data.CharacterResponseDto>()

    override fun getItemCount(): Int = characters.size

    override fun createFragment(position: Int): Fragment {
        return CharacterCollectionDetailFragment.newInstance(characters[position])
    }

    fun getList(list:List<ResponseCollectionDto.Data.CharacterResponseDto>){
        characters.clear()
        characters.addAll(list)
        notifyDataSetChanged()
    }
}