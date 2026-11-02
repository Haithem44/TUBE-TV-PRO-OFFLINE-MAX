package com.tubetv.offline

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class VideoItem(val uri: Uri, val name: String)

class MainActivity : AppCompatActivity() {
    private val videos = mutableListOf<VideoItem>()
    private lateinit var adapter: VideoAdapter
    private lateinit var search: EditText

    private val pickVideos = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNullOrEmpty()) return@registerForActivityResult
        uris.forEach { uri ->
            try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            catch (_: Exception) { }
            val name = queryDisplayName(uri) ?: "فيديو"
            if (videos.none { it.uri == uri }) videos.add(VideoItem(uri, name))
        }
        adapter.submit(videos.toList())
        Toast.makeText(this, "تمت إضافة الفيديوهات: ${videos.size}", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(16,16,16)
        window.navigationBarColor = android.graphics.Color.rgb(16,16,16)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 12, 16, 8)
            setBackgroundColor(0xFF101010.toInt())
        }
        val header = TextView(this).apply {
            text = "▶ TUBE TV PRO OFFLINE MAX"
            textSize = 21f
            setTextColor(0xFFFF3333.toInt())
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 8, 0, 12)
        }
        search = EditText(this).apply {
            hint = "ابحث في فيديوهاتك..."
            setSingleLine(true)
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFFAAAAAA.toInt())
            setBackgroundColor(0xFF242424.toInt())
            setPadding(14, 8, 14, 8)
        }
        val add = Button(this).apply {
            text = "＋ إضافة فيديوهات من الهاتف"
            setOnClickListener { pickVideos.launch(arrayOf("video/*")) }
        }
        val info = TextView(this).apply {
            text = "فيديوهاتك المحلية • تعمل دون إنترنت"
            textSize = 13f
            setTextColor(0xFFBBBBBB.toInt())
            setPadding(2, 8, 2, 8)
        }
        val list = RecyclerView(this).apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            setBackgroundColor(0xFF101010.toInt())
        }
        adapter = VideoAdapter { item ->
            startActivity(Intent(this, PlayerActivity::class.java).setData(item.uri).putExtra("name", item.name))
        }
        list.adapter = adapter
        search.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.submit(videos.filter { it.name.contains(s.toString(), ignoreCase = true) })
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        root.addView(header)
        root.addView(search)
        root.addView(add)
        root.addView(info)
        root.addView(list, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun queryDisplayName(uri: Uri): String? {
        return try {
            contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                if (c.moveToFirst()) c.getString(0) else null
            }
        } catch (_: Exception) { null }
    }
}

private class VideoAdapter(private val onClick: (VideoItem) -> Unit) :
    RecyclerView.Adapter<VideoAdapter.Holder>() {
    private val items = mutableListOf<VideoItem>()
    fun submit(newItems: List<VideoItem>) { items.clear(); items.addAll(newItems); notifyDataSetChanged() }
    class Holder(val box: LinearLayout, val title: TextView, val play: TextView) : RecyclerView.ViewHolder(box)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): Holder {
        val box = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(10, 14, 10, 14)
            setBackgroundColor(0xFF222222.toInt())
        }
        val play = TextView(parent.context).apply {
            text = "▶"
            textSize = 38f
            gravity = Gravity.CENTER
            setTextColor(0xFFFF3333.toInt())
        }
        val title = TextView(parent.context).apply {
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
            maxLines = 2
            setPadding(0, 8, 0, 0)
        }
        box.addView(play, LinearLayout.LayoutParams(-1, 96))
        box.addView(title)
        return Holder(box, title, play)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.title.text = item.name
        holder.box.setOnClickListener { onClick(item) }
    }
    override fun getItemCount() = items.size
}
