package faizan.com.islamichandbook.splashscreen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import faizan.com.islamichandbook.MainActitivity.MainActivity;
import faizan.com.islamichandbook.R;

public class SplashScreen extends AppCompatActivity {

    ImageView imageViewSalam;
    Animation animFadein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageViewSalam = (ImageView) findViewById(R.id.imageViewSalam);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        imageViewSalam.startAnimation(animFadein);
    }


    @Override
    protected void onResume() {
        super.onResume();
        final MediaPlayer mediaPlayer=MediaPlayer.create(this,R.raw.greeting);
        mediaPlayer.start();
        Thread t = new Thread(){
            @Override
            public void run(){
                super.run();
                try{
                    sleep(5000);
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    Log.i("Abdul", "Audio Duration");
                    Intent i = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(i);

                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
