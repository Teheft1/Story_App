package com.teheft.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.teheft.storyapp.data.remote.response.ListStoriesResponse
import com.teheft.storyapp.data.remote.response.ListStoryItem
import com.teheft.storyapp.data.remote.retrofit.ApiService

class StoryPagingSource(private val apiService: ApiService): PagingSource<Int, ListStoryItem>() {
    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }


    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let {anchorPosition ->
            val anchorPage =state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?:anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try{
            val position = params.key ?: INITIAL_PAGE_INDEX
            Log.d("paging","params load size: ${params.loadSize}")
            val responseData = apiService.getStories(position, params.loadSize)
            val storyData = responseData.listStory?.filterNotNull() ?: emptyList()
            LoadResult.Page(
                data = storyData,
                prevKey = if(position == INITIAL_PAGE_INDEX) null else position -1,
                nextKey = if(responseData.listStory.isNullOrEmpty()) null else position + 1
            )
        }catch (e: Exception){
            Log.d("source", "$e")
            return LoadResult.Error(e)
        }
    }
}