package com.example.ronaldfernandes.myminiproject;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView iv;
    private AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.animation);


        if (iv == null) throw new AssertionError();
        iv.setBackgroundResource(R.drawable.animation);
        animation = (AnimationDrawable) iv.getBackground();

        animation.setOneShot(true);
        final Thread y = new Thread() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    Intent n = new Intent(MainActivity.this ,MapsActivity.class);
                    startActivity(n);
                    finish();
                }

            }


        };
        y.start();
        animation.start();

    }
}
