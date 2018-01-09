package happysingh.thehappychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        verifyemail = (Button)findViewById(R.id.verifyemail);

        //setting Toolbar
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.email_verify_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Email Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Email  = getIntent().getStringExtra("email");
        Password  = getIntent().getStringExtra("password");
        Name = getIntent().getStringExtra("name");
        verifyemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!firebaseUser.isEmailVerified())
                {

                    Toast.makeText(Email_verification.this,"User Registration Successfuly",Toast.LENGTH_SHORT).show();

                    String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Getting User Id Of Newly Registred Id
                    sendDataToFirebase(Uid,Name,Email,Password); // Function to Send Data To Firebase Storage

                    Intent i = new Intent(Email_verification.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // fOR User Not Going To Previous Page
                    startActivity(i);
                    finish();
                }

                else
                {
                    Toast.makeText(Email_verification.this,"You Not Clicked Yet On Verification Link",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void sendDataToFirebase(String uid, String name, String email, String password) {

        //First Get Connection With FireBase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(uid);

        //Writing User Profile Data To Firebase
        databaseReference.child("name").setValue(name);
        databaseReference.child("status").setValue("Hii.... I am Using The Happy Chat Which Was Created By Happy Singh");
        databaseReference.child("email").setValue(email);
        databaseReference.child("password").setValue(password);
        databaseReference.child("image").setValue("https://firebasestorage.googleapis.com/v0/b/happy-chat-8a939.appspot.com/o/default-avatar-250x250.png?alt=media&token=9c5928ad-04d2-4781-9db4-193de25d4461");
        databaseReference.child("thumb_name").setValue("default");
    }
}
