package com.innoxapp.assignmentapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
class ImageAdapter(
    private val context: Context,
    private var articles: List<Article>
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private val memoryCache: LruCache<String, Bitmap> = LruCache(1024 * 1024 * 10) // 10MB cache
    private val diskCacheDir: File = context.cacheDir // Disk cache directory
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        fun bind(article: Article) {
            val imageUrl = "${article.thumbnail.domain}/${article.thumbnail.basePath}/0/${article.thumbnail.key}"
            val cachedBitmap = memoryCache.get(imageUrl)
            if (cachedBitmap != null) {
                imageView.setImageBitmap(cachedBitmap)
            } else {
                loadImage(imageUrl,imageView)
            }
        }
    }

    private fun loadImage(imageUrl: String, imageView: ImageView) {
        coroutineScope.launch {
            val bitmap = loadImageFromUrl(imageUrl)
            bitmap?.let {
                withContext(Dispatchers.Main) {
                    memoryCache.put(imageUrl, it)
                    imageView.setImageBitmap(it)
                }
            }
        }
    }

    private fun loadImageFromUrl(urlString: String): Bitmap? {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            return bitmap
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun updateData(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    fun clearMemoryCache() {
        memoryCache.evictAll()
    }
}
