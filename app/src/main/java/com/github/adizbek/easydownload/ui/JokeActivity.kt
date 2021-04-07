package com.github.adizbek.easydownload.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.adizbek.easydownload.R
import com.github.adizbek.easydownload.library.http.resource.SuccessResource
import com.github.adizbek.easydownload.library.ui.visibilitySwitch
import com.mikepenz.fastadapter.GenericFastAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_joke.*

@AndroidEntryPoint
class JokeActivity : AppCompatActivity() {

    private val vm: JokeViewModel by viewModels()

    private val jokeItems = GenericItemAdapter()
    private val jokeAdapter = GenericFastAdapter.with(jokeItems)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joke)

        joke_list.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = jokeAdapter
        }

        get_jokes_btn.setOnClickListener {
            vm.getJokes()
        }

        vm.randomJokes.observe(this) {
            if (it is SuccessResource) {
                jokeItems.clear().add(it.data)
            }
        }

        vm.randomJokesLoading.observe(this) { get_jokes_btn.isEnabled = !it }

        vm.randomJokesLoading.visibilitySwitch(this, loading_overlay, joke_list)
    }
}