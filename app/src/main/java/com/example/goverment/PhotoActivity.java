package com.example.goverment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {
    private static final String TAG = "PhotoActivity";
    private TextView title;
    private TextView context;
    private ImageView imageView;
    private ImageView imageView1;
    private Official n;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        title = findViewById(R.id.title);
        context = findViewById(R.id.name);
        Intent intent = getIntent();
        imageView=findViewById(R.id.photoa);
        imageView1=findViewById(R.id.partya);

        if(intent.hasExtra("official")){
            n = (Official) intent.getSerializableExtra("official");
            if (n != null) {
                title.setText(n.getTitle());
                context.setText(n.getName());
                if (n.getPhotourl() != null) {
                    Picasso.get().load(n.getPhotourl())
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(imageView);
                }
            }
            imageView1.setVisibility(View.VISIBLE);

            if(n.getParty().contains("Republican")){
                imageView1.setImageResource(R.drawable.rep_logo);
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            }else if(n.getParty().contains("Democratic")){
                imageView1.setImageResource(R.drawable.dem_logo);
                getWindow().getDecorView().setBackgroundColor(Color.RED);
            }else {
                imageView1.setVisibility(View.INVISIBLE);
                getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            }

        }
    }

    private void loadRemoteImage(final String imageURL) {
        // Needs gradle  implementation 'com.squareup.picasso:picasso:2.71828'

        Picasso.get().load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(imageView); // Use this if you don't want a callback

    }

}
