package com.kivitool.captureimageandresize;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewImageActivity extends AppCompatActivity {

    ImageView imageView;
    Button download;
    FileOutputStream fileOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        imageView = findViewById(R.id.imageView);
        download = findViewById(R.id.download);

        imageView.setImageBitmap(MainActivity.bitmap);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File filePath = Environment.getExternalStorageDirectory();
                File dir = new File(filePath.getAbsolutePath() + "/task/");
                dir.mkdir();

                File file = new File(dir, System.currentTimeMillis() + ".jpg");

                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

                try {
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(ViewImageActivity.this, "Downloaded", Toast.LENGTH_SHORT).show();
                
            }
        });

    }
}