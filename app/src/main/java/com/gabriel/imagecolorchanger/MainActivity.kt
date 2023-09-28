package com.gabriel.imagecolorchanger

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlin.math.abs

class MainActivity : AppCompatActivity() {



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // get image view
        val imageView = findViewById<ImageView>(R.id.imageView)
        val okButton = findViewById<Button>(R.id.button)
        val loadingTextView = findViewById<TextView>(R.id.loading_text_view)
        loadingTextView.visibility = TextView.GONE


        okButton.setOnClickListener {
            changeColor(imageView)
        }

        val color = Color.valueOf(1.0f, 0.0f, 0.0f)
        val img = readImageAndChangeColor(color, this)

        imageView.setImageBitmap(img)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun changeColor(imageView: ImageView) {
        val input = findViewById<TextView>(R.id.hex_input)
        val button = findViewById<Button>(R.id.button)
        val hexStr = input.text.toString()
        val number = hexStr.toInt(16)
        val red = number shr 16 and 0xFF
        val green = number shr 8 and 0xFF
        val blue = number and 0xFF

        val color = Color.valueOf(red / 255.0f, green / 255.0f, blue / 255.0f)
        button.setBackgroundColor(color.toArgb())

        val loadingTextView = findViewById<TextView>(R.id.loading_text_view)
        loadingTextView.visibility = TextView.VISIBLE



        val img = readImageAndChangeColor(color, this)

        imageView.setImageBitmap(img)


        loadingTextView.visibility = TextView.GONE

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun readImageAndChangeColor(replacementColor: Color, context: Context): Bitmap {
        val image = BitmapFactory.decodeStream(context.assets.open("boxGray.png"))
        val width = image.width
        val height = image.height
        val pixels = IntArray(width * height)
        image.getPixels(pixels, 0, width, 0, 0, width, height)


        val whiteColorTolerance = 0.85f
        val intensity = 1.2f

        for (i in pixels.indices) {
            val pixel = Color.valueOf(pixels[i])

            val currentColorRed = pixel.red()
            val currentColorGreen = pixel.green()
            val currentColorBlue = pixel.blue()

            if (currentColorRed >= 1.0f * whiteColorTolerance &&
                currentColorGreen >= 1.0f * whiteColorTolerance &&
                currentColorBlue >= 1.0f * whiteColorTolerance
            )
                continue


            val redDiff = replacementColor.red() * intensity - currentColorRed
            val greenDiff = replacementColor.green() * intensity - currentColorGreen
            val blueDiff = replacementColor.blue() * intensity - currentColorBlue

            Log.w(
                "MainActivity",
                "readImageAndChangeColor: redDiff: $redDiff, greenDiff: $greenDiff, blueDiff: $blueDiff"
            )
            Log.w(
                "MainActivity",
                "readImageAndChangeColor: currentColorRed: $currentColorRed, currentColorGreen: $currentColorGreen, currentColorBlue: $currentColorBlue"
            )


            val newRedColor = currentColorRed + currentColorRed * redDiff
            val newGreenColor = currentColorGreen + currentColorGreen * greenDiff
            val newBlueColor = currentColorBlue + currentColorBlue * blueDiff

            val newColor = Color.valueOf(
                newRedColor,
                newGreenColor,
                newBlueColor,
                pixel.alpha(),
            )

            pixels[i] = newColor.toArgb()


        }
        val newBitmap = image.copy(Bitmap.Config.ARGB_8888, true)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)


        return newBitmap
    }
}