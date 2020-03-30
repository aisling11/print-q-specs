package com.q.argument;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private String q_base64 = "";
    private String q = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        completeInformation();
    }


    public void completeInformation() {
        int id1 = getResources().getIdentifier("deviceQ", "id", getPackageName());
        TextView deviceQ = (TextView) findViewById(id1);

        q_base64 = GenerateQ.generateQ(this);
        q = GenerateQ.getFilters(this);


        deviceQ.setText("Q base64 (coded): " + "\n" + "\n" + q_base64 + "\n" + "\n" + "\n"+ "Q (decoded): " + "\n" + "\n" + q + "\n" + "\n");


    }

    public void saveFile(View Qbutton) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {

            Toast.makeText(this, "External Storage is not available  or it's read only", Toast.LENGTH_SHORT).show();

        } else{

            try {

                File sdDir = Environment.getExternalStorageDirectory();
                String path = sdDir.getAbsolutePath();
                File sgDir = new File(path);
                if (!sgDir.exists()) {
                    sgDir.mkdirs();
                    sgDir.createNewFile();
                }

                FileWriter fw = new FileWriter(path + "/"+"Q_argument.txt");
                BufferedWriter out = new BufferedWriter(fw);
                out.write(q_base64 + "\n" + "\n" + q);
                out.close();


                TextView storage = (TextView) findViewById(R.id.storage);
                storage.setText("Saved to: " + path);

            } catch (IOException e) {

                //No permission to write on the SDCard or another Exception
                try {

                    File myExternalFile = new File(getExternalFilesDir("MyFileStorage"), "Q_Argument.txt");

                    OutputStream fos = new FileOutputStream(myExternalFile);

                    fos.write((q_base64 + "\n" + "\n" + q).getBytes());
                    fos.flush();
                    fos.close();

                    TextView storage = (TextView) findViewById(R.id.storage);
                    storage.setText("Saved to: " + myExternalFile.getPath());

                }
                catch (Exception ex2) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}