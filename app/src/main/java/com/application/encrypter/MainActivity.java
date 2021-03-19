package com.application.encrypter;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.application.encrypter.Utils.MyEncryption;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME_ENC = "GharPeSiksha_sample";
    private static final String FILE_NAME_DEC = "GharPeSiksha_sample_video.mp4";
    Button btn_encrypt, btn_decrypt;
    VideoView videoView;
    private File myDir;

    String my_key = "bNuulTlP4yziNTq9";
    String my_spec_key = "xR5Q5Rhuhz2UjuD0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_encrypt = findViewById(R.id.btn_encrypt);
        btn_decrypt = findViewById(R.id.btn_decrypt);

        videoView = findViewById(R.id.videoView);

        //Creating MediaController
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);

        myDir = new File(Environment.getExternalStorageDirectory().toString()+"/saved_videos");

        Dexter.withContext(this)
                .withPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        btn_encrypt.setEnabled(true);
                        btn_decrypt.setEnabled(true);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        Toast.makeText(MainActivity.this, "You must enable permission", Toast.LENGTH_SHORT).show();

                    }
                }).check();


        btn_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Uri uri = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");

                InputStream is = null;
                try {
                    is = new FileInputStream(Environment.getExternalStorageDirectory().toString()+"/DCIM/1.mp4");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                File outFileEnc = new File(myDir, FILE_NAME_ENC);
                try {
                    MyEncryption.encryptToFile(my_key, my_spec_key, is, new FileOutputStream(outFileEnc));
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        btn_decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outFileDec = new File(myDir, FILE_NAME_DEC);
                File encFile = new File(myDir, FILE_NAME_ENC);

                try {
                    MyEncryption.decryptToFile(my_key, my_spec_key, new FileInputStream(encFile), new FileOutputStream(outFileDec));
                    videoView.setMediaController(mediaController);
                    videoView.setVideoURI(Uri.fromFile(outFileDec));
                    videoView.requestFocus();
                    videoView.start();
                    outFileDec.delete();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}