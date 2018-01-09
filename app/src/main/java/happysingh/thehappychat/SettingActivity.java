package happysingh.thehappychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    TextView name,status,email;
    CircleImageView profile;
    Button change_image,change_status;
    String Uid;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    private  static  final  int GALLERY_PICK=1;


    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        name = (TextView) findViewById(R.id.profile_name);
        status = (TextView) findViewById(R.id.profile_status);
        email = (TextView)findViewById(R.id.profile_email);
        profile = (CircleImageView)findViewById(R.id.profile_pic);
        change_image = (Button)findViewById(R.id.bt_profile_change_image);
        change_status = (Button)findViewById(R.id.bt_profile_change_status);

        // Setting Toolbar
        toolbar = (Toolbar)findViewById(R.id.profile_tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Getting Current User Unique Identity

        // Connecting Firefase To Fetch And Set Data To Objects;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(Uid);


        // Getting Information  Of  User Profile
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                try {
                    String Name = dataSnapshot.child("name").getValue().toString();
                    String Email = dataSnapshot.child("email").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();
                    String Status = dataSnapshot.child("status").getValue().toString();
                    name.setText(Name);
                    email.setText(Email);
                    status.setText(Status);


                    //
                    Picasso.with(SettingActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(profile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingActivity.this).load(image).into(profile);
                        }
                    });

                }
                catch (Exception e)
                {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Changing Status whwn user clicks update status button
        change_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i =new Intent(SettingActivity.this,Status_activity.class);
                startActivity(i);
            }
        });

        //When User Profile Picture Clicked  and Connecting FireBase Storage To Store Images
        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Picking Up A Image By Intent
                Intent image = new Intent();
                image.setType("image/*");
                image.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(image,"Select Image"),GALLERY_PICK);
            }
        });
    }

    @Override
    // Function Is Call When A Image Selected In The Galery Or By Intent
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK) // Check There Is Everuthing Fine Or Not
        {
            Uri imguri = data.getData(); // Getting Selected Image In Uri Form
            CropImage.activity(imguri)      // Starting Crop Function to Crop it
                    .setAspectRatio(1,1) // Setting Image Square Crop
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                // Setting Progressing Bar To Load data
                progressDialog = new ProgressDialog(SettingActivity.this);
                progressDialog.setTitle("Uploading....");
                progressDialog.setMessage("Please Wait While Uploading Your Profile Picture");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri(); // This Is Where We Got Our Crrooped Image

                FirebaseStorage  storage = FirebaseStorage.getInstance(); // Coonecting to database firebase
                StorageReference reference = storage.getReference().child("Profile_pictures"); // setting path where we have to go
                StorageReference file =  reference.child(Uid + ".jpg"); // setting path with name for new image

                file.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            String img_url = task.getResult().getDownloadUrl().toString(); // Converting image Uri To  Downloadable Image Url
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(Uid);

                            databaseReference.child("image").setValue(img_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this,"Your Profile Picture Updated ",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                            }
                        else
                        {
                            Toast.makeText(SettingActivity.this,"Error In Uploading ",Toast.LENGTH_SHORT).show();

                        }

                    }
                }); // Putting Data To FireBase
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        databaseReference.keepSynced(true);

        databaseReference.child("online").setValue("true");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        databaseReference.keepSynced(true);

        databaseReference.child("online").setValue("true");

    }
}
