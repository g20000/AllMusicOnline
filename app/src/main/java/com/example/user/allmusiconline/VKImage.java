package com.example.user.allmusiconline;

import android.net.Uri;

public class VKImage {
    private Uri imageURL = null;

    VKImage(Uri name){
        this.imageURL = name;
    }

    Uri getNameSong(){
        return imageURL;
    }
}