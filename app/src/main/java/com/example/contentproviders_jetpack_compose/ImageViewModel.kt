package com.example.contentproviders_jetpack_compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

//viewModel for updating image list

class ImageViewModel:ViewModel() {

    var images by mutableStateOf(emptyList<Images>())
        private set

    fun updateImage(images: List<Images>){
        this.images = images
    }
}