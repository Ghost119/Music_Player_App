package com.example.deeven.echo_music.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.deeven.echo_music.Adapters.MainScreenAdapter
import com.example.deeven.echo_music.R
import com.example.deeven.echo_music.Songs
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */

class MainScreenFragment : Fragment() {
    var _songsList: ArrayList<Songs>? = null
    var nowPlayingTab: RelativeLayout? = null
    var songTitle: TextView? = null
    var playPauseButton: ImageButton? = null
    var noSongs: RelativeLayout? = null
    var visibleLayout: RelativeLayout? = null
    var recyclerView: RecyclerView? = null
    var myActivity: Activity? = null
    var _mainScreenAdapter: MainScreenAdapter? = null
    var trackPosition: Int = 0

    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
        activity?.title = "All Songs"
        nowPlayingTab = view?.findViewById<RelativeLayout>(R.id.nowplayingtab)
        songTitle = view?.findViewById<TextView>(R.id.songTitleM)
        noSongs = view?.findViewById<RelativeLayout>(R.id.noSongs)
        visibleLayout = view?.findViewById<RelativeLayout>(R.id.visibleLayout)
        recyclerView = view?.findViewById<RecyclerView>(R.id.contentMain)
        playPauseButton = view?.findViewById<ImageButton>(R.id.playPauseButtonM)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        _songsList = getSongsFromPhone()
        val prefs = activity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending","true")
        val action_sort_recent = prefs?.getString("action_sort_recent","false")
        if(_songsList == null){
             visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        }
        else {
            _mainScreenAdapter = MainScreenAdapter(_songsList as ArrayList<Songs>, myActivity as Context)
            recyclerView?.layoutManager = LinearLayoutManager(myActivity as Context)
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = _mainScreenAdapter
        }
        if(_songsList != null){
            if(action_sort_ascending!!.equals("true",true)){
                Collections.sort(_songsList,Songs.Statified.namecomparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
            else if(action_sort_recent!!.equals("true",true)){
                Collections.sort(_songsList,Songs.Statified.datecomparator)
                _mainScreenAdapter?.notifyDataSetChanged()
            }
        }
        bottomBarSetup()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.action_sort_ascending) {
            val editor = myActivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending","true")
            editor?.putString("action_sort_recent","false")
            editor?.apply()
            if (_songsList != null) {
                Collections.sort(_songsList, Songs.Statified.namecomparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.action_sort_recent) {
            val editor = myActivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending","false")
            editor?.putString("action_sort_recent","true")
            editor?.apply()
            if (_songsList != null) {
                Collections.sort(_songsList, Songs.Statified.datecomparator)
            }
            _mainScreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = (context as Activity)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
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
            songTitle?.setText(NowPlayingFragment.Statified.currentSongHelper.songTitle1)
            NowPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
                songTitle?.setText(NowPlayingFragment.Statified.currentSongHelper.songTitle1)
                NowPlayingFragment.Staticated.onComplete()
            })
            if (NowPlayingFragment.Statified.currentSongHelper.isPlaying1 == true) {
                nowPlayingTab?.visibility = View.VISIBLE
            } else {
                nowPlayingTab?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler() {
        nowPlayingTab?.setOnClickListener({
            Statified.mediaPlayer = NowPlayingFragment.Statified.mediaPlayer
            val nowPlay = NowPlayingFragment()
            var args = Bundle()
            args.putString("songTitle", NowPlayingFragment.Statified.currentSongHelper.songTitle1)
            args.putString("songArtist", NowPlayingFragment.Statified.currentSongHelper.songArtist1)
            args.putString("path", NowPlayingFragment.Statified.currentSongHelper.songpath1)
            args.putInt("songId", NowPlayingFragment.Statified.currentSongHelper.songID1.toInt())
            args.putInt("songPosition", NowPlayingFragment.Statified.songPosition)
            args.putParcelableArrayList("songData", NowPlayingFragment.Statified.fetchSongs)
            args.putString("MainBottomBar", "success")
            nowPlay.arguments = args
            fragmentManager?.beginTransaction()?.replace(R.id.details_fragment, nowPlay)?.addToBackStack("SPF1")
                ?.commit()
        })
        playPauseButton?.setOnClickListener({
            if (NowPlayingFragment.Statified.currentSongHelper.isPlaying1 == true) {
                NowPlayingFragment.Statified.mediaPlayer?.pause()
                NowPlayingFragment.Statified.currentSongHelper.isPlaying1 = false
                var editorPlay = NowPlayingFragment.Statified.myActivity?.
                    getSharedPreferences(NowPlayingFragment.Staticated.MY_PREFS_ISPLAYING,Context.MODE_PRIVATE)?.edit()
                editorPlay?.putBoolean("feature",false)
                editorPlay?.apply()
                trackPosition = NowPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                NowPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                NowPlayingFragment.Statified.mediaPlayer?.start()
                NowPlayingFragment.Statified.currentSongHelper.isPlaying1 = true
                var editorPlay = NowPlayingFragment.Statified.myActivity?.
                    getSharedPreferences(NowPlayingFragment.Staticated.MY_PREFS_ISPLAYING,Context.MODE_PRIVATE)?.edit()
                editorPlay?.putBoolean("feature",true)
                editorPlay?.apply()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }
}
