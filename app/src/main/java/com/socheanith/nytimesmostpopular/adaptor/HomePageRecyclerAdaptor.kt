package com.socheanith.nytimesmostpopular.adaptor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.provider.Settings
import android.service.media.MediaBrowserService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.socheanith.nytimesmostpopular.MainActivity
import com.socheanith.nytimesmostpopular.R
import com.socheanith.nytimesmostpopular.fragment.DetailFragment
import com.socheanith.nytimesmostpopular.model.News
import com.squareup.picasso.Picasso

class HomePageRecyclerAdaptor(val context: Context, val itemList: ArrayList<News>):RecyclerView.Adapter<HomePageRecyclerAdaptor.HomePageViewHolder>(){
    class HomePageViewHolder(view:View):RecyclerView.ViewHolder(view){
        val title:TextView = view.findViewById(R.id.txtTitle)
        val author:TextView = view.findViewById(R.id.txtAuthor)
        val date:TextView = view.findViewById(R.id.txtDate)
        val image:ImageView = view.findViewById(R.id.imgNews)
        val llcontent:LinearLayout = view.findViewById(R.id.llcontent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_single_row, parent, false)
        return HomePageViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomePageViewHolder, position: Int) {
        val news = itemList[position]
        holder.title.text = news.tile
        holder.author.text = news.author
        holder.date.text = news.date
        val url = news.url
        Picasso.get().load(news.uri).error(R.drawable.ic_circle).into(holder.image)
        holder.llcontent.setOnClickListener {
          Toast.makeText(context,"clicked on ${holder.title.text}",Toast.LENGTH_SHORT).show()
            val openUrl = Intent(android.content.Intent.ACTION_VIEW)
            openUrl.data = Uri.parse(url)
            context.startActivity(openUrl)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}