package com.bravepeople.onggiyonggi.presentation.main.character

import androidx.lifecycle.ViewModel
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Character

class CharacterCollectionViewModel:ViewModel() {
    private val collectionList = listOf(
        Character(R.drawable.character_flying_squirrel, "하늘다람쥐", "밤하늘을 날아다니는 숲속의 요정! 커다란 눈과 활강막이 매력적인 야행성 동물이에요.",false),
        Character(R.drawable.character_dooroomi, "두루미", "고고한 자태로 강과 습지를 지키는 수호자. 오래도록 장수와 평화를 상징해왔어요.",false),
        Character(R.drawable.character_horseradish_duck, "호사비오리", "물가에서만 볼 수 있는 특별한 오리! 화려한 깃털로 자연 속 패션왕이라 불려요.",true),
        Character(R.drawable.character_lynx, "스라소니", "북방의 숲에 사는 은둔의 사냥꾼. 뾰족한 귀 털이 포인트!",false),
        Character(R.drawable.character_mandarin_duck, "원앙", "사랑과 인연의 상징! 항상 짝을 이루어 다니는 다정한 커플새예요.",false),
        Character(R.drawable.character_olive_turtle, "올리브바다거북","대양을 헤엄치는 여행자. 수천 킬로미터를 넘나드는 놀라운 항해 능력을 가졌어요.", true),
        Character(R.drawable.character_red_bat, "붉은박쥐", "밤하늘을 날며 곤충을 사냥하는 붉은빛의 박쥐! 숲의 해충을 잡아주는 고마운 존재예요.",false),
        Character(R.drawable.character_spotted_seal, "점박이물범","귀여운 점박이 무늬가 특징인 해양 포유류. 바닷가 얼음 위에서 낮잠 자는 걸 좋아해요.", true),
        Character(R.drawable.character_stork, "황새", "하늘을 유유히 나는 커다란 새. 좋은 소식을 전해주는 전령으로도 유명해요.",false),
        Character(R.drawable.character_flying_squirrel, "하늘다람쥐", "밤하늘을 날아다니는 숲속의 요정! 커다란 눈과 활강막이 매력적인 야행성 동물이에요.",false),
        Character(R.drawable.character_dooroomi, "두루미", "고고한 자태로 강과 습지를 지키는 수호자. 오래도록 장수와 평화를 상징해왔어요.",false),
        Character(R.drawable.character_horseradish_duck, "호사비오리", "물가에서만 볼 수 있는 특별한 오리! 화려한 깃털로 자연 속 패션왕이라 불려요.",false),
        Character(R.drawable.character_lynx, "스라소니", "북방의 숲에 사는 은둔의 사냥꾼. 뾰족한 귀 털이 포인트!",false),
        Character(R.drawable.character_mandarin_duck, "원앙", "사랑과 인연의 상징! 항상 짝을 이루어 다니는 다정한 커플새예요.",false),
        Character(R.drawable.character_olive_turtle, "올리브바다거북","대양을 헤엄치는 여행자. 수천 킬로미터를 넘나드는 놀라운 항해 능력을 가졌어요.", false),
        Character(R.drawable.character_red_bat, "붉은박쥐", "밤하늘을 날며 곤충을 사냥하는 붉은빛의 박쥐! 숲의 해충을 잡아주는 고마운 존재예요.",false),
        Character(R.drawable.character_spotted_seal, "점박이물범","귀여운 점박이 무늬가 특징인 해양 포유류. 바닷가 얼음 위에서 낮잠 자는 걸 좋아해요.", false),
        Character(R.drawable.character_stork, "황새", "하늘을 유유히 나는 커다란 새. 좋은 소식을 전해주는 전령으로도 유명해요.",false),
    )

    fun getCollectionList():List<Character> = collectionList
}