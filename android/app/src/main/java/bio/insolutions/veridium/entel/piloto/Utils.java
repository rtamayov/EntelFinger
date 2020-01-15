package bio.insolutions.veridium.entel.piloto;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
    public static void  saveWSQ(Context context, String data, String nombreArchivo) {

        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/", nombreArchivo + ".txt");

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }
        Toast.makeText(context, "File Saved",
                Toast.LENGTH_SHORT).show();

    }
}
