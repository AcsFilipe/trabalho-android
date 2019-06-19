package com.example.trabalho1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.PipedInputStream;

import nl.dionsegijn.pixelate.Pixelate;


public class MainActivity extends AppCompatActivity {

    private  static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    Button mCaptureBtn;
    Button mConverter;
    ImageView mImageView;
    Bitmap bmp;
    Matrix matrix;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.imageView);
        mCaptureBtn = findViewById(R.id.capture_image_btn);
        /*|-----------------------------------------------------------|
          |      Button Click                                         |
        /*|-----------------------------------------------------------|*/
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*|----------------------------------------|
                  | Se system os for> = Masrshmallow,      |
                  |solicite permissão de tempo de execução |
                  |----------------------------------------|*/

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED){
                        /*|------------------------------------------------------------|
                          |   permissão não ativada, solicite                    |
                          |------------------------------------------------------------|*/
                        String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        /*|------------------------------------------------------------|
                          |  mostrar pop-up para solicitar permissões                      |
                          |------------------------------------------------------------|*/
                        requestPermissions(permission,PERMISSION_CODE);
                    }else{
                        /*|-------------------------------|
                          | Permissao                     |
                          |-------------------------------|*/
                        openCamera();
                    }
                }
            }
        });

         /*|----------------------------------------------------------|
           |      Button Conveter                                     |
        /*|-----------------------------------------------------------|*/
        mConverter = findViewById(R.id.btnConveter);
        mConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new Pixelate(mImageView)
                        .setArea(400, 400, 400, 400)
                        .setDensity(120)
                        .make();

            }
        });
    }

    private void openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult( cameraIntent,0);
    }

    //Handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //o método é chamado quando o usuário pressiona Permitir Negar da permissão Solicitar pop-up
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length > 0 &&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else{
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
                Uri targetUri = data.getData();
                Bitmap bmp2 = bitmap(targetUri);
                mImageView.setImageBitmap(bmp2);
                //mImageView.refreshDrawableState();
        }
    }
    private Bitmap bitmap(Uri targetUri) {
        try {
            //Virar a imagem 90 graus e Foto
            matrix = new Matrix();
            matrix.postRotate(90);
            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            return bmp;
        }catch(FileNotFoundException e){return null;}
    }
}



//https://androidexample365.com/simple-android-library-to-pixelate-images-or-certain-areas-of-an-image/?fbclid=IwAR0xbOr8g0r1OjEJBDzHeeJAclYuv1Ss4M6LdESYbxKbud86CN7o-vXSRso