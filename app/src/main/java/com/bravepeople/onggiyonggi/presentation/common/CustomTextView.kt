package com.bravepeople.onggiyonggi.presentation.common

import android.content.Context
import android.graphics.Canvas
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CustomTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var lines: List<String> = emptyList()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val availableWidth = measuredWidth - paddingLeft - paddingRight
        val paint: TextPaint = paint
        val text = text.toString()

        val tempLines = mutableListOf<String>()
        var currentLine = StringBuilder()
        var currentLineWidth = 0f

        for (char in text) {
            val charWidth = paint.measureText(char.toString())

            if (currentLineWidth + charWidth > availableWidth) {
                tempLines.add(currentLine.toString())
                currentLine = StringBuilder()
                currentLine.append(char)
                currentLineWidth = charWidth
            } else {
                currentLine.append(char)
                currentLineWidth += charWidth
            }
        }
        if (currentLine.isNotEmpty()) {
            tempLines.add(currentLine.toString())
        }

        lines = tempLines
    }

    override fun onDraw(canvas: Canvas) {
        val paint = paint
        paint.color = currentTextColor

        var y = paddingTop + paint.textSize

        for (line in lines) {
            canvas.drawText(line, paddingLeft.toFloat(), y, paint)
            y += paint.textSize + lineSpacingExtra
        }
    }
}
