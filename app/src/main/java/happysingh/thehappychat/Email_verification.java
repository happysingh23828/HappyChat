package happysingh.thehappychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Email_verification extends AppCompatActivity {

    Button verifyemail;
    String Name ;
    String Email;
    String Password;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    ImageView backImage;
    Button backButton;
    TextView email ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        FirebaseAuth.getInstance().signOut();
        backImage = findViewById(R.id.back_image);
        backButton = findViewById(R.id.back_button);
        email = findViewById(R.id.email);
        email.setText(getIntent().getStringExtra("email"));

        new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                {
                    Intent i = new Intent(Email_verification.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // fOR User Not Going To Previous Page
                    startActivity(i);
                    finish();
                }
            }
        };


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Email_verification.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Email_verification.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    public static void  sendDataToFirebase(String uid, String name, String email, String password , Boolean isEmailVerified) {

        //First Get Connection With FireBase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(uid);

        //Writing User Profile Data To Firebase
        databaseReference.child("name").setValue(name);
        databaseReference.child("status").setValue("Hii.... I am Using The Happy Chat Which Was Created By Happy Singh");
        databaseReference.child("email").setValue(email);
        databaseReference.child("password").setValue(password);
        databaseReference.child("image").setValue("https://www.shareicon.net/download/2015/09/18/103157_man_512x512.png");
        databaseReference.child("thumb_name").setValue("default");
        databaseReference.child("isemailverified").setValue(isEmailVerified);
    }
}
