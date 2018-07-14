package com.sumon.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sumon.map.helper.ImageProcessClass;
import com.binjar.prefsdroid.Preference;
import com.sumon.map.helper.SentInformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static java.lang.String.valueOf;

/**
 * Created by SumOn on 1/6/2018.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    String TAG = "MapActivity";
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    Button submitBtn;
    TextView title;
    ImageView closeBtn, image_preview;
    FloatingActionButton fabCall, fabCamera, fabGallery;
    Button leftIcon, rightIcon;
    EditText complainTextInput;
    Bitmap bitmap;
    ProgressDialog progressDialog ;

    public  static final int INTENT_CAPTURE_PHOTO  = 200;
    private static final int INTENT_GALLERY_ACCESS = 300;


    int id;
    String marketTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /*-------------> INITIAL ALL FIELD <----------------*/
        leftIcon = findViewById(R.id.title_bar_left_menu);
        rightIcon = findViewById(R.id.DetailsInfo);

        submitBtn = findViewById(R.id.submitBtn);
        title = findViewById(R.id.title);
        closeBtn = findViewById(R.id.closeBtn);
        complainTextInput = findViewById(R.id.complainField);
        fabCall = findViewById(R.id.fabCall);
        fabCamera = findViewById(R.id.fabCamera);
        image_preview = findViewById(R.id.image_preview);
        fabGallery = findViewById(R.id.fabGellary);

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),"Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        EnableRuntimePermissionToAccessCamera();

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:{

                        title.setText("স্বাগতম");
                        complainTextInput.getText().clear();
                        image_preview.setImageBitmap(null);
                        image_preview.setVisibility(View.GONE);

                    }
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        /*-------------> Handel onClick <----------------*/
        submitBtn.setOnClickListener(this);
        fabCamera.setOnClickListener(this);
        fabGallery.setOnClickListener(this);
        fabCall.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        rightIcon.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("bitmap", bitmap);
        outState.putString("title", marketTitle);
        outState.putInt("id", id);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        bitmap = savedInstanceState.getParcelable("bitmap");
        marketTitle = savedInstanceState.getString("title");
        id = savedInstanceState.getInt("id");
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        title.setText(marketTitle);

        Log.d(TAG, "onRestoreInstanceState: "+savedInstanceState.getString("title"));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        double[] latitude = {
                22.934536, 22.931789, 22.923853, 22.968639, 23.070244, 22.900419, 22.875069, 22.855167,
                22.830831, 22.866219, 22.865222, 22.851072, 22.884033, 22.916106, 22.969567, 22.907961,
                22.563185, 22.942181
        };

        double[] logitude = {
                89.368043, 89.365597, 89.357294, 89.364969, 89.278906, 89.350014, 89.352983, 89.351764,
                89.344775, 89.293256, 89.291367, 89.279656, 89.259553, 89.216631, 89.235367, 89.322139,
                89.133537, 89.226492
        };
        String[] title = {

                "ভবদাহ ৬-ভেন্ট রেগুলেটর - অভয়নগর - পায়রা - পায়রা",
                "ভবদাহ ২১-ভেন্ট রেগুলেটর - অভয়নগর - পায়রা - ভবদাহ",
                "ভবদাহ ৯-ভেন্ট রেগুলেটর - অভয়নগর - পায়রা - ভবদাহ",
                "ভবদাহ ৩-ভেন্ট রেগুলেটর - মনিরামপুর - মনোহরপুর - কাপালিয়া",
                "নবুয়ার খাল ১-ভেন্ট রেগুলেটর - মনিরামপুর - নেহালপুর - নবুয়ার খাল",
                "ঢাকুরিয়া ১৫-ভেন্ট রেগুলেটর - মনিরামপুর - ঢাকুরিয়া - ঢাকুরিয়া",
                "কানাইসিসা ৩-ভেন্ট রেগুলেটর - কেশবপুর - সুফলাকাঠি - কানাইসিসা",
                "বিলখুশিয়া ৮-ভেন্ট রেগুলেটর - কেশবপুর - সুফলাকাঠি - বিলখুশিয়া",
                "আগরহাটি ২-ভেন্ট রেগুলেটর - কেশবপুর - গৌরীঘনা - আগরহাটি",
                "কাশেমপুর ১-ভেন্ট রেগুলেটর - কেশবপুর - গৌরীঘনা - কাশেমপুর",
                "বুরুলি ৩-ভেন্ট রেগুলেটর - কেশবপুর - মঙ্গলকোট - বুরুলি",
                "পাত্রা ৩-ভেন্ট রেগুলেটর - কেশবপুর - মঙ্গলকোট - পাত্রা",
                "নুরানিয়া ৪-ভেন্ট রেগুলেটর - ডুমুরিয়া - আটলিয়া - নুরানিয়া",
                "গরালিয়া ৩-ভেন্ট রেগুলেটর - কেশবপুর - মঙ্গলকোট - বরেংগা",
                "খোজাখালি ৩-ভেন্ট রেগুলেটর - কেশবপুর - কেশবপুর - মদ্ধকুল",
                "জামলা ৩-ভেন্ট রেগুলেটর - মনিরামপুর - শ্যামকুর - জামলা",
                "কাটাখালি ৫-ভেন্ট রেগুলেটর - কেশবপুর - সুফলাকাঠি - কালিচরণপুর",
                "মদ্ধকুল ২-ভেন্ট রেগুলেটর - কেশবপুর - কেশবপুর - মদ্ধকুল"

        };

        for( int i=0; i<logitude.length; i++){

            // Add a marker in Sydney and move the camera
            LatLng PLACE = new LatLng(latitude[i], logitude[i]);
            googleMap.addMarker(new MarkerOptions()
                    .position(PLACE)
                    .title(title[i])
                    .snippet("")
                    .zIndex(1.0f)
                    .flat(false)

            ).setTag(i);

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(PLACE));
            //  LatLngBounds BOUND = new LatLngBounds(new LatLng(22.95, 89.25 ), new LatLng(22.97, 89.35));
            LatLngBounds BOUND = new LatLngBounds(new LatLng(22.90, 89.20 ), new LatLng(23, 89.40));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BOUND.getCenter(), 11));
            googleMap.setLatLngBoundsForCameraTarget(BOUND);
            // Set a preference for minimum and maximum zoom.
            googleMap.setMinZoomPreference(8.0f);
            googleMap.setMaxZoomPreference(16.0f);
            googleMap.setOnMarkerClickListener(this);
            googleMap.setMapType(4);
        }
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.hideInfoWindow();
        //  Toast.makeText(getBaseContext(), marker.getTitle(),Toast.LENGTH_SHORT).show();
        String id_r = marker.getId();
        id_r=id_r.substring(1);
        id=Integer.parseInt(id_r);
        id = id+1;
        //Log.e("T",String.format("value = %d", id+1));

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                marketTitle = marker.getTitle();
                title.setText(marketTitle);
                Log.d(TAG, "onMarkerClick: "+marketTitle);
        }
        //  Toast.makeText(getBaseContext(), valueOf( marker.getTag()),Toast.LENGTH_SHORT).show();

        return false;
    }
    /*-------------> Handel onClick <----------------*/
    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();
        switch (id) {
            case R.id.closeBtn:
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                title.setText("স্বাগতম");
                complainTextInput.getText().clear();
                image_preview.setImageBitmap(null);
                image_preview.setVisibility(View.GONE);
                break;
            case R.id.fabCamera:
                capturePhoto();
                break;
            case R.id.fabGellary:
                selectPhotoFromGallery();
                break;
            case R.id.fabCall:
                String phone = "042168996";
                intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
                Toast.makeText(MapsActivity.this, "call", Toast.LENGTH_SHORT).show();
                break;
            case R.id.submitBtn:

                if (complainTextInput.getText().toString().isEmpty()) {
                    Toast.makeText(MapsActivity.this,"অনুগ্রহ করে আপনার অভিযোগটি লিখুন!",Toast.LENGTH_SHORT).show();
                } else uploadToServer();

                break;
            case R.id.DetailsInfo:
                intent = new Intent(this, ScrollingActivity.class);
                startActivity(intent);
                break;

        }
    }
    /*-------------> CAPTURE PHOTO<----------------*/
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
                startActivityForResult(takePictureIntent, INTENT_CAPTURE_PHOTO);
            }
        }
    }
    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
       String mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    /*-------------> SELECT PHOTO FROM GALLERY <----------------*/
    private void selectPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), INTENT_GALLERY_ACCESS);

    }
    /*-------------> Star activity for result method to Set captured image <----------------*/

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == INTENT_CAPTURE_PHOTO && resultCode == RESULT_OK) {

            bitmap = (Bitmap) data.getExtras().get("data");
            image_preview.setVisibility(View.VISIBLE);
            image_preview.setImageBitmap(bitmap);
        }

        if (requestCode == INTENT_GALLERY_ACCESS && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                image_preview.setVisibility(View.VISIBLE);
                image_preview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    /*-------------> UPLOAD TO SERVER <----------------*/
    private void uploadToServer() {

        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gorilla);
        }
            ByteArrayOutputStream byteArrayOutputStreamObject ;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
            byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
           final String convertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);


        @SuppressLint("StaticFieldLeak")
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

          //  private String ServerUploadPath ="http://213.32.18.37/capture_img_upload_to_server.php" ;
          private String ServerUploadPath ="http://213.32.18.37/capture_img_upload_to_server.php" ;
            private String complainText = complainTextInput.getText().toString();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MapsActivity.this,"আপনার অভিযোগটি সংরক্ষন করা হচ্ছে","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);

                progressDialog.dismiss();
                Toast.makeText(MapsActivity.this,string1,Toast.LENGTH_LONG).show();
               // image_preview.setImageResource(android.R.color.transparent);
                complainTextInput.getText().clear();
                image_preview.setImageBitmap(null);
                image_preview.setVisibility(View.GONE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            protected String doInBackground(Void... params) {
               /* String username = Preference.getString(SentInformation.USERNAME);
                String number = Preference.getString(SentInformation.MOBILE_NUMBER);

                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put(ImageName, comaplinText);
                HashMapParams.put(ImagePath, ConvertImage);
                HashMapParams.put("id_r",valueOf(id));
                return imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);*/

                String username = Preference.getString(SentInformation.USERNAME);
                String number = Preference.getString(SentInformation.MOBILE_NUMBER);
                Log.e("T",valueOf(complainText));
                Log.e("T",complainText);
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put("complain", complainText);
                HashMapParams.put("image_path", convertImage);
                HashMapParams.put("id_r",valueOf(id));
                HashMapParams.put("name",username);
                HashMapParams.put("phoneNumber",number);
                return imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }
    /*-------------> Requesting runtime permission to access camera <----------------*/
    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                Manifest.permission.CAMERA))
        {
            // Printing toast message after enabling runtime permission.
            Toast.makeText(MapsActivity.this,"CAMERA permission allows us to Access CAMERA", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.CAMERA}, 100);
        }
    }
    @Override
    public void onRequestPermissionsResult(int RC, @NonNull String per[], @NonNull int[] PResult) {

        switch (RC) {

            case 100:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MapsActivity.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MapsActivity.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
