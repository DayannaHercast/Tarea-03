package com.example.tarea_03

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object SpotifyManager {
    fun openPlaylist(context: Context, playlistUri: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playlistUri)).apply {
                setPackage("com.spotify.music")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val fallbackUri = if (playlistUri.startsWith("spotify:playlist:")) {
                val playlistId = playlistUri.removePrefix("spotify:playlist:")
                "https://open.spotify.com/playlist/$playlistId"
            } else {
                "https://open.spotify.com/"
            }

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUri)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        }
    }
}
