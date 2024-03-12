package com.example.wander.ui.dashboard;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wander.model.Post;

public class PostLayout extends RelativeLayout {
    private TextView header;

    public PostLayout(Context context, Post post) {
        super(context);

    }
}
