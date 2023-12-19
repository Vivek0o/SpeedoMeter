package com.example.test

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class Speedometer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val speedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    var colorPrimary : Int

    private var speed: Float = 0f
    private lateinit var typeface: Typeface

    init {
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.FILL_AND_STROKE

        textPaint.color = Color.BLACK
        textPaint.textSize = 30f
        textPaint.strokeWidth = 5f
        textPaint.textAlign = Paint.Align.CENTER

        speedPaint.strokeCap = Paint.Cap.ROUND
        speedPaint.style = Paint.Style.STROKE
        speedPaint.strokeWidth = 5f

        fillPaint.style = Paint.Style.FILL
        colorPrimary = ContextCompat.getColor(context, R.color.ligh_green)
    }

    fun setTypeface(typeface: Typeface) {
        this.typeface = typeface
    }

    fun setSpeed(speed: Float) {
        this.speed = speed
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(width, height) / 2f - 20f  // Increased distance between arcs

        // Create a SweepGradient for the double arc
        val gradient = SweepGradient(
            centerX, centerY,
            intArrayOf(Color.GREEN, colorPrimary, Color.WHITE),
            floatArrayOf(0f, 0.5f, 1f)
        )

        // Set the gradient shader to the paint
        paint.shader = gradient

        // Draw the double arc
        path.reset()
        val outerArc = RectF(30f, centerY - radius + 10f, width - 30f, centerY + radius)
        val innerArc = RectF(130f, centerY - radius + 110f, width - 130f, centerY + radius - 100f)
        path.arcTo(outerArc, 180f, 180f)
        path.arcTo(innerArc, 360f, -180f)
        canvas.drawPath(path, paint)

        // Draw text labels
        for (angle in 0..180 step 20) {
            val radians = Math.toRadians((180 - angle).toDouble())
            val x = centerX + (radius - 50f) * Math.cos(radians).toFloat()
            val y = centerY - (radius - 50f) * Math.sin(radians).toFloat()

//            //Draw Line
//            val lineLength = 30f
//            val startX = centerX + (radius - lineLength) * Math.cos(radians).toFloat()
//            val startY = centerY - (radius - lineLength) * Math.sin(radians).toFloat()
//            val endX = startX + lineLength * Math.cos(radians).toFloat()
//            val endY = startY - lineLength * Math.sin(radians).toFloat()
//            canvas.drawLine(startX, startY, endX, endY, textPaint)
            textPaint.typeface = typeface
            canvas.drawText(angle.toString(), x, y, textPaint)
        }

        // Draw dynamic speed line
        val speedRadians = Math.toRadians((180 - speed).toDouble())
        val speedLineLength = radius - 90f
        val speedStartX = centerX + (radius - 10f) * Math.cos(speedRadians).toFloat()
        val speedStartY = centerY - (radius - 10f) * Math.sin(speedRadians).toFloat()
        val speedEndX = centerX + speedLineLength * Math.cos(speedRadians).toFloat()
        val speedEndY = centerY - speedLineLength * Math.sin(speedRadians).toFloat()

        // Set the gradient shader to the speedPaint
        val speedGradient = SweepGradient(
            centerX, centerY,
            intArrayOf(Color.YELLOW, colorPrimary, Color.WHITE),
            floatArrayOf(0f, 0.5f, 1f)
        )

        val colorss = true
        if (colorss) {
            speedPaint.shader = speedGradient
        } else {
            speedPaint.color = Color.TRANSPARENT
        }

        // Draw the speed line with the arc
        canvas.drawLine(speedStartX, speedStartY, speedEndX, speedEndY, speedPaint)

        // Draw filled region up to the speed
        path.reset()
        path.arcTo(outerArc, 180f, speed)
        path.arcTo(innerArc, 180f + speed, -speed)
        path.close()
        fillPaint.shader = SweepGradient(
            centerX, centerY,
            intArrayOf(Color.GREEN, Color.YELLOW, Color.GREEN),
            floatArrayOf(0.2f, 0.3f, 1f)
        )
        canvas.drawPath(path, fillPaint)

        // Draw 12 separate arcs above the outer arc
        drawUpperArcs(canvas, centerX, centerY, radius, 9)


        // Draw concentric arcs made up of dots inside the inner arc
        val numArcs = 5  // Adjust the number of arcs as needed
        val dotRadius = 5f
        val dotDistance = 30f  // Adjust the distance between dots as needed

        for (i in 1..numArcs) {
            val arcRadius = radius - 110f - i * dotDistance  // Adjust the radius of each concentric arc
            val arcPaint = Paint(paint)  // Use the same paint properties as the existing arcs

            for (angle in 0..180 step 10) {
                val radians = Math.toRadians((180 - angle).toDouble())
                val x = centerX + arcRadius * Math.cos(radians).toFloat()
                val y = centerY - arcRadius * Math.sin(radians).toFloat()
                canvas.drawCircle(x, y, dotRadius, arcPaint)
            }
        }
    }

    private fun drawUpperArcs(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, count: Int) {
        // Calculate the angles for each of the 12 arcs with a gap
        val angleInterval = (180f - count) / count // Subtract the gap
        val startAngle = 180f

        // Draw the 12 separate arcs above the outer arc
        for (i in 0 until count) {
            val arcStartAngle = startAngle + i * (angleInterval + 1) // Add 1 for the gap
            val arcEndAngle = arcStartAngle + angleInterval

            val arcRect =  RectF(10f, centerY - radius - 10f, width - 10f, centerY + radius - 10f)
            canvas.drawArc(arcRect, arcStartAngle, arcEndAngle - arcStartAngle, false, speedPaint)
        }
    }
}

