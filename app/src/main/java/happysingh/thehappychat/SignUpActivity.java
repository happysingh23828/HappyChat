package happysingh.thehappychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.support.design.widget.TextInputEditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import happysingh.thehappychat.Utils.Utils;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText name,email,password;
    FloatingActionButton signup;
    ProgressDialog  progressDialog;
    ImageView back;
    RelativeLayout parent;
    TextView emailOnSent,emailSentMessage;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        // Getting Data Into  Java Objects For Further Use
        name = (TextInputEditText)findViewById(R.id.et_signup_name);
        email = (TextInputEditText)findViewById(R.id.et_signup_email);
        password = (TextInputEditText)findViewById(R.id.et_signup_password);
        signup = (FloatingActionButton)findViewById(R.id.bt_signup_signup);
        back = (ImageView)findViewById(R.id.back_image);
        parent = findViewById(R.id.parent);
        emailOnSent = findViewById(R.id.email_sent);
        emailSentMessage = findViewById(R.id.email_sent_textview);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        // When SignUp Button Clicked
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Setting Progress Bar


                if(TextUtils.isEmpty(name.getText().toString()))
                {
                    Utils.snackBar(parent,"Enter Name");
                }
                else if(TextUtils.isEmpty(email.getText().toString()))
                {
                    Utils.snackBar(parent,"Enter Email");
                }
                else if(TextUtils.isEmpty(password.getText().toString()))
                {
                    Utils.snackBar(parent,"Enter Password");;
                }
                else {
                    progressDialog = new ProgressDialog(SignUpActivity.this);
                    progressDialog.setTitle("Registering User.....");
                    progressDialog.setMessage("Please Wait While We Creating Your Account");
                    progressDialog.show();
                    Registration(name, email, password);
                }
            }
        });

    }

    public void Registration(final TextInputEditText name, final TextInputEditText email, final TextInputEditText password) {
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
                                                progressDialog.dismiss();
                                                emailOnSent.setText(email.getText().toString());
                                                email.setText("");
                                                password.setText("");
                                                name.setText("");
                                                emailOnSent.setVisibility(View.VISIBLE);
                                                emailSentMessage.setVisibility(View.VISIBLE);
                                            }
                                            else
                                            {
                                                Utils.snackBar(parent,task.getException().getMessage());
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            progressDialog.cancel();
                            Utils.snackBar(parent,task.getException().getMessage());
                        }
                    }
                });
    }


}
