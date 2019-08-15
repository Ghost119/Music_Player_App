package com.example.deeven.echo_music.fragments

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.deeven.echo_music.CurrentSongHelper
import com.example.deeven.echo_music.Databases.EchoDatabase
import com.example.deeven.echo_music.R
import com.example.deeven.echo_music.Songs
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 *
 */
class NowPlayingFragment : Fragment(){

    object Statified{
        var myActivity: Activity?= null
        var mediaPlayer: MediaPlayer?= null
        var songPosition:Int = 0
        var startTimeText: TextView?= null
        var endTimeText: TextView?= null
        var playPauseButton: ImageButton?= null
        var playNextButton: ImageButton?= null
        var playPreviousButton: ImageButton?= null
        var loopButton: ImageButton?= null
        var shuffleButton: ImageButton?= null
        var songTitleText: TextView?= null
        var songArtistText: TextView?= null
        var seekBar: SeekBar?= null
        var fetchSongs: ArrayList<Songs>?= null
        var audioVisualization: AudioVisualization?= null
        var glView: GLAudioVisualizationView?= null
        var fab: ImageButton?= null
        var currentSongHelper = CurrentSongHelper()
        var favoriteContent: EchoDatabase ?= null
        var mSensor: SensorManager?= null
        var mSensorListner: SensorEventListener ?= null
        var updateSongTime = object : Runnable{
            override fun run() {
                val getcurrent = Statified.mediaPlayer?.currentPosition
                Statified.startTimeText?.setText(String.format("%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long)
                    ,TimeUnit.MILLISECONDS.toSeconds(getcurrent.toLong())
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent.toLong()))
                ))
                Statified.seekBar?.setProgress(getcurrent as Int)
                Handler().postDelayed(this,1000)
            }
        }
    }

    object Staticated{
        var MY_PREFS_Shuffle = "Prefer Shuffle"
        var MY_PREFS_Loop = "prefer Loop"
        var MY_PREFS_ISPLAYING = "Prefer isPlaying"
        var MY_PREFS_NAME = "Shake Next"
        fun playNext(check: String){
            if(check.equals("PlayNormal",true)){
                Statified.songPosition = Statified.songPosition + 1
            }
            else if(check.equals("PlayShuffle",true)){
                val randObj = Random()
                val randomPosition = randObj.nextInt(Statified.fetchSongs?.size as Int + 1)
                Statified.songPosition = randomPosition
            }
            if(Statified.songPosition == Statified.fetchSongs?.size){
                Statified.songPosition = 0
            }
            Statified.mediaPlayer?.reset()
            Statified.currentSongHelper.isLoop1 = false
            val nextSong = Statified.fetchSongs?.get(Statified.songPosition)
            Statified.currentSongHelper.songTitle1 = nextSong?.songTitle
            Statified.currentSongHelper.songArtist1 = nextSong?.artist
            Statified.currentSongHelper.songID1 = nextSong?.songID as Long
            Statified.currentSongHelper.songpath1 = nextSong.songData
            Statified.currentSongHelper.songPosition1 = Statified.songPosition
            updateTextView(Statified.currentSongHelper.songTitle1 as String,
                Statified.currentSongHelper.songArtist1 as String)
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity as Context, Uri.parse(nextSong.songData))
                Statified.mediaPlayer?.prepare()
            }catch (e:Exception){
                e.printStackTrace()
            }
            if(Statified.currentSongHelper.isPlaying1){
                Statified.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
                Statified.mediaPlayer?.start()
                processInformation(Statified.mediaPlayer as MediaPlayer)
            }
            else{
                Statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
            if(Statified.favoriteContent?.checkifIDexists(Statified.currentSongHelper.songID1.toInt()) as Boolean){
                Statified.fab?.setImageDrawable(ContextCompat.
                    getDrawable(Statified.myActivity as Context,R.drawable.favorite_on))
            }
            else{
                Statified.fab?.setImageDrawable(ContextCompat.
                    getDrawable(Statified.myActivity as Context,R.drawable.favorite_off))
            }
        }

        fun onComplete(){
            if(Statified.currentSongHelper.isShuffle1){
                Statified.currentSongHelper.isPlaying1 = true
                Staticated.playNext("PlayShuffle")
            }
            else{
                if(Statified.currentSongHelper.isLoop1){
                    Statified.mediaPlayer?.reset()
                    val nextSong = Statified.fetchSongs?.get(Statified.songPosition)
                    Statified.currentSongHelper.songTitle1 = nextSong?.songTitle
                    Statified.currentSongHelper.songArtist1 = nextSong?.artist
                    Statified.currentSongHelper.songpath1 = nextSong?.songData
                    Statified.currentSongHelper.songID1 = nextSong?.songID as Long
                    updateTextView(Statified.currentSongHelper.songTitle1 as String,
                        Statified.currentSongHelper.songArtist1 as String)
                    try{
                        Statified.mediaPlayer?.
                            setDataSource(Statified.myActivity as Context, Uri.parse(nextSong.songData))
                        Statified.mediaPlayer?.prepare()
                    }
                    catch (e: Exception){
                        e.printStackTrace()
                    }
                    Statified.mediaPlayer?.start()
                    processInformation(Statified.mediaPlayer as MediaPlayer)
                    Statified.currentSongHelper.isPlaying1 = true
                }
                else{
                    Staticated.playNext("PlayNormal")
                    Statified.currentSongHelper.isPlaying1 = true
                }
            }
            if(Statified.favoriteContent?.checkifIDexists(Statified.currentSongHelper.songID1.toInt()) as Boolean){
                Statified.fab?.setImageDrawable(ContextCompat.
                    getDrawable(Statified.myActivity as Context,R.drawable.favorite_on))
            }
            else{
                Statified.fab?.setImageDrawable(ContextCompat.
                    getDrawable(Statified.myActivity as Context,R.drawable.favorite_off))
            }
        }

        fun updateTextView(Title:String,Artist:String){
            var songTitleUpdated = Title
            var songArtistUpated = Artist
            if(songTitleUpdated.equals("<unknown>",true)){
                songTitleUpdated = "unknown"
            }
            if(songArtistUpated.equals("<unknown>",true)){
                songArtistUpated = "unknown"
            }
            Statified.songTitleText?.setText(songTitleUpdated)
            Statified.songArtistText?.setText(songArtistUpated)
        }

        fun processInformation(mediaPlayer: MediaPlayer){
            var finalTime = mediaPlayer.duration
            var startTime = mediaPlayer.currentPosition
            Statified.seekBar?.max = finalTime
            Statified.startTimeText?.setText(String.format("%d: %d",
                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())
                ,TimeUnit.MILLISECONDS.toSeconds(startTime.toLong())
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))
            ))
            Statified.endTimeText?.setText(String.format("%d: %d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())
                ,TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong())
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))
            ))
            Handler().postDelayed(Statified.updateSongTime,1000)
        }
    }
    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        setHasOptionsMenu(true)
        activity?.title = "Now Playing"
        Statified.startTimeText = view.findViewById(R.id.startTime)
        Statified.endTimeText = view.findViewById(R.id.endTime)
        Statified.playPauseButton = view.findViewById(R.id.playPauseButton)
        Statified.playNextButton = view.findViewById(R.id.nextButton)
        Statified.playPreviousButton = view.findViewById(R.id.previousButton)
        Statified.loopButton = view.findViewById(R.id.loopButton)
        Statified.shuffleButton = view.findViewById(R.id.shuffleButton)
        Statified.songTitleText = view.findViewById(R.id.songTitle)
        Statified.songArtistText = view.findViewById(R.id.songArtist)
        Statified.seekBar = view.findViewById(R.id.seekBar)
        Statified.glView = view.findViewById(R.id.visualizer_view)
        Statified.fab = view.findViewById(R.id.favouriteIcon)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization = Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = (context as Activity)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onPause() {
        Statified.audioVisualization?.onPause()
        super.onPause()
        Statified.mSensor?.unregisterListener(Statified.mSensorListner)
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
        Statified.mSensor?.registerListener(
            Statified.mSensorListner,Statified.mSensor?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroyView() {
        Statified.audioVisualization?.release()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensor = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.now_playing_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect ->{
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Statified.favoriteContent = EchoDatabase(Statified.myActivity)
        //Shared Preferences Play Icon
        val PreffPlaying = Statified.myActivity?.
            getSharedPreferences(Staticated.MY_PREFS_ISPLAYING,Context.MODE_PRIVATE)
        val isPlayingAllowed = PreffPlaying?.getBoolean("feature",true)
        if(isPlayingAllowed as Boolean){
            Statified.currentSongHelper.isPlaying1 = true
        }
        else{
            Statified.currentSongHelper.isPlaying1 = false
        }
        //Shared Preferences Loop Icon
        val PreffForLoop = Statified.myActivity?.
            getSharedPreferences(Staticated.MY_PREFS_Loop,Context.MODE_PRIVATE)
        val isLoopAllowed = PreffForLoop?.getBoolean("feature",false)
        if(isLoopAllowed as Boolean){
            Statified.currentSongHelper.isLoop1 = true
            Statified.currentSongHelper.isShuffle1 = false
            Statified.shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopButton?.setBackgroundResource(R.drawable.loop_icon)
        }
        else{
            Statified.currentSongHelper.isLoop1 = false
            Statified.loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        //Shared Preferences Shuffle Icon
        val PreffForShuffle = Statified.myActivity?.
            getSharedPreferences(Staticated.MY_PREFS_Shuffle,Context.MODE_PRIVATE)
        val isShuffleAllowed = PreffForShuffle?.getBoolean("feature",false)
        if(isShuffleAllowed as Boolean){
            Statified.currentSongHelper.isShuffle1 = true
            Statified.currentSongHelper.isLoop1 = false
            Statified.shuffleButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        else{
            Statified.currentSongHelper.isShuffle1 = false
            Statified.shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var songTitle: String?= null
        var songArtist:String?= null
        var path: String?= null
        var songID: Long?= null
        try{
            path = arguments?.getString("path")
            songTitle = arguments?.getString("songTitle")
            songArtist = arguments?.getString("songArtist")
            Statified.songPosition = arguments?.getInt("songPosition") as Int
            songID = arguments?.getInt("songId")?.toLong()
            Statified.fetchSongs = arguments?.getParcelableArrayList("songData")
            Statified.currentSongHelper.songpath1 = path
            Statified.currentSongHelper.songArtist1 = songArtist
            Statified.currentSongHelper.songTitle1 = songTitle
            Statified.currentSongHelper.songID1 = songID as Long
            Statified.currentSongHelper.songPosition1 = Statified.songPosition
            Staticated.updateTextView(Statified.currentSongHelper.songTitle1 as String,
                Statified.currentSongHelper.songArtist1 as String)

        }catch (e: Exception){
            e.printStackTrace()
        }
        val FavoriteBottomBar = arguments?.get("FavBottomBar") as? String
        val MainBottomBar = arguments?.get("MainBottomBar") as? String
        if(FavoriteBottomBar != null){
            Statified.mediaPlayer = FavouritesFragment.Statified.mediaPlayer
        }
        else if(MainBottomBar != null){
            Statified.mediaPlayer = MainScreenFragment.Statified.mediaPlayer
        }
        else {
         /*   if(Statified.currentSongHelper.isPlaying1 == true){
                Statified.mediaPlayer?.reset()
            }*/
            Statified.mediaPlayer?.reset()
            Statified.mediaPlayer = MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity as Context, Uri.parse(path))
                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Statified.mediaPlayer?.start()
            Statified.currentSongHelper.isPlaying1 = true
            var editorPlay = Statified.myActivity?.
                getSharedPreferences(Staticated.MY_PREFS_ISPLAYING,Context.MODE_PRIVATE)?.edit()
            editorPlay?.putBoolean("feature",true)
            editorPlay?.apply()
        }
        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        //Setting isPlaying
        if(Statified.currentSongHelper.isPlaying1){
            Statified.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
        }
        else{
            Statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
        }
        //Click Handler
        clickHandler()
        //On Complete Listener
        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onComplete()
        }
        //Visualization
        val visualizationHandler = DbmHandler.
            Factory.newVisualizerHandler(Statified.myActivity as Context,0)
        Statified.audioVisualization?.linkTo(visualizationHandler)
        //Favorite Icon
        if(Statified.favoriteContent?.checkifIDexists(Statified.currentSongHelper.songID1.toInt()) as Boolean){
            Statified.fab?.setImageDrawable(ContextCompat.
                getDrawable(Statified.myActivity as Context,R.drawable.favorite_on))
        }
        else{
            Statified.fab?.setImageDrawable(ContextCompat.
                getDrawable(Statified.myActivity as Context,R.drawable.favorite_off))
        }
    }
    fun clickHandler(){
        Statified.fab?.setOnClickListener({
            if(Statified.favoriteContent?.checkifIDexists(Statified.currentSongHelper.songID1.toInt()) as Boolean){
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity as Context,
                    R.drawable.favorite_off))
                Statified.favoriteContent?.deleteFavorite(Statified.currentSongHelper.songID1.toInt())
                Toast.makeText(Statified.myActivity,"Removed from Favorites",Toast.LENGTH_SHORT).show()
            }
            else{
                Statified.fab?.setImageDrawable(ContextCompat.
                    getDrawable(Statified.myActivity as Context,R.drawable.favorite_on))
                Statified.favoriteContent?.storeAsFavorites(Statified.currentSongHelper.songID1.toInt(),
                    Statified.currentSongHelper.songTitle1 as String
                    ,Statified.currentSongHelper.songArtist1 as String,Statified.currentSongHelper.songpath1 as String)
                Toast.makeText(Statified.myActivity,"Added to Favorites",Toast.LENGTH_SHORT).show()
            }
        })
        Statified.playPauseButton?.setOnClickListener({
            if(Statified.currentSongHelper.isPlaying1){
                Statified.mediaPlayer?.pause()
                Statified.currentSongHelper.isPlaying1 = false
                Statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
            else{
                Statified.mediaPlayer?.start()
                Statified.currentSongHelper.isPlaying1 = true
                Statified.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
        Statified.playNextButton?.setOnClickListener({
            if (Statified.currentSongHelper.isLoop1){
                Statified.loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            if(Statified.currentSongHelper.isShuffle1){
                Staticated.playNext("PlayShuffle")
            }
            else{
                Staticated.playNext("PlayNormal")
            }
        })
        Statified.playPreviousButton?.setOnClickListener({
            if (Statified.currentSongHelper.isLoop1){
                Statified.loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })
        Statified.loopButton?.setOnClickListener({
            val editorShuffle = Statified.myActivity?.
                getSharedPreferences(Staticated.MY_PREFS_Shuffle,Context.MODE_PRIVATE)?.edit()
            val editorLoop = Statified.myActivity?.
                getSharedPreferences(Staticated.MY_PREFS_Loop,Context.MODE_PRIVATE)?.edit()
            if(Statified.currentSongHelper.isLoop1){
                Statified.currentSongHelper.isLoop1 = false
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
                Statified.loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            else{
                Statified.currentSongHelper.isLoop1 = true
                Statified.currentSongHelper.isShuffle1 = false
                Statified.loopButton?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
            }
        })
        Statified.shuffleButton?.setOnClickListener({
            val editorShuffle = Statified.myActivity?.
                getSharedPreferences(Staticated.MY_PREFS_Shuffle,Context.MODE_PRIVATE)?.edit()
            val editorLoop = Statified.myActivity?.
                getSharedPreferences(Staticated.MY_PREFS_Loop,Context.MODE_PRIVATE)?.edit()
            if(Statified.currentSongHelper.isShuffle1){
                Statified.currentSongHelper.isShuffle1 = false
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
                Statified.shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            }
            else{
                Statified.currentSongHelper.isShuffle1 = true
                Statified.currentSongHelper.isLoop1 = false
                Statified.shuffleButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature",true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            }
        })
    }

    fun playPrevious(){
        Statified.songPosition = Statified.songPosition - 1
        if(Statified.songPosition == -1){
            Statified.songPosition = 0
        }
        Statified.currentSongHelper.isLoop1 = false
        val nextSong = Statified.fetchSongs?.get(Statified.songPosition)
        Statified.currentSongHelper.songTitle1 = nextSong?.songTitle
        Statified.currentSongHelper.songArtist1 = nextSong?.artist
        Statified.currentSongHelper.songID1 = nextSong?.songID as Long
        Statified.currentSongHelper.songpath1 = nextSong.songData
        Statified.currentSongHelper.songPosition1 = Statified.songPosition
        Staticated.updateTextView(Statified.currentSongHelper.songTitle1 as String,
            Statified.currentSongHelper.songArtist1 as String
        )
        Statified.mediaPlayer?.reset()
        try {
            Statified.mediaPlayer?.setDataSource(Statified.myActivity as Context, Uri.parse(nextSong.songData))
            Statified.mediaPlayer?.prepare()
        }catch (e:Exception){
            e.printStackTrace()
        }
        if(Statified.currentSongHelper.isPlaying1){
            Statified.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            Statified.mediaPlayer?.start()
            Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        }
        else{
            Statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
        }
        if(Statified.favoriteContent?.checkifIDexists(Statified.currentSongHelper.songID1.toInt()) as Boolean){
            Statified.fab?.setImageDrawable(ContextCompat.
                getDrawable(Statified.myActivity as Context,R.drawable.favorite_on))
        }
        else{
            Statified.fab?.setImageDrawable(ContextCompat.
                getDrawable(Statified.myActivity as Context,R.drawable.favorite_off))
        }
    }

    fun bindShakeListener(){
        Statified.mSensorListner = object : SensorEventListener{
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt((x*x + y*y + z*z).toDouble()).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration*0.9f + delta
                if(mAcceleration  > 12){
                    val prefs = Statified.myActivity?.
                        getSharedPreferences(SettingsFragment.Statified.MY_PREFS_NAME,Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature",false)
                    if(isAllowed as Boolean){
                    Staticated.playNext("PlayNormal")}
                }
            }
        }
    }

}
