package com.example.wander.ui.dashboard;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.wander.model.Post;
import com.google.firebase.storage.StorageReference;

public class PostLayout extends RelativeLayout {
    private TextView header;
    private ImageView postPic;

    private Post post;

    public PostLayout(Context context, @NonNull Post post) {
        super(context);
        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.setMargins(-1, 100, -1, -1);
        super.setLayoutParams(layout);
        postPic = new ImageView(context);

        ViewGroup.LayoutParams picLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800);
        postPic.layout(0, 100, 0, 0);
        postPic.setLayoutParams(picLayout);
        header = new TextView(context);
        this.post = post;
        header.setText(getHeaderText());
        header.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        header.setPadding(40, 20, 40, 0);
        header.setTextSize(20);
        loadImage();
        super.addView(header);
        super.addView(postPic);
    }

    private String getHeaderText() {
        return post.getGroupName();
    }

    private void loadImage() {
        Log.d("PostLayout", post.getImageURL().getPath());
        Glide.with(postPic).load(post.getImageURL()).into(postPic);
    }
}
