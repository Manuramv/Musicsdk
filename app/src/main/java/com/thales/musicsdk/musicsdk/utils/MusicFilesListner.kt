package com.thales.musicsdk.musicsdk.utils

import com.thales.musicsdk.musicsdk.models.Song

interface MusicFilesListner {
    fun songLoaded(songs: List<Song>)
    fun onError(error: Throwable)
}

internal object ListnerConstant {
    lateinit var musicFilesListner :MusicFilesListner
}