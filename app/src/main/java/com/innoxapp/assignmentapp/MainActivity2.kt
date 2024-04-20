package com.innoxapp.assignmentapp
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity2 : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    private val articles = mutableListOf<Article>()
    private lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = Repository()
        viewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]
        viewModel.getArticles().observe(this) { articles ->
            articles?.let {
                adapter.updateData(it)
            }
        }

        val progressBar: ProgressBar = findViewById(R.id.progress_bar)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = ImageAdapter(this,articles)
        recyclerView.adapter = adapter



        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.VISIBLE
            }

            withContext(Dispatchers.Main) {
                viewModel.fetchArticles()
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE

            }
        }

    }
}


