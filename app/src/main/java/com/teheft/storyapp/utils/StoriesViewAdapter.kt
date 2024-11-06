package com.teheft.storyapp.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.teheft.storyapp.data.remote.response.ListStoryItem
import com.teheft.storyapp.databinding.ActivityLoginBinding
import com.teheft.storyapp.databinding.ItemListBinding
import com.teheft.storyapp.ui.detail.DetailActivity
import com.teheft.storyapp.utils.StoriesViewAdapter.MyViewHolder

class StoriesViewAdapter(
    private val context: Context
): ListAdapter<ListStoryItem, MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stories = getItem(position)
        holder.bind(context, stories)
    }

    class MyViewHolder(val binding: ItemListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(context: Context, stories: ListStoryItem){
            Log.d("photo", "${stories.photoUrl}")
            Glide.with(itemView.context).load(stories.photoUrl).into(binding.imgItem)
            binding.tvTitle.text = stories.name
            binding.tvItemDescription.text = stories.description
            itemView.setOnClickListener{
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("STORY_ID", stories.id)
                context.startActivity(intent)
            }
        }
    }

    companion object{
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStoryItem> =
            object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areContentsTheSame(
                    oldItem: ListStoryItem,
                    newItem: ListStoryItem
                ): Boolean {
                    return oldItem.description == newItem.description
                }

                override fun areItemsTheSame(
                    oldItem: ListStoryItem,
                    newItem: ListStoryItem
                ): Boolean {
                    return oldItem.id ==  newItem.id
                }
            }
    }
}