package happysingh.thehappychat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity {

    String userid;
    TextView name,status;
    CircleImageView image;
    Toolbar toolbar;
    Button sendRequest,deleteRequest;
    String current_status="not_friend";
    String current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();;
    DatabaseReference friend_request;
    DatabaseReference friend_data;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        // Setting ToolBar
        toolbar = (Toolbar)findViewById(R.id.profile_tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdView = findViewById(R.id.adViewprofile);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        userid = getIntent().getStringExtra("user_id");
        name = (TextView)findViewById(R.id.user_profile_name);
        status = (TextView)findViewById(R.id.user_profile_status);
        image = (CircleImageView)findViewById(R.id.user_profile_pic);
        sendRequest = (Button)findViewById(R.id.bt_user_send_request);
        deleteRequest = (Button)findViewById(R.id.bt_user_cancel_request);
        deleteRequest.setVisibility(View.INVISIBLE);
        deleteRequest.setEnabled(false);

        // Setting Progress Dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("PLease Wait While Loading Profile......");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Connecting Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(userid);

        databaseReference.keepSynced(true);



        // Getting selected user Profile Details
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String Name = dataSnapshot.child("name").getValue().toString();
                String Status = dataSnapshot.child("status").getValue().toString();
                final String Img = dataSnapshot.child("image").getValue().toString();

                name.setText(Name);
                status.setText(Status);
                Picasso.with(Profile_Activity.this).load(Img).networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(Profile_Activity.this).load(Img).into(image);
                    }
                });

                //__________________CHECKING IF ITS ALREADY A Friend__________________________

                friend_data = FirebaseDatabase.getInstance().getReference().child("friend_data");

                friend_data.child(current_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(userid))
                        {
                            current_status="friend";
                            sendRequest.setText("Unfriend This Person");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // ________________Friend Request Sent OR Received According Button Changes ___________________

                friend_request = FirebaseDatabase.getInstance().getReference("friend_request_data").child(current_uid);

                friend_request.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(userid))
                        {
                            String request_type = dataSnapshot.child(userid).child("req_type").getValue().toString();

                            if(request_type.equals("sent"))
                            {
                                current_status="req_sent";
                                sendRequest.setText("Cancel Friend Request");
                            }
                            else
                            {
                                current_status="req_received";
                                deleteRequest.setVisibility(View.VISIBLE);
                                deleteRequest.setEnabled(true);
                                sendRequest.setText("Accept Friend Request");
                            }
                        }
                        else {

                            FirebaseDatabase.getInstance().getReference().child("friend_data").child(current_uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(userid))
                                    {
                                        current_status="friend";
                                        sendRequest.setText("Unfriend This Person");
                                        deleteRequest.setVisibility(View.INVISIBLE);
                                        deleteRequest.setEnabled(false);
                                    }
                                    else {
                                        current_status="not_friend";
                                        sendRequest.setText("Send Friend Request");
                                        sendRequest.setEnabled(true);
                                        deleteRequest.setVisibility(View.GONE);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });





                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // When User Clicks On Send Request Button
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // ________________________________This Is First Date____________________________________
                if(current_status.equals("not_friend"))
                {
                    sendRequest.setEnabled(false);
                    friend_request = FirebaseDatabase.getInstance().getReference().child("friend_request_data");
                    friend_request.child(current_uid).child(userid).child("req_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                friend_request.child(userid).child(current_uid).child("req_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        current_status="req_sent";
                                       Toast.makeText(Profile_Activity.this,"Friend Request Sent",Toast.LENGTH_SHORT).show();
                                       sendRequest.setText("Cancel Friend Request");
                                       sendRequest.setEnabled(true);
                                    }
                                });
                            }
                        }
                    });
                }

                // _______________________________WHEN REQUEST SENT________________________________
                if(current_status.equals("req_sent"))
                {
                    sendRequest.setEnabled(false);
                    friend_request = FirebaseDatabase.getInstance().getReference().child("friend_request_data");
                    friend_request.child(current_uid).child(userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                friend_request.child(userid).child(current_uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        current_status="not_friend";
                                        Toast.makeText(Profile_Activity.this,"Friend Request Cancelled",Toast.LENGTH_SHORT).show();
                                        sendRequest.setText("Send Friend Request");
                                        sendRequest.setEnabled(true);

                                    }
                                });
                            }
                        }
                    });

                }

                // ____________________________WHEN RECIEVEING THE REQUEST_______________________________

                if(current_status.equals("req_received"))
                {
                   final String date = DateFormat.getLongDateFormat(Profile_Activity.this).format(new Date());

                    friend_data = FirebaseDatabase.getInstance().getReference().child("friend_data").child(current_uid);
                    friend_data.child(userid).child("date").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                friend_request = FirebaseDatabase.getInstance().getReference().child("friend_request_data");
                                friend_request.child(current_uid).child(userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful())
                                        {
                                            friend_request.child(userid).child(current_uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    current_status="friend";
                                                    sendRequest.setText("Unfriend This Person");
                                                    deleteRequest.setVisibility(View.INVISIBLE);
                                                    deleteRequest.setEnabled(false);
                                                    friend_data = FirebaseDatabase.getInstance().getReference().child("friend_data").child(userid);
                                                    friend_data.child(current_uid).child("date").setValue(date);
                                                    Toast.makeText(Profile_Activity.this,"Friend Request Accepted",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

                // _____________________ when Have Already In Friend list_____________________

                if(current_status.equals("friend"))
                {

                    friend_data = FirebaseDatabase.getInstance().getReference().child("friend_data");


                    friend_data.child(current_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(userid))
                            {
                                FirebaseDatabase.getInstance().getReference().child("messages").child(current_uid).child(userid).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("messages").child(userid).child(current_uid).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("chat").child(current_uid).child(userid).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("chat").child(userid).child(current_uid).removeValue();

                                friend_data.child(userid).child(current_uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        friend_data.child(current_uid).child(userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                current_status="not_friend";
                                                sendRequest.setText("Send Friend Request");
                                                Toast.makeText(Profile_Activity.this,"Unfriend Done",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        // deleting  received friend request
        deleteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                friend_request = FirebaseDatabase.getInstance().getReference().child("friend_request_data");
                friend_request.child(current_uid).child(userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            friend_request.child(userid).child(current_uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    current_status="not_friend";
                                    deleteRequest.setVisibility(View.INVISIBLE);
                                    deleteRequest.setEnabled(false);
                                    Toast.makeText(Profile_Activity.this,"Friend Request Deleted",Toast.LENGTH_SHORT).show();
                                    sendRequest.setText("Send Friend Request");
                                    sendRequest.setEnabled(true);

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    protected void onResume() {
        super.onResume();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        databaseReference.keepSynced(true);

        databaseReference.child("online").setValue("true");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        databaseReference.keepSynced(true);

        databaseReference.child("online").setValue("true");

    }
}
