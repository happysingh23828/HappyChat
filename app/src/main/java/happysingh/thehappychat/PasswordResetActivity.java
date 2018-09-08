package happysingh.thehappychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.support.design.widget.TextInputEditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import happysingh.thehappychat.Utils.Utils;

public class PasswordResetActivity extends AppCompatActivity {

    Button send;
    TextInputEditText email;
    ProgressDialog progressDialog1;
    RelativeLayout parent;
    ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        send = (Button) findViewById(R.id.send_password_rest);
        email = (TextInputEditText) findViewById(R.id.email_to_sent_reset_password);
        backImage = (ImageView) findViewById(R.id.back_image);
        parent = findViewById(R.id.parent);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(email.getText().toString())) {
                    Utils.snackBar(parent, "Enter Email");
                } else {
                    progressDialog1 = new ProgressDialog(PasswordResetActivity.this);
                    progressDialog1.setTitle("Sending....");
                    progressDialog1.setMessage("NOTE : Sometime Reset Email Come's Late ");
                    progressDialog1.show();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = email.getText().toString();

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog1.dismiss();
                                        email.setText("");
                                        Utils.snackBar(parent, "Reset Password Link Has been Sent to your email");
                                    } else {
                                        progressDialog1.dismiss();
                                        Utils.snackBar(parent, task.getException().getMessage());
                                    }
                                }
                            });
                }

            }
        });
    }
}
