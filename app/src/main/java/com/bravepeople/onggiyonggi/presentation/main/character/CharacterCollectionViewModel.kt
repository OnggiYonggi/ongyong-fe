package com.bravepeople.onggiyonggi.presentation.main.character

import androidx.lifecycle.ViewModel
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Character

class CharacterCollectionViewModel:ViewModel() {
    private val collectionList = listOf(
        Character(R.drawable.character_flying_squirrel, "하늘다람쥐", false),
        Character(R.drawable.character_dooroomi, "두루미", false),
        Character(R.drawable.character_horseradish_duck, "호사비오리", true),
        Character(R.drawable.character_lynx, "스라소니", false),
        Character(R.drawable.character_mandarin_duck, "원앙", false),
        Character(R.drawable.character_olive_turtle, "올리브바다거북", false),
        Character(R.drawable.character_red_bat, "붉은박쥐", false),
        Character(R.drawable.character_spotted_seal, "점박이물범", true),
        Character(R.drawable.character_stork, "황새", false),
        Character(R.drawable.character_flying_squirrel, "하늘다람쥐", false),
        Character(R.drawable.character_dooroomi, "두루미", true),
        Character(R.drawable.character_horseradish_duck, "호사비오리", false),
        Character(R.drawable.character_lynx, "스라소니", false),
        Character(R.drawable.character_mandarin_duck, "원앙", false),
        Character(R.drawable.character_olive_turtle, "올리브바다거북", false),
        Character(R.drawable.character_red_bat, "붉은박쥐", true),
        Character(R.drawable.character_spotted_seal, "점박이물범", false),
        Character(R.drawable.character_stork, "황새", false),
    )

    fun getCollectionList():List<Character> = collectionList
}