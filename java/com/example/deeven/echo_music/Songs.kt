package com.example.deeven.echo_music

import android.os.Parcel
import android.os.Parcelable

class Songs(var songID: Long,var songTitle: String,var artist: String,var songData: String,var dateAdded: Long):Parcelable{
    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }
    override fun describeContents(): Int {
        return 0
    }

    object Statified{
        var namecomparator: Comparator<Songs> = Comparator<Songs>{song1,song2->
            val songOne = song1.songTitle.toUpperCase()
            val songTwo = song2.songTitle.toUpperCase()
            songOne.compareTo(songTwo)
        }
        var datecomparator: Comparator<Songs> = Comparator<Songs>{song1,song2 ->
            val songOne = song1.dateAdded.toDouble()
            val songTwo = song2.dateAdded.toDouble()
            songOne.compareTo(songTwo)
        }
    }
}