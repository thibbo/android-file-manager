package fr.vigicorp.test_import;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Button captureButton;
    private Button importButton;
    private FloatingActionButton fullscreenButton;
    private ImageView thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureButton = findViewById(R.id.button_capture);
        importButton = findViewById(R.id.button_import);
        fullscreenButton = findViewById(R.id.button_fullscreen);
        thumbnail = findViewById(R.id.thumbnail);

        final Intent captureIntent = Tools.getCameraIntent(getApplicationContext());

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureIntent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 0) {
            if(scalePicture(Tools.mCurrentPhotoPath, thumbnail)) {
                fullscreenButton.setVisibility(View.VISIBLE);

                fullscreenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File(Tools.mCurrentPhotoPath);
                        Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "fr.vigicorp.test_import.provider", file);
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                        intent.setDataAndType(fileUri, "image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private boolean scalePicture(String mCurrentPhotoPath, ImageView thumbnail) {
        // Get the dimensions of the View
        int targetW = thumbnail.getWidth();
        int targetH = thumbnail.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        thumbnail.setImageBitmap(bitmap);

        return bitmap != null;
    }
}
