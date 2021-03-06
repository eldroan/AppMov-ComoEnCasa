package com.google.codelabs.mdc.java.shrine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashScreenActivity extends AppCompatActivity {
    private static int timeout = 1000;
    TextView txt;
    CircleImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_splash_screen);
        txt=findViewById(R.id.text_splash);
        img=findViewById(R.id.img_splash);

//        Animation animation2 = AnimationUtils.loadAnimation(SplashScreenActivity.this,R.animator.splash_screen_animation);
//        img.startAnimation(animation);
       // txt.startAnimation(animation2);

        Animation animation = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.animator.splash_screen_animation2);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(700);
        img.startAnimation(animation);
        txt.startAnimation(animation);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.animator.splash_screen_animation,R.animator.splash_screen_animation);
            }
        },timeout);





    }

}
