package happysingh.thehappychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import happysingh.thehappychat.Utils.Utils;

public class LoginActivity extends AppCompatActivity {

    FloatingActionButton login;
    TextView reset,signup;
    TextInputEditText email,password;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog,progressDialog1;
    RelativeLayout parent;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        login = (FloatingActionButton) findViewById(R.id.bt_login_login);
        email = (TextInputEditText) findViewById(R.id.et_login_email);
        password = (TextInputEditText)findViewById(R.id.et_login_password);
        reset = (TextView) findViewById(R.id.et_login_reset);
        signup = (TextView)findViewById(R.id.bt_signup_signup);
        parent = findViewById(R.id.parent);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        // Checking Authentication When User Clicks On Login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(email.getText().toString()))
                {
                    Utils.snackBar(parent,"Enter Email");
                }
                else if(TextUtils.isEmpty(password.getText().toString()))
                {
                    Utils.snackBar(parent,"Enter Password");
                }
                else
                 {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setTitle("Logging.....");
                    progressDialog.setMessage("Please Wait While Logging !");
                    progressDialog.show();

                    login(email, password);
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this,PasswordResetActivity.class);
                 startActivity(i);
            }
        });

    }

    private void login(TextInputEditText email, TextInputEditText password) {

        String Email = email.getText().toString();
        String Password = password.getText().toString();

        // Getting To FireBase DateBase Setting Or Connectivity
        firebaseAuth = FirebaseAuth.getInstance();


        //fireBase Authentication Checking
        firebaseAuth.signInWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                final FirebaseUser user= firebaseAuth.getCurrentUser();
                                if(!user.isEmailVerified())
                                {
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Intent i = new Intent(LoginActivity.this,Email_verification.class);
                                                        i.putExtra("email",user.getEmail().toString());
                                                        startActivity(i);
                                                        finish();
                                                    }
                                                    progressDialog.dismiss();

                                                }
                                            });
                                } else {
                                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("isemailverified").setValue(true);
                                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("online").setValue("true");
                                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);// for user not going to previous page
                                    startActivity(i);
                                    finish();
                                }
                                progressDialog.dismiss();

                            }
                            else
                            {
                                progressDialog.cancel();
                                Utils.snackBar(parent, Objects.requireNonNull(task.getException()).getMessage());
                            }
                    }
                });

    }
}
