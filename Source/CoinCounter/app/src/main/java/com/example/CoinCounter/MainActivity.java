package com.example.CoinCounter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Core;

import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.core.CvType;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ImageView img;
    Button button;
    Button buttonGalery;
    TextView textView;

    Double totalPesos = 0.0;

    private Bitmap selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        OpenCVLoader.initDebug();

        img = findViewById(R.id.img);
        button = findViewById(R.id.button);
        //buttonGalery = findViewById(R.id.buttonGaleria);
        textView = findViewById(R.id.txt);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 101);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 101);
            }
        });

        /*buttonGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 102);
            }
        });*/
    }

    public Bitmap detectarMonedas(Bitmap originalBitmap) {
        // Convertir Bitmap a Mat
        Mat imgMat = new Mat();
        Utils.bitmapToMat(originalBitmap, imgMat);

        // Aumentar el brillo de la imagen
        Mat imgProcesada = new Mat();
        imgMat.convertTo(imgProcesada, -1, 0.9, 70);

        // Aplicar desenfoque para reducir el ruido
        Imgproc.medianBlur(imgProcesada,
                imgProcesada,
                3);

        // Convertir a escala de grises
        Imgproc.cvtColor(imgProcesada, imgProcesada, Imgproc.COLOR_RGB2GRAY);

        // Aplicar la transformación de Hough para detectar círculos
        Mat circulos = new Mat();
        Imgproc.HoughCircles(
                imgProcesada,
                circulos,
                Imgproc.HOUGH_GRADIENT,
                1.0,
                imgProcesada.rows() / 8.0,
                100,
                30,
                2,
                50
        );

        // Crear un mapa para los valores de las monedas según su radio
        Map<Integer, Double> valoresMonedas = new HashMap<>();
        valoresMonedas.put(14, 50.0);
        valoresMonedas.put(17, 100.0);
        valoresMonedas.put(19, 200.0);
        valoresMonedas.put(21, 500.0);
        valoresMonedas.put(23, 1000.0);

        // Dibujar los círculos detectados y contar el valor total
        Bitmap outputBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(outputBitmap, imgMat);

        // Dibujar circulos
        for (int i = 0; i < circulos.cols(); i++) {
            double[] datos = circulos.get(0, i);
            Point centro = new Point(datos[0], datos[1]);
            int radio = (int) datos[2];
            System.out.println(radio);
            // Dibujar el borde del círculo
            Imgproc.circle(imgMat, centro, radio, new Scalar(0, 255, 0), 3);
            // Dibujar el centro del círculo
            Imgproc.circle(imgMat, centro, 3, new Scalar(0, 0, 255), 3);
        }

        double totalDinero = 0.0;
        // Contabilizar total dinero
        for (int i = 0; i < circulos.cols(); i++) {
            double[] datos = circulos.get(0, i);
            int radio = (int) datos[2];
            // Encontrar el valor de la moneda basado en su radio
            Double valor = null;
            for (Map.Entry<Integer, Double> entry : valoresMonedas.entrySet()) {
                if ( radio == entry.getKey() || radio == (entry.getKey()-1) ){
                    valor = entry.getValue();
                    break;
                }
            }
            if (valor != null) {
                totalDinero += valor;
            }
        }

        // Convertir Mat a Bitmap
        Utils.matToBitmap(imgMat, outputBitmap);
        totalPesos = totalDinero;

        return outputBitmap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Abrir Camara
        if (requestCode == 101) {
            Bitmap originalBitmap = (Bitmap) data.getExtras().get("data");
            // Guardar la imagen seleccionada para su posterior uso
            selectedImage = originalBitmap;
            img.setImageBitmap(detectarMonedas(originalBitmap));
            textView.setText("Total en pesos: " + totalPesos);
        }

        // Abrir Galeria
        /*if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
            //Bitmap originalBitmap = (Bitmap) data.getExtras().get("data");
            //img.setImageBitmap(filterNegative(originalBitmap));
            Uri selectedImageUri = data.getData();
            //img.setImageURI(selectedImageUri);
            //Bitmap bitmap = null;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                // Guardar la imagen seleccionada para su posterior uso
                selectedImage = bitmap;
                // Aquí puedes hacer lo que necesites con el Bitmap
                img.setImageBitmap(detectarMonedas(bitmap));
                textView.setText("Total en pesos: " + totalPesos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/
    }
}