package happysingh.thehappychat.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

public class Utils {

    public static void  snackBar(View view,String message){

        Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
    }
}
