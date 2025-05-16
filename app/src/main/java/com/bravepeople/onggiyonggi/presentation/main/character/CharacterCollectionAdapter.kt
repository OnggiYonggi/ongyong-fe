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
import coil.load
import coil.transform.Transformation
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Character
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCollectionDto
import com.bravepeople.onggiyonggi.databinding.ItemCollectionBinding
import timber.log.Timber

class CharacterCollectionAdapter(
    private val clickCharacterIndex:(Int, List<ResponseCollectionDto.Data.CharacterResponseDto>) -> Unit,
):
    RecyclerView.Adapter<CharacterCollectionAdapter.CharacterCollectionViewHolder>() {
    private val collectionList = mutableListOf<ResponseCollectionDto.Data.CharacterResponseDto>()

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

    fun getList(list:List<ResponseCollectionDto.Data>){
        val collectedMap=list.associateBy {it.id }

        val fullList=(1..9).map{id->
            collectedMap[id]?.characterResponseDto ?:ResponseCollectionDto.Data.CharacterResponseDto(
                id = id,
                name = "",
                description = "",
                story = "",
                imageURL = ""
            )
        }

        collectionList.clear()
        collectionList.addAll(fullList)
        notifyDataSetChanged()
    }

    inner class CharacterCollectionViewHolder(private val binding:ItemCollectionBinding)
        :RecyclerView.ViewHolder(binding.root){
        fun bind(character: ResponseCollectionDto.Data.CharacterResponseDto){
            with(binding) {
                val isCollected = character.imageURL.isNotEmpty()

                ivCharacter.load(
                    if (isCollected) character.imageURL else R.drawable.ic_hidden_gray_48
                ) {
                    if (!isCollected) transformations(GrayscaleTransformation())
                }

                ivBackground.load(R.drawable.ic_card_front) {
                    if (!isCollected) transformations(GrayscaleTransformation())
                }

                itemCollection.setOnClickListener {
                    if (isCollected) {
                        Timber.d("adapter에서 캐릭터 클릭")
                        clickCharacterIndex(bindingAdapterPosition, collectionList)
                    }
                }

                tvCharacterName.apply {
                    text = character.name
                    visibility = if (isCollected) View.VISIBLE else View.INVISIBLE
                }
            }
        }
    }

    inner class GrayscaleTransformation : Transformation {

        override val cacheKey: String
            get() = "GrayscaleTransformation"

        override suspend fun transform(input: Bitmap, size: coil.size.Size): Bitmap {
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