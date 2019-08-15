package com.example.deeven.echo_music.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.deeven.echo_music.Adapters.FavoritesAdapter
import com.example.deeven.echo_music.Databases.EchoDatabase
import com.example.deeven.echo_music.R
import com.example.deeven.echo_music.Songs

/**
 * A simple [Fragment] subclass.
 *
 */
class FavouritesFragment : Fragment() {
    var myActivity: Activity? = null
    var noFav: RelativeLayout? = null
    var recyclerView: RecyclerView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playpauseButton: ImageButton? = null
    var songtit: TextView? = null
    var trackPosition: Int = 0
    var favContent: EchoDatabase? = null
    var refreshList: ArrayList<Songs>? = null
    var getListFromDatabase: ArrayList<Songs>? = null
    var favoritesAdapter: FavoritesAdapter? = null
    var fetchListFromDevice: ArrayList<Songs> ?= null
    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        activity?.title = "Favorites"
        noFav = view?.findViewById(R.id.noFav)
        recyclerView = view?.findViewById(R.id.recyclerFav)
        nowPlayingBottomBar = view?.findViewById(R.id.nowplayingtab1)
        playpauseButton = view?.findViewById(R.id.playPauseButton1)
        songtit = view?.findViewById(R.id.songTitle1)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favContent = EchoDatabase(myActivity)
        display_favourites_by_searching()
        bottomBarSetup()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    fun getSongsFromPhone(): ArrayList<Songs> {
        var arraylist = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songID = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateAdded = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            while (songCursor.moveToNext()) {
                var currentID = songCursor.getLong(songID)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(artist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateAdded)
                arraylist.add(Songs(currentID, currentTitle, currentArtist, currentData, currentDate))
            }
        }
        return arraylist
    }

    fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            songtit?.setText(NowPlayingFragment.Statified.currentSongHelper.songTitle1)
            NowPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
                NowPlayingFragment.Staticated.onComplete()
                songtit?.setText(NowPlayingFragment.Statified.currentSongHelper.songTitle1)
            })
            if (NowPlayingFragment.Statified.currentSongHelper.isPlaying1 == true) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener({
            Statified.mediaPlayer = NowPlayingFragment.Statified.mediaPlayer
            val nowPlay = NowPlayingFragment()
            var args = Bundle()
            args.putString("songTitle", NowPlayingFragment.Statified.currentSongHelper.songTitle1)
            args.putString("songArtist", NowPlayingFragment.Statified.currentSongHelper.songArtist1)
            args.putString("path", NowPlayingFragment.Statified.currentSongHelper.songpath1)
            args.putInt("songId", NowPlayingFragment.Statified.currentSongHelper.songID1.toInt())
            args.putInt("songPosition", NowPlayingFragment.Statified.songPosition)
            args.putParcelableArrayList("songData", NowPlayingFragment.Statified.fetchSongs)
            args.putString("FavBottomBar", "success")
            nowPlay.arguments = args
            fragmentManager?.beginTransaction()?.replace(R.id.details_fragment, nowPlay)?.addToBackStack("SPF")
                ?.commit()
        })
        playpauseButton?.setOnClickListener({
            if (NowPlayingFragment.Statified.currentSongHelper.isPlaying1 == true) {
                NowPlayingFragment.Statified.mediaPlayer?.pause()
                NowPlayingFragment.Statified.currentSongHelper.isPlaying1 = false
                var editorPlay = NowPlayingFragment.Statified.myActivity?.
                    getSharedPreferences(NowPlayingFragment.Staticated.MY_PREFS_ISPLAYING,Context.MODE_PRIVATE)?.edit()
                editorPlay?.putBoolean("feature",false)
                editorPlay?.apply()
                trackPosition = NowPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playpauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                NowPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                NowPlayingFragment.Statified.mediaPlayer?.start()
                NowPlayingFragment.Statified.currentSongHelper.isPlaying1 = true
                var editorPlay = NowPlayingFragment.Statified.myActivity?.
                    getSharedPreferences(NowPlayingFragment.Staticated.MY_PREFS_ISPLAYING,Context.MODE_PRIVATE)?.edit()
                editorPlay?.putBoolean("feature",true)
                editorPlay?.apply()
                playpauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    fun display_favourites_by_searching() {
        if (favContent?.checkSize() as Int > 0) {
            refreshList = ArrayList()
            getListFromDatabase = favContent?.queryDBlist()
            fetchListFromDevice = getSongsFromPhone()
            if (fetchListFromDevice != null) {
                for (i in 0..fetchListFromDevice?.size as Int - 1) {
                    for (j in 0..getListFromDatabase?.size as Int - 1) {
                        if (fetchListFromDevice?.get(i)?.songID == getListFromDatabase?.get(j)?.songID) {
                            refreshList?.add((getListFromDatabase as ArrayList<Songs>)[j])
                        }
                    }
                }
            }
            if (refreshList == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFav?.visibility = View.VISIBLE
            } else {
                favoritesAdapter = FavoritesAdapter(refreshList as ArrayList<Songs>, myActivity as Context)
                recyclerView?.layoutManager = LinearLayoutManager(myActivity as Context)
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favoritesAdapter
                recyclerView?.setHasFixedSize(true)
            }
        } else {
            recyclerView?.visibility = View.INVISIBLE
            noFav?.visibility = View.VISIBLE
        }
    }
}
