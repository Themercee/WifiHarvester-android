package com.cosig.wifiharvester;

import android.content.Context;
import android.os.Environment;
import android.os.Vibrator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Jimmy on 2016-05-31.
 */
public class Debug {

    private Vibrator vib;
    private static boolean canVibrate;
    private static int lastTimeVib;
    private Context context;

    private static Debug instance = null;

    /*
    * Singleton Implementation
    */
    public static Debug getDebug(Context context) {
        if (instance == null) {
            instance = new Debug(context);
        }
        return instance;
    }

    public Debug(Context context){

        this.vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.canVibrate = true;
        this.lastTimeVib = 0;

    }

    public void vibrate(){
        Calendar now = Calendar.getInstance();
        int secondes = now.get(Calendar.SECOND);

        log("Debug","secondes: " + Integer.toString(secondes) + ", lastTimeVib: " + Integer.toString(lastTimeVib));

        if(canVibrate){
            log("Debug","canVibrate = true");
            if((secondes - lastTimeVib) > 10){ // 10 is a random time
                vib.vibrate(500);
                log("Debug","Has vibrate (if not ERROR!)");
                Calendar rightNow = Calendar.getInstance();
                lastTimeVib = rightNow.get(Calendar.SECOND);
            }
        }

    }

    public void log(String classe, String data){
        Calendar rightNow = Calendar.getInstance();
        String day = Integer.toString(rightNow.get(Calendar.DAY_OF_MONTH));
        String hour = Integer.toString(rightNow.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(rightNow.get(Calendar.MINUTE));
        String second = Integer.toString(rightNow.get(Calendar.SECOND));
        String millisec = Integer.toString(rightNow.get(Calendar.MILLISECOND));

        try {

            String dataToSaved = day + "d-" + hour + "h-" + minute + "m-" + second + "s-" + millisec + "milli ** " + classe + " : " + data + "\n";

            FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/debugWifiHarvester.json", true);
            fw.write(dataToSaved);
            fw.flush();
            fw.close();
        }
        catch (IOException e) {
            // f**ck, can't log the log -_-
        }
    }

    public void setCanVibrate(boolean canVibrate){
        this.canVibrate = canVibrate;
    }
}
