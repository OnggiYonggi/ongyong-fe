package com.bravepeople.onggiyonggi.presentation.main.character

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.Canvas
import coil3.load
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.Transformation
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Character
import com.bravepeople.onggiyonggi.databinding.ItemCollectionBinding
import timber.log.Timber

class CharacterCollectionAdapter(
    private val clickCharacter:(Character) -> Unit,
):
    RecyclerView.Adapter<CharacterCollectionAdapter.CharacterCollectionViewHolder>() {
    private val collectionList = mutableListOf<Character>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CharacterCollectionViewHolder {
        val binding=ItemCollectionBinding.inflate(
            LayoutInflater.from(parent.context), parent,false
        )
        return CharacterCollectionViewHolder(binding)
    }

    override fun getItemCount(): Int = collectionList.size

    override fun onBindViewHolder(holder: CharacterCollectionViewHolder, position: Int) {
        holder.bind(collectionList[position])
    }

    fun getList(list:List<Character>){
        collectionList.clear()
        collectionList.addAll(list)
        notifyDataSetChanged()
    }

    inner class CharacterCollectionViewHolder(private val binding:ItemCollectionBinding)
        :RecyclerView.ViewHolder(binding.root){
        fun bind(character: Character){
            with(binding){
                ivCharacter.load(character.image) {
                    if (!character.collected) {
                        transformations(GrayscaleTransformation())
                    }
                }


                tvCharacterName.apply {
                    text = character.name
                    visibility = if (character.collected) View.VISIBLE else View.INVISIBLE
                }

                if(character.collected){
                    itemCollection.setOnClickListener{
                        Timber.d("adapter에서 캐릭터 클릭")
                        clickCharacter(character)
                    }
                }else ivBackground.load(R.drawable.ic_card_front){
                    transformations(GrayscaleTransformation())
                }
            }
        }
    }

    inner class GrayscaleTransformation : Transformation() {

        override val cacheKey: String
            get() = "GrayscaleTransformation"

        override suspend fun transform(input: Bitmap, size: Size): Bitmap {
            val output = Bitmap.createBitmap(input.width, input.height, input.config ?: Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val paint = Paint()

            val colorMatrix = ColorMatrix().apply {
                setSaturation(0f)
            }

            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(input, 0f, 0f, paint)

            return output
        }
    }

}