package com.example.trabalho1;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



import nl.dionsegijn.pixelate.Pixelate;


public class MainActivity extends AppCompatActivity  {

    private  static final int PERMISSION_CODE = 1000;
    Button mCaptureBtn;
    Button mConverter;
    ImageView mImageView;
    Bitmap bmp;
    Matrix matrix;
    String  cameraFilePath;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
                        /*|-----------------------------------|
                          |   permissão não ativada, solicite |
                          |-----------------------------------|*/
                        String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        /*|-------------------------------------------|
                          |  mostrar pop-up para solicitar permissões |
                          |-------------------------------------------|*/
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

        /*|------------------------|
          |      Button Conveter   |
        /*|------------------------|*/
        mConverter = findViewById(R.id.btnConveter);
        mConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Basta instanciar Pixelate, definir a densidade. Isso irá pixelizar toda a sua imagem.

                 new Pixelate(mImageView)
                        .setDensity(120)
                        .make();

                  try {

                    Bitmap bm = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                    Thread.sleep(2000);
                    galleryAddPic(bm);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                      e.printStackTrace();
                  }

                        //storeImage(bmp);
            }
        });
    }
    private void openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult( cameraIntent,0);
    }

    /* |-------------------------------------|
       |Resultado da permissão de tratamento|
       |------------------------------------|
     */
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
            /*  |-------------------------------|
                |Virar a Foto 90 graus          |
                |------------------------------ |*/
            matrix = new Matrix();
            matrix.postRotate(90);
            bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            return bmp;
        }catch(FileNotFoundException e){return null;}
    }

    //Salvar
     private void galleryAddPic(Bitmap bitmap )  throws PackageManager.NameNotFoundException {

        if (Build.VERSION.SDK_INT >= 23) {
             if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
             }
             else {
                 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
             }
         }

         String finalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
         File mypath = new File(finalPath,"profile.jpg");

         FileOutputStream fos = null;
         try {
             fos = new FileOutputStream(mypath);
             // Use the compress method on the BitMap object to write image to the OutputStream
             bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             try {
                 fos.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }

    }


}

//https://developer.android.com/training/camera/photobasics

//https://androidexample365.com/simple-android-library-to-pixelate-images-or-certain-areas-of-an-image/?fbclid=IwAR0xbOr8g0r1OjEJBDzHeeJAclYuv1Ss4M6LdESYbxKbud86CN7o-vXSRso