package com.mirrormind.ipsgroup.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mirrormind.ipsgroup.R;
import java.io.ByteArrayOutputStream;
import retrofit.ApiClient;
import retrofit.ApiInterface;

public class PhotoVideoRedirectActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_cam_submit;
    ImageView imgShow;
    VideoView videoView;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp_photo);

        imgShow = findViewById(R.id.imgShow);
        btn_cam_submit = findViewById(R.id.btn_cam_submit);
        videoView = findViewById(R.id.vidShow);
        btn_cam_submit.setOnClickListener(this);

        if (getIntent().getStringExtra("WHO").equalsIgnoreCase("Image")) {

            String message = getIntent().getStringExtra("PATH");

            Bitmap bm = BitmapFactory.decodeFile(message);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            Log.e("base64", encodedImage);

            imgShow.setVisibility(View.VISIBLE);

            Glide.with(PhotoVideoRedirectActivity.this)
                    .load(getIntent().getStringExtra("PATH"))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    }).placeholder(R.drawable.ic_photo_cont)
                    .into(imgShow);
        } else {

            videoView.setVisibility(View.VISIBLE);
            try {
                videoView.setMediaController(null);
                videoView.setVideoURI(Uri.parse(getIntent().getStringExtra("PATH")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {

                    videoView.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.start();
                }
            });
        }
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

    }
    @Override
    public void onBackPressed() {
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cam_submit:
                Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
