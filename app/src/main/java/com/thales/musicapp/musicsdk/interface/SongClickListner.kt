package com.thales.musicapp.musicsdk.`interface`

import com.thales.musicapp.musicsdk.models.Song

interface SongClickListner {
    fun onSongSelected(song :Song)
}