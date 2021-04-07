package com.github.adizbek.easydownload.api.data

import android.view.View
import com.github.adizbek.easydownload.R
import com.google.gson.annotations.SerializedName
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.joke_item.view.*

data class Joke(
    val id: Int,

    @SerializedName("type")
    val jokeType: String,

    @SerializedName("setup")
    val jokeText: String,

    @SerializedName("punchline")
    val description: String
) : AbstractItem<Joke.VH>() {

    class VH(private val view: View) : FastAdapter.ViewHolder<Joke>(view) {
        override fun bindView(item: Joke, payloads: List<Any>) {
            view.apply {
                tv_joke.text = item.jokeText
                tv_meaning.text = item.description
            }
        }

        override fun unbindView(item: Joke) {
        }
    }

    override val layoutRes: Int
        get() = R.layout.joke_item

    override fun getViewHolder(v: View): VH = VH(v)

    override val type: Int
        get() = 0
}