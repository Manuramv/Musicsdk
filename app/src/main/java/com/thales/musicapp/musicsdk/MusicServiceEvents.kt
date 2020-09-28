package com.thales.musicapp.musicsdk

interface MusicServiceEvents {

    fun startMusicService()
    fun stopMusicService()
    fun playMusic(id: Int)
    fun stopMusic(id: Int)
    fun resumeMusic(id: Int)

}