package com.thales.musicsdk.musicsdk.utils

import android.content.Context
import android.provider.MediaStore
import com.thales.musicsdk.musicsdk.models.Song
import java.util.*

object FileUtils {

    /**
     * Method to get all the audio files from the device and adding the Name and file path to Data class
     */
    fun getAllAudios(c: Context): List<Song>? {
        val files: MutableList<Song> =
            ArrayList()
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME
        )
        val cursor = c.contentResolver
            .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        try {
            cursor!!.moveToFirst()
            do {
                files.add( Song(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                )))
            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return files
    }

    /**
     * Method to remove the file extension from file name.
     */
    fun getFileName(fileName: String): String? {
        if (fileName.indexOf(".") > 0)
            return fileName?.substring(0, fileName?.lastIndexOf("."));
        else return ""
    }
}