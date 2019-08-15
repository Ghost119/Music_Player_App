package com.example.deeven.echo_music.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.deeven.echo_music.R
import com.example.deeven.echo_music.activities.MainActivity
import com.example.deeven.echo_music.fragments.AboutUsFragment
import com.example.deeven.echo_music.fragments.FavouritesFragment
import com.example.deeven.echo_music.fragments.MainScreenFragment
import com.example.deeven.echo_music.fragments.SettingsFragment

class NavigationDrawerAdapter(_contentList: ArrayList<String>,_iconList: IntArray,_context: Context)
    : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>(){
    var contentList: ArrayList<String> ?= null
    var getimages: IntArray?=null
    var mContext: Context?=null
    init {
        this.contentList = _contentList
        this.getimages = _iconList
        this.mContext = _context
    }

    override fun onBindViewHolder(p0: NavViewHolder, p1: Int) {
        p0.iconGet?.setBackgroundResource(getimages?.get(p1) as Int)
        p0.textGet?.setText(contentList?.get(p1))
        p0.contentholder?.setOnClickListener({
            if(p1 == 0){
                val mainscreenfragment = MainScreenFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment,mainscreenfragment)
                    .commit()

            }
            else if(p1 == 1){
                val favouritesFragment = FavouritesFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment,favouritesFragment)
                    .commit()
            }
            else if(p1 == 2){
                val settingsFragment = SettingsFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment,settingsFragment)
                    .commit()
            }
            else{
                val aboutUsFragment = AboutUsFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment,aboutUsFragment)
                    .commit()
            }
            MainActivity.Statified.drawerLayout?.closeDrawers()
        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NavViewHolder {
        val itemView = LayoutInflater.from(p0.context)
            .inflate(R.layout.row_custom_nav_adapter,p0,false)
        return NavViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return (contentList as ArrayList).size
    }

    class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var iconGet: ImageView?=null
            var textGet: TextView?=null
            var contentholder: RelativeLayout?=null
            init {
                iconGet = itemView.findViewById(R.id.icon_navdrawer)
                textGet = itemView.findViewById(R.id.text_navdrawer)
                contentholder = itemView.findViewById(R.id.navigation_drawer_item_holder)
            }
    }

}