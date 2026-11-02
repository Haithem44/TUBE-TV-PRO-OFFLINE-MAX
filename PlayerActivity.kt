package com.tubetv.offline

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent.data
        if (uri == null) { finish(); return }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF101010.toInt())
        }
        val title = TextView(this).apply {
            text = intent.getStringExtra("name") ?: "تشغيل الفيديو"
            textSize = 18f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(16, 18, 16, 18)
        }
        val view = PlayerView(this).apply {
            useController = true
            layoutParams = LinearLayout.LayoutParams(-1, 0, 1f)
        }
        root.addView(title)
        root.addView(view, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        player = ExoPlayer.Builder(this).build().also {
            view.player = it
            it.setMediaItem(MediaItem.fromUri(uri))
            it.prepare()
            it.playWhenReady = true
        }
    }
    override fun onStop() { super.onStop(); player?.pause() }
    override fun onDestroy() { player?.release(); player = null; super.onDestroy() }
}
