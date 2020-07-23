package com.example.hasnasmarthome;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Variables
        Animation topAnim, bottomAnim;
        final ImageView image, title;
        final TextView slogan, made;

        //Hooks
        image = findViewById(R.id.imageView);
        title = findViewById(R.id.imageView2);
        slogan = findViewById(R.id.textView);
        made = findViewById(R.id.textView2);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Set animation to elements
        image.setAnimation(topAnim);
        title.setAnimation(topAnim);
        slogan.setAnimation(topAnim);
        made.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(){
                Intent intent = new Intent(MainActivity.this, Login.class);
                Pair[] pairs = new Pair[4];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(title, "logo_image");
                pairs[2] = new Pair<View, String>(slogan, "logo_text");
                pairs[3] = new Pair<View, String>(made, "logo_text");

                //wrap the call in API level 21 or higher
                if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    startActivity(intent,options.toBundle());
                }
            }
        }, SPLASH_SCREEN);
    }
}
