package com.example.deeven.echo_music.Adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.deeven.echo_music.R
import com.example.deeven.echo_music.Songs
import com.example.deeven.echo_music.fragments.NowPlayingFragment


class MainScreenAdapter(_arrayList : ArrayList<Songs>,_context: Context):
    RecyclerView.Adapter<MainScreenAdapter.MainViewHolder>(){
    var songList: ArrayList<Songs>?= null
    var mContext: Context?= null
    init {
        this.songList = _arrayList
        this.mContext = _context
    }
    override fun onBindViewHolder(p0: MainViewHolder, p1: Int) {
        val songObject = songList?.get(p1)
        p0.text1Get?.text = (songObject?.songTitle)
        p0.text2Get?.text = (songObject?.artist)
        p0.contentHolder?.setOnClickListener({
            val nowPlay = NowPlayingFragment()
            var args = Bundle()
            args.putString("songTitle",songObject?.songTitle)
            args.putString("songArtist", songObject?.artist)
            args.putString("path", songObject?.songData)
            args.putInt("songId", songObject?.songID?.toInt() as Int)
            args.putInt("songPosition", p1)
            args.putParcelableArrayList("songData", songList as ArrayList<Songs>)
            nowPlay.arguments = args
            (mContext as FragmentActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragment,nowPlay)
                .commit()
        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MainViewHolder {
        val itemView = LayoutInflater.from(p0.context)
            .inflate(R.layout.row_custom_mainscreen,p0,false)
        return MainViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if(songList == null){
            return 0
        }
        return (songList as ArrayList<Songs>).size
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var text1Get: TextView?= null
            var text2Get: TextView?= null
            var contentHolder: RelativeLayout?= null
            init {
                text1Get = itemView.findViewById<TextView>(R.id.trackTitle)
                text2Get = itemView.findViewById<TextView>(R.id.trackArtist)
                contentHolder = itemView.findViewById<RelativeLayout>(R.id.contentRow)
            }
    }

}