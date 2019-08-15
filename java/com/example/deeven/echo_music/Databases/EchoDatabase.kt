package com.example.deeven.echo_music.Databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.deeven.echo_music.Songs

class EchoDatabase: SQLiteOpenHelper{
    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    )
    constructor(context: Context?) : super(
        context,
        Staticated.DB_NAME,
        null,
        Staticated.DB_version
    )
    object Staticated {
        var DB_version = 1
        val DB_NAME = "FavoriteDataBase"
        val TABLE_NAME = "FavoriteTable"
        val COLUMN_ID = "SongID"
        val COLUMN_SONG_TITLE = "SongTitle"
        val COLUMN_SONG_ARTIST = "SongArtist"
        val COLUMN_SONG_PATH = "SongPath"
    }
    var songList = ArrayList<Songs>()
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
        "CREATE TABLE " + Staticated.TABLE_NAME + "( " + Staticated.COLUMN_ID + " INTEGER," + Staticated.COLUMN_SONG_TITLE
                + " STRING," + Staticated.COLUMN_SONG_ARTIST + " STRING," + Staticated.COLUMN_SONG_PATH + " STRING);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun storeAsFavorites(id: Int?,Title: String?,artist: String?,Path: String?){
        val db = this.writableDatabase
        val contentvalues = ContentValues()
        contentvalues.put(Staticated.COLUMN_ID,id)
        contentvalues.put(Staticated.COLUMN_SONG_TITLE,Title)
        contentvalues.put(Staticated.COLUMN_SONG_ARTIST,artist)
        contentvalues.put(Staticated.COLUMN_SONG_PATH,Path)
        db.insert(Staticated.TABLE_NAME,null,contentvalues)
        db.close()
    }

    fun queryDBlist() : ArrayList<Songs>?{
        try {
            val db = this.readableDatabase
            val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
            var cSor = db.rawQuery(query_params, null)
            if (cSor.moveToFirst()) {
                do {
                    var _id = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _songTitle = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _songArtist = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _songPath = cSor.getString(cSor.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    songList.add(Songs(_id.toLong(), _songTitle, _songArtist, _songPath, 0))
                } while (cSor.moveToNext())
            } else {
                return null
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
        return songList
    }

    fun checkifIDexists (_id: Int) : Boolean{
        var storeID = -1090
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE SongID = '$_id'"
        var csor = db.rawQuery(query_params,null)
        if(csor.moveToFirst()){
            do {
                storeID = csor.getInt(csor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
            }while (csor.moveToNext())
        }
        else{
            return false
        }
        return storeID != -1090
    }

    fun deleteFavorite(_id: Int){
        val db = this.writableDatabase
        db.delete(Staticated.TABLE_NAME,Staticated.COLUMN_ID + " = " + _id, null)
        db.close()
    }

    fun checkSize(): Int{
        var count = 0
        var db = this.readableDatabase
        var query_params = "SELECT * FROM " + Staticated.TABLE_NAME
        var cSor = db.rawQuery(query_params,null)
        if(cSor.moveToFirst()){
            do {
                count += 1
            }while (cSor.moveToNext())
        }
        return count
    }
}