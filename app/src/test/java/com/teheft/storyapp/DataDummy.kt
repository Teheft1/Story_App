package com.teheft.storyapp

import com.teheft.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem>{
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 1..100){
            val storyItem = ListStoryItem(
                id = i.toString(),
                name = "Story $i",
                description = "Desc $i",
                photoUrl = null,
                createdAt = "2024-11-17T00:00:00Z",
                lon = null,
                lat = null
            )
            items.add(storyItem)
        }
        return items
    }
}