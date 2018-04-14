package happysingh.thehappychat;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Happy-Singh on 12/31/2017.
 */

public class HappyChat extends Application {
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        firebaseAuth  = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null)
             databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());

        if(!checkInternetConnection())
        {
            Toast.makeText(this,"Your Have No Internet Connection",Toast.LENGTH_SHORT).show();
        }


        // picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso picasso = builder.build();
        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);
        Picasso.setSingletonInstance(picasso);

        // Setting online false When User Disconnect from App or Internet
        if(firebaseAuth.getCurrentUser()!=null) {
            if(firebaseAuth.getCurrentUser().isEmailVerified())
            {
                databaseReference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
            }
        }

    }



    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }
}
