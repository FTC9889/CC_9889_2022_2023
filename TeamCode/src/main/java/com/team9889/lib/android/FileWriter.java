package com.team9889.lib.android;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by joshua9889 on 4/5/2018.
 *
 * Class to write data to a file.
 */

public class FileWriter {
    private java.io.FileWriter writer = null;

    public FileWriter(String filename){
        this.setup(filename);
    }

    private void setup(String filename) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/FIRST/saved_data");

            File file = new File(myDir, filename);

            // creates the file
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();

            // creates a FileWriter Object
            writer = new java.io.FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String object){
        try {
            writer.write(object + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(int priority, Object object){
        String printString = priority + " " + object;
        write(printString);
    }

    public void close(){
        try {
//            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
