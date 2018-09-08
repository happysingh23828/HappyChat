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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FloatingActionButton login;
    TextView reset,signup;
    TextInputEditText email,password;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog,progressDialog1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        login = (FloatingActionButton) findViewById(R.id.bt_login_login);
        email = (TextInputEditText) findViewById(R.id.et_login_email);
        password = (TextInputEditText)findViewById(R.id.et_login_password);
        reset = (TextView) findViewById(R.id.et_login_reset);
        signup = (TextView)findViewById(R.id.bt_signup_signup);

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
                    email.setError("enter email");
                }
                else if(TextUtils.isEmpty(password.getText().toString()))
                {
                    password.setError("enter password");
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

                Intent i = new Intent(LoginActivity.this,PasswordReset.class);
                 startActivity(i);
                finish();
            }
        });

    }

    private void login(TextInputEditText email, TextInputEditText password) {

        String Email = email.getText().toString();
        String Password = password.getText().toString();

        // Getting To Firebase Datebase Seeting Or Connectivity
        firebaseAuth = FirebaseAuth.getInstance();


        //fireBase Authentication Checking
        firebaseAuth.signInWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this,"Login Successfuly",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);// for user not going to previous page
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                progressDialog.cancel();
                                Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                    }
                });

    }
}
