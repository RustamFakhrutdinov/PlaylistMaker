package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private var editTextValue: String = NAME_DEF

    companion object {
        const val SEARCH_NAME = "TEXT_WATCHER_NAME"
        const val NAME_DEF = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val linearLayout = findViewById<LinearLayout>(R.id.container)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<Button>(R.id.clearIcon)
        val backButton = findViewById<Button>(R.id.arrowBack)

        inputEditText.setText(editTextValue)

        backButton.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                editTextValue = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(searchTextWatcher)




        val tracksArrayList: ArrayList<Track> = arrayListOf(
            Track("Yesterday (Remastered 2009)", "The Beatlesjhbgiusgbweogbwoebewgjuvavovboia", "2:55",
                "https://s3-alpha-sig.figma.com/img/c2df/63f0/b535f173143b2a1e1fc0c069482185b6?Expires=1717372800&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=DFRnsnHi8C4EoSmMfDKvEveay-pgTkV56bTLPt3UCA3SHksoQtYmPQKsxF3sahn35XwxNX14NGQLrb7Hd7mskAHz7j70cWJJN13JBxuP~Z~-tjhAJIn6eLQmSnq2oWXD1jTkBTE-7JJZTR8OMhVfomI27bOx3UdrlaZh3BG7dH8hgm8oahaUeziT1-T-95s6gQBa1ugmgF-4B23WgbEFCpUJI5EmMYsPImBtdK4IJfOzW6VkIIAGDs6LPdh8Z81RQofygvv49USg-oilVTM04lcb7Qe8GMaLsL4aNMY0xkn~UWGQyih~IFgpnsTv74Grwie4o5RGnqYoptm6PS9UQA__"),
            Track("Here Comes The Sun (Remastered 2009)jbefaibsdbkjs", "The Beatles", "4:01",
                "https://s3-alpha-sig.figma.com/img/88d4/998a/44813e71e0b476b0d3be316e151c9d8e?Expires=1717372800&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=djtSo993mB8IsickC7BZYIdzTRct1v3taJBZQa27PN3LZdsF0X46jZIEQgpU9bZDkv8u00VbGGkC1HRCmjmTvJbP7hODpefsadVxFi9p0xocElU4SuRxMf2LWvjZ7dmYjHu1-n1rma~MzT740CvCJms6mYJK3~NP2cSJkv6RZGZ65HZ0RU1JKqbZNcrFfmxnaN5wqkcJS5mdcEizzd4OC9AW8KtP6kl9lhxhxnplUInxVOjQmnWyLHPcnztvao1-GvzISGURcCCFs-lMVuF~7qUkUf6zM8wjWg7Mlx4yL1p~XpM7GS2qey2fNqtWEQpIPhonIinjXpUN~VVFiNAxnQ__"),
            Track("No Reply", "The Beatles", "5:12",
                "https://s3-alpha-sig.figma.com/img/d7b8/cd0b/608881a0d071e280c2c3559fdf879901?Expires=1717372800&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=YEIUEWnTfa4KlTjpW4XpjSuX~F57YEXnUDamCPwD7Or4jRaF7AH2wIJCgk1QHnQ1l~9iU~DypUqsyUBqmq0pW~NbBiVTelgDMYDrhptBmJG1WUpwevBLnTEa-xVv5Ey2-cjTvpH38v62Vo5~fYdKdO2ueJarXiTpHrs2ogstL5SqQ1TlAM9lR69Zzw2IlWr18WjuJNDuU-rImPOsfBCfE8jcz~iNQAe6eDQCRSV4xMz9Tk8d29ZwJQJkGwL~dMrQMWfkU03Iy5y1Kd2tst6Cp4eE-JyfbJt9R6Jc7hAtT-kJvUvvXNaF3xDTJ857vQKWJGDBsouRU9hpZwxGgNemrg__"),
            Track("Let It Be", "The Beatles", "6:01",
                "https://s3-alpha-sig.figma.com/img/d7b8/cd0b/608881a0d071e280c2c3559fdf879901?Expires=1717372800&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=YEIUEWnTfa4KlTjpW4XpjSuX~F57YEXnUDamCPwD7Or4jRaF7AH2wIJCgk1QHnQ1l~9iU~DypUqsyUBqmq0pW~NbBiVTelgDMYDrhptBmJG1WUpwevBLnTEa-xVv5Ey2-cjTvpH38v62Vo5~fYdKdO2ueJarXiTpHrs2ogstL5SqQ1TlAM9lR69Zzw2IlWr18WjuJNDuU-rImPOsfBCfE8jcz~iNQAe6eDQCRSV4xMz9Tk8d29ZwJQJkGwL~dMrQMWfkU03Iy5y1Kd2tst6Cp4eE-JyfbJt9R6Jc7hAtT-kJvUvvXNaF3xDTJ857vQKWJGDBsouRU9hpZwxGgNemrg__"),
            Track("Girl", "The Beatles", "4:11",
                "https://s3-alpha-sig.figma.com/img/d7b8/cd0b/608881a0d071e280c2c3559fdf879901?Expires=1717372800&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=YEIUEWnTfa4KlTjpW4XpjSuX~F57YEXnUDamCPwD7Or4jRaF7AH2wIJCgk1QHnQ1l~9iU~DypUqsyUBqmq0pW~NbBiVTelgDMYDrhptBmJG1WUpwevBLnTEa-xVv5Ey2-cjTvpH38v62Vo5~fYdKdO2ueJarXiTpHrs2ogstL5SqQ1TlAM9lR69Zzw2IlWr18WjuJNDuU-rImPOsfBCfE8jcz~iNQAe6eDQCRSV4xMz9Tk8d29ZwJQJkGwL~dMrQMWfkU03Iy5y1Kd2tst6Cp4eE-JyfbJt9R6Jc7hAtT-kJvUvvXNaF3xDTJ857vQKWJGDBsouRU9hpZwxGgNemrg__"),
            Track("Michelle", "The Beatles", "3:01",
                "https://s3-alpha-sig.figma.com/img/d7b8/cd0b/608881a0d071e280c2c3559fdf879901?Expires=1717372800&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=YEIUEWnTfa4KlTjpW4XpjSuX~F57YEXnUDamCPwD7Or4jRaF7AH2wIJCgk1QHnQ1l~9iU~DypUqsyUBqmq0pW~NbBiVTelgDMYDrhptBmJG1WUpwevBLnTEa-xVv5Ey2-cjTvpH38v62Vo5~fYdKdO2ueJarXiTpHrs2ogstL5SqQ1TlAM9lR69Zzw2IlWr18WjuJNDuU-rImPOsfBCfE8jcz~iNQAe6eDQCRSV4xMz9Tk8d29ZwJQJkGwL~dMrQMWfkU03Iy5y1Kd2tst6Cp4eE-JyfbJt9R6Jc7hAtT-kJvUvvXNaF3xDTJ857vQKWJGDBsouRU9hpZwxGgNemrg__"),
            Track("Eleanor Rigby", "The Beatles", "6:12",
                "https://s3-alpha-sig.figma.com/img/d7b8/cd0b/608881a0d071e280c2c3559fdf879901?Expires=1717372800&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=YEIUEWnTfa4KlTjpW4XpjSuX~F57YEXnUDamCPwD7Or4jRaF7AH2wIJCgk1QHnQ1l~9iU~DypUqsyUBqmq0pW~NbBiVTelgDMYDrhptBmJG1WUpwevBLnTEa-xVv5Ey2-cjTvpH38v62Vo5~fYdKdO2ueJarXiTpHrs2ogstL5SqQ1TlAM9lR69Zzw2IlWr18WjuJNDuU-rImPOsfBCfE8jcz~iNQAe6eDQCRSV4xMz9Tk8d29ZwJQJkGwL~dMrQMWfkU03Iy5y1Kd2tst6Cp4eE-JyfbJt9R6Jc7hAtT-kJvUvvXNaF3xDTJ857vQKWJGDBsouRU9hpZwxGgNemrg__"),
            Track("Come Together", "The Beatles", "4:09",
                "https://s3-alpha-sig.figma.com/img/d7b8/cd0b/608881a0d071e280c2c3559fdf879901?Expires=1717372800&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=YEIUEWnTfa4KlTjpW4XpjSuX~F57YEXnUDamCPwD7Or4jRaF7AH2wIJCgk1QHnQ1l~9iU~DypUqsyUBqmq0pW~NbBiVTelgDMYDrhptBmJG1WUpwevBLnTEa-xVv5Ey2-cjTvpH38v62Vo5~fYdKdO2ueJarXiTpHrs2ogstL5SqQ1TlAM9lR69Zzw2IlWr18WjuJNDuU-rImPOsfBCfE8jcz~iNQAe6eDQCRSV4xMz9Tk8d29ZwJQJkGwL~dMrQMWfkU03Iy5y1Kd2tst6Cp4eE-JyfbJt9R6Jc7hAtT-kJvUvvXNaF3xDTJ857vQKWJGDBsouRU9hpZwxGgNemrg__"),

            )


        val rvTrack = findViewById<RecyclerView>(R.id.rvTrack)
        val weatherAdapter = TrackAdapter(tracksArrayList)
        rvTrack.adapter = weatherAdapter

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_NAME, editTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextValue = savedInstanceState.getString(SEARCH_NAME, NAME_DEF)
    }

}