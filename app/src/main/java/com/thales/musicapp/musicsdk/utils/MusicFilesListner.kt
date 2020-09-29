package com.thales.musicapp.musicsdk.utils

import com.thales.musicapp.musicsdk.models.Song

interface MusicFilesListner {
    fun songLoaded(songs: List<Song>)
    fun onError(error: Throwable)
}

internal object ListnerConstant {
    lateinit var musicFilesListner :MusicFilesListner
}