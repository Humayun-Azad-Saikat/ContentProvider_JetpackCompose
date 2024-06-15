package com.example.contentproviders_jetpack_compose

import android.Manifest
import android.content.ContentProvider
import android.content.ContentUris
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.example.contentproviders_jetpack_compose.ui.theme.ContentProviders_jetpack_composeTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    @get:RequiresApi(Build.VERSION_CODES.O)

    val imageViewModel by viewModels<ImageViewModel> ()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
            0
        )

        //for getting date and time
        val millisYesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR,-1)
        }.timeInMillis

        //for what we will show along with the image
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        //for query
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
        val selectionArgs = arrayOf(millisYesterday.toString())

        //content resolver
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use {curser->
            val idColumn = curser.getColumnIndex(MediaStore.Images.Media._ID)
            val nameColumn = curser.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

            val images = mutableListOf<Images>()
            while(curser.moveToNext()){
                val id = curser.getLong(idColumn)
                val name = curser.getString(nameColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                images.add(Images(id,name,uri))
            }
            imageViewModel.updateImage(images)

        }



        setContent {
            ContentProviders_jetpack_composeTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(imageViewModel.images){images->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(model = images.uri, contentDescription = null)
                                Text(text = images.name)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Images(
    val id:Long,
    val name:String,
    val uri:Uri
)



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ContentProviders_jetpack_composeTheme {

    }
}