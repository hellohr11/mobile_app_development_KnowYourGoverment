package com.example.goverment;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity
        implements View.OnClickListener{
    private static final String TAG = "OfficialActivity";
    private TextView title;
    private TextView context;
    private TextView address;
    private TextView phone;
    private TextView email;
    private TextView url;
    private Official n;
    private ImageView imageView;
    private ImageView imageView1;
    private ImageButton youtube;
    private ImageButton facebook;
    private ImageButton twitter;
    private boolean flag = false;
    //int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_detail);
        title = findViewById(R.id.title);
        context = findViewById(R.id.name);

        imageView = findViewById(R.id.photoa);
        imageView1=findViewById(R.id.partya);
        address=findViewById(R.id.address);
        phone=findViewById(R.id.phone);
        email=findViewById(R.id.email);
        url=findViewById(R.id.website);
        facebook=findViewById(R.id.facebook);
        facebook.setVisibility(View.INVISIBLE);
        twitter=findViewById(R.id.twitter);
        twitter.setVisibility(View.INVISIBLE);
        youtube=findViewById(R.id.youtube);
        youtube.setVisibility(View.INVISIBLE);


        Intent intent = getIntent();
        flag=intent.hasExtra("name");
        if (flag) {
            n = (Official) intent.getSerializableExtra("name");
            if (n != null) {
                title.setText(n.getTitle());
                context.setText(n.getName());
                if(n.getPhotourl()!=null) {
                    loadRemoteImage(n.getPhotourl());
                }


                address.setText(n.getAddress().toString());
                if(n.getPhone()!=null) phone.setText("Phone: "+n.getPhone());
                if(n.getEmail()!=null) email.setText("Email: "+n.getEmail());
                if(n.getUrl()!=null) url.setText("Website: "+n.getUrl());

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

                if(n.getSc().getFacebookPageID()!=null){
                    facebook.setVisibility(View.VISIBLE);
                }
                if(n.getSc().getTwitterPageID()!=null){
                    twitter.setVisibility(View.VISIBLE);
                }
                if(n.getSc().getYoutubePageID()!=null){
                    youtube.setVisibility(View.VISIBLE);
                }


            }


        }
    }

    private void loadRemoteImage(final String imageURL) {
        // Needs gradle  implementation 'com.squareup.picasso:picasso:2.71828'

        final long start = System.currentTimeMillis(); // Used for timing

        Picasso.get().load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(imageView); // Use this if you don't want a callback

    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + n.getSc().getFacebookPageID();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                //newer versions of fb app
                urlToUse = "fb: //facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + n.getSc().getFacebookPageID();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }


    public void sentEmail(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:"
                + n.getEmail()
                + "?subject=" + "&body=" + "");
        intent.setData(data);
        startActivity(intent);

    }

    public void website(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(n.getUrl()));
        startActivity(browserIntent);
    }

    public void dial(View v){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+n.getPhone()));
        startActivity(intent);
    }

    public void map(View v){
        String map = "http://maps.google.co.in/maps?q=" + n.getAddress().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        startActivity(intent);
    }


    public void twitterClicked(View v) {
        Intent intent = null;
        String name = n.getSc().getTwitterPageID();
        try { // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void youTubeClicked(View v) {
        String name = n.getSc().getYoutubePageID();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void onClick(View v) {
        if(n.getPhotourl()!=null && n.getPhotourl().length()>0) {
            Log.d(TAG, "onClick: photoactivity");
            Intent intent2 = new Intent(this, PhotoActivity.class);
            intent2.putExtra("official", n);
            startActivity(intent2);
        }
    }
}



