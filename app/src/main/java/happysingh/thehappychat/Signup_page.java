package happysingh.thehappychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup_page extends AppCompatActivity {

    EditText name,email,password;
    Button signup,login;
    Toolbar tb;
    ProgressDialog  progressDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        // Getting Data Into  Java Objects For Further Use
        name = (EditText)findViewById(R.id.et_signup_name);
        email = (EditText)findViewById(R.id.et_signup_email);
        password = (EditText)findViewById(R.id.et_signup_password);
        signup = (Button)findViewById(R.id.bt_signup_signup);
        login = (Button)findViewById(R.id.bt_signup_login);



        // Setting ToolBar
        tb = (Toolbar)findViewById(R.id.signup_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Sign Up");                  // Setting Tile To ACtion BAr
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  /// This For Enabling To Go Back

        // When SignUp Button Clicked
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Setting Progress Bar


                if(TextUtils.isEmpty(email.getText().toString()))
                {
                    email.setError("enter email");
                }
                else if(TextUtils.isEmpty(name.getText().toString()))
                {
                    name.setError("Enter name");
                }
                else if(TextUtils.isEmpty(password.getText().toString()))
                {
                    password.setError("enter password");
                }
                else {
                    progressDialog = new ProgressDialog(Signup_page.this);
                    progressDialog.setTitle("Registering User.....");
                    progressDialog.setMessage("Please Wait While We Creating Your Account");
                    progressDialog.show();
                    Registration(name, email, password);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Signup_page.this,login_page.class);
                startActivity(i);
                finish();
            }
        });

    }

    public void Registration(final EditText name, final EditText email, final EditText password) {
      final   String Name = name.getText().toString();
      final   String Email = email.getText().toString();
      final   String Password = password.getText().toString();
      final   String Uid;
      final   String image;

        //Showing Progressing Dialog To Look And Feel Good To User :-)
        // We Have To Check validation Here But We Will Do It Further

        // Getting FireBase Authorization Access
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //sending data to firebase authentication data storage
        mAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Email_verification email_verification  = new Email_verification();
                                                email_verification.sendDataToFirebase(user.getUid(),Name,Email,Password,false);

                                                Intent i = new Intent(Signup_page.this,Email_verification.class);
                                                startActivity(i);
                                            }
                                            else
                                            {
                                                Toast.makeText(Signup_page.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            progressDialog.cancel();
                            Toast.makeText(Signup_page.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
