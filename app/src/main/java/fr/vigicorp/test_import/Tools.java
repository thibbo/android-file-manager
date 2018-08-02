package fr.vigicorp.test_import;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Tools {
    public static String mCurrentPhotoPath;

    public static Intent getPickImageIntent(Context context, String chooserTitle) {

        Intent cameraIntent = null;
        if(!appManifestContainsPermission(context, "android.permission.CAMERA") || hasCameraAccess(context)) {
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        final Intent filePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        filePickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        filePickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerIntent.setType("image/*|application/pdf");

        final Intent chooserIntent = Intent.createChooser(filePickerIntent, chooserTitle);
        if (cameraIntent != null) {
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{cameraIntent});
        }

        return chooserIntent;
    }

    public static Intent getImportIntent(String type) {
        Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choosePictureIntent.setType(type);
        choosePictureIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        choosePictureIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        choosePictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        return choosePictureIntent;
    }

    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static Intent getCameraIntent(Context context) {
        Intent cameraIntent = null;
        if(!appManifestContainsPermission(context, "android.permission.CAMERA") || hasCameraAccess(context)) {
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(photoFile != null) {
                Uri mediaUri = FileProvider.getUriForFile(context, "fr.vigicorp.test_import.provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
//                cameraIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            }
        }
        return cameraIntent;
    }

    public static boolean appManifestContainsPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo e = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = null;
            if(e != null) {
                requestedPermissions = e.requestedPermissions;
            }

            if(requestedPermissions == null) {
                return false;
            }

            if(requestedPermissions.length > 0) {
                List requestedPermissionsList = Arrays.asList(requestedPermissions);
                return requestedPermissionsList.contains(permission);
            }
        } catch (PackageManager.NameNotFoundException var6) {
            var6.printStackTrace();
        }

        return false;
    }

    public static boolean hasCameraAccess(Context context) {
        return ContextCompat.checkSelfPermission(context, "android.permission.CAMERA") == 0;
    }
}
