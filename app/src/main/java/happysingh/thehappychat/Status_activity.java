package happysingh.thehappychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Status_activity extends AppCompatActivity {

    Toolbar toolbar;
    EditText status;
    Button update;
    String Uid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_activity);

        // Setting Toolbar
        toolbar = (Toolbar)findViewById(R.id.status_tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        status = findViewById(R.id.et_status);
        update = (Button)findViewById(R.id.bt_status_submit);

        //Connecting For Auth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Uid = firebaseAuth.getCurrentUser().getUid();

        // Connecting FireBase Real Time Database
        FirebaseDatabase firebaseDatabase =     FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(Uid);

        // When User Clicks On Update Activity
        update.setOnClickListener(new View.OnClickListener() { // Updating Status On Update Click
            @Override
            public void onClick(View view) {

                String st = status.getText().toString().trim();
                if(TextUtils.isEmpty(st))
                {
                        status.setError("Status Cannot Be Blank !!!!");
                    //Toast.makeText(Status_activity.this, "Status Cannot Be Blanked", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Status_activity.this,"Your Status Updated",Toast.LENGTH_SHORT).show();
                    databaseReference.child("status").setValue(status.getText().toString());
                    Intent i = new Intent(Status_activity.this,SettingActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
