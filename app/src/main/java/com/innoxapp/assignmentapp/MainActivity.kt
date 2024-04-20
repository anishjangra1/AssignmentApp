//package com.innoxapp.assignmentapp
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.os.Bundle
//import android.util.LruCache
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.ProgressBar
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import kotlinx.coroutines.*
//import org.json.JSONArray
//import java.io.File
//import java.io.FileInputStream
//import java.io.FileOutputStream
//import java.io.IOException
//import java.net.HttpURLConnection
//import java.net.URL
//
//class MainActivity : AppCompatActivity() {
//    private lateinit var viewModel: MainViewModel
////    private lateinit var adapter: ArticleAdapter
//
//    private val articles = mutableListOf<Article>()
//    private lateinit var adapter: ImageAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//
//        val progressBar: ProgressBar = findViewById(R.id.progress_bar)
//        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
//        recyclerView.layoutManager = GridLayoutManager(this, 3)
//
//        adapter = ImageAdapter(this, articles)
//        recyclerView.adapter = adapter
//
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main) {
//                progressBar.visibility = View.VISIBLE
//            }
//
//            val jsonString = fetchData()
//
//            withContext(Dispatchers.Main) {
//                if (jsonString != null) {
//                    val parsedArticles = parseJson(jsonString)
//                    articles.addAll(parsedArticles)
//                    adapter.notifyDataSetChanged()
//                    progressBar.visibility = View.GONE
//                } else {
//                    Toast.makeText(this@MainActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    private suspend fun fetchData(): String? {
//        return try {
//            val url = URL("https://acharyaprashant.org/api/v2/content/misc/media-coverages?limit=100")
//            val urlConnection = url.openConnection() as HttpURLConnection
//            urlConnection.requestMethod = "GET"
//            urlConnection.connectTimeout = 10000 // 10 seconds
//            urlConnection.readTimeout = 10000 // 10 seconds
//
//            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
//                val inputStream = urlConnection.inputStream
//                val jsonString = inputStream.bufferedReader().use { it.readText() }
//                jsonString
//            } else {
//                null
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    private suspend fun parseJson(jsonString: String): List<Article> {
//        val jsonArray = JSONArray(jsonString)
//        val articles = mutableListOf<Article>()
//        for (i in 0 until jsonArray.length()) {
//            val jsonObj = jsonArray.getJSONObject(i)
//            val id = jsonObj.getString("id")
//            val title = jsonObj.getString("title")
//            val thumbnailJson = jsonObj.getJSONObject("thumbnail")
//            val thumbnail = Thumbnail(
//                domain = thumbnailJson.getString("domain"),
//                basePath = thumbnailJson.getString("basePath"),
//                key = thumbnailJson.getString("key")
//            )
//            val coverageURL = jsonObj.getString("coverageURL")
//            val article = Article(id, title, thumbnail, coverageURL)
//            articles.add(article)
//        }
//        return articles
//    }
//}
//
////data class Article(
////    val id: String,
////    val title: String,
////    val thumbnail: Thumbnail,
////    val coverageURL: String
////)
////
////data class Thumbnail(
////    val domain: String,
////    val basePath: String,
////    val key: String
////)
//
//class ImageAdapter(
//    private val context: Context,
//    private val articles: List<Article>
//) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
//
//    private val memoryCache: LruCache<String, Bitmap> = LruCache(1024 * 1024 * 10) // 10MB cache
//    private val diskCacheDir: File = context.cacheDir // Disk cache directory
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_image, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val article = articles[position]
//        holder.bind(article)
//    }
//
//    override fun getItemCount(): Int {
//        return articles.size
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val imageView: ImageView = itemView.findViewById(R.id.image_view)
//
//        fun bind(article: Article) {
//            val imageUrl = "${article.thumbnail.domain}/${article.thumbnail.basePath}/0/${article.thumbnail.key}"
//            val cachedBitmap = memoryCache.get(imageUrl)
//            if (cachedBitmap != null) {
//                imageView.setImageBitmap(cachedBitmap)
//            } else {
//                CoroutineScope(Dispatchers.IO).launch {
//                    val bitmap = loadImage(imageUrl)
//                    withContext(Dispatchers.Main) {
//                        imageView.setImageBitmap(bitmap)
//                    }
//                }
//            }
//        }
//    }
//
//    private suspend fun loadImage(imageUrl: String): Bitmap? {
//        return withContext(Dispatchers.IO) {
//            val cachedBitmap = loadImageFromDiskCache(imageUrl)
//            if (cachedBitmap != null) {
//                cachedBitmap
//            } else {
//                val bitmap = loadImageFromUrl(imageUrl)
//                bitmap?.let { saveToMemoryCache(imageUrl, it) }
//                bitmap
//            }
//        }
//    }
//
//    private fun loadImageFromDiskCache(key: String): Bitmap? {
//        val file = File(diskCacheDir, key.hashCode().toString())
//        if (file.exists()) {
//            val inputStream = FileInputStream(file)
//            return BitmapFactory.decodeStream(inputStream)
//        }
//        return null
//    }
//
//    private fun loadImageFromUrl(urlString: String): Bitmap? {
//        try {
//            val url = URL(urlString)
//            val connection = url.openConnection() as HttpURLConnection
//            connection.doInput = true
//            connection.connect()
//            val inputStream = connection.inputStream
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            inputStream.close()
//            saveToDiskCache(urlString, bitmap)
//            return bitmap
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return null
//    }
//
//    private fun saveToMemoryCache(key: String, bitmap: Bitmap) {
//        memoryCache.put(key, bitmap)
//    }
//
//    private fun saveToDiskCache(key: String, bitmap: Bitmap) {
//        val file = File(diskCacheDir, key.hashCode().toString())
//        try {
//            val outputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//            outputStream.flush()
//            outputStream.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//}
