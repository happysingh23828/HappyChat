package happysingh.thehappychat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.internal.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreen extends AppCompatActivity {

    Toolbar toolbar;
    String current_user,user_name,img,uid;
    TextView name,lastseen;
    CircleImageView image;
    DatabaseReference databaseReference,rootref,chatcov;
    FirebaseAuth firebaseAuth;
    ImageView send_msg,attach;
    EditText message;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    LinearLayoutManager mLayoutManager;
    private  static  final  int GALLERY_PICK=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);


        send_msg = (ImageView)findViewById(R.id.send);
        attach = (ImageView) findViewById(R.id.attach_file);
        message = (EditText) findViewById(R.id.msg);
        recyclerView = (RecyclerView)findViewById(R.id.single_chat_recyclerView);


        current_user = getIntent().getStringExtra("user_id");
        user_name = getIntent().getStringExtra("user_name");
        img = getIntent().getStringExtra("img");

        //Setting FireBase Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);
        rootref = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();

        databaseReference.keepSynced(true);


        // Setting Own Custome Bar On The Top
        toolbar = (Toolbar)findViewById(R.id.chatToolBaar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custome_bar,null);
        actionBar.setCustomView(action_bar_view);

        // Setting Custome Bar Data
        name = (TextView)findViewById(R.id.bar_name);
        image =(CircleImageView)findViewById(R.id.custom_bar_profile);
        lastseen = (TextView)findViewById(R.id.bar_seen);
        name.setText(user_name);
        Picasso.with(this).load(img).into(image);

        databaseReference.keepSynced(true); // for offline

        // Going To Load All The Stuff Related To User ....
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("online")) {
                    String online = dataSnapshot.child("online").getValue().toString();
                    if (online.equals("true")) {
                        lastseen.setText("online");
                    } else {  // Converting Firebase Timestamp to A Time Ago String
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        Long last_time = Long.parseLong(online);
                        String last_seen = getTimeAgo.getTimeAgo(last_time, getApplicationContext());
                        lastseen.setText(last_seen);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Creating First Time DataBase For Chat Activity
        rootref.child("chat").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(current_user))
                {


                    Map addValue = new HashMap();
                    addValue.put("seen",false);
                    addValue.put("message","");
                    addValue.put("type","");
                    addValue.put("timestamp", ServerValue.TIMESTAMP);


                    Map chatUservalue = new HashMap();
                    chatUservalue.put("chat/" +    uid + "/" + current_user,addValue);
                    chatUservalue.put("chat/" +    current_user + "/" + uid ,addValue);

                    rootref.updateChildren(chatUservalue);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Sending Message To User When User Clicks Send Message Icon Button

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(message.equals(""))
                {
                    message.setError("can't send blank message");
                }
                else
                {

                    String  UidRoot  = "messages" + "/" + uid + "/" + current_user;
                    String  UserRoot = "messages" + "/" + current_user + "/" + uid;

                    DatabaseReference   user_msg_id = rootref.child("msg").child(uid).child(current_user).push();

                    user_msg_id.keepSynced(true);

                    String Push_id = user_msg_id.getKey();
                    Map AddMssg = new HashMap();
                    AddMssg.put("message",message.getText().toString());
                    AddMssg.put("type","text");
                    AddMssg.put("seen",false);
                    AddMssg.put("timestamp",ServerValue.TIMESTAMP);
                    AddMssg.put("from",uid);

                    Map SendMsg = new HashMap();
                    SendMsg.put(UidRoot + "/" + Push_id,AddMssg);
                    SendMsg.put(UserRoot + "/" + Push_id, AddMssg);

                    chatcov = FirebaseDatabase.getInstance().getReference().child("chat").child(uid).child(current_user);
                    rootref.child("chat").child(current_user).child(uid).updateChildren(AddMssg);
                    chatcov.updateChildren(AddMssg);

                    rootref.updateChildren(SendMsg);
                    message.setText(null);

                }

            }
        });


        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);



         // For Image Sending in the chat
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent image = new Intent();
                image.setType("image/*");
                image.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(image,"Select Image"),GALLERY_PICK);
            }
        });
    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK) // Check There Is Everuthing Fine Or Not
        {
            Uri imguri = data.getData(); // Getting Selected Image In Uri Form
            CropImage.activity(imguri)      // Starting Crop Function to Crop it
                     // Setting Image Square Crop
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                // Setting Progressing Bar To Load data
                progressDialog = new ProgressDialog(ChatScreen.this);
                progressDialog.setTitle("Sending....");
                progressDialog.setMessage("Please Wait While Sending Your Picture");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();



                Uri resultUri = result.getUri(); // This Is Where We Got Our Crooped Image


                FirebaseStorage storage = FirebaseStorage.getInstance(); // Connecting to database firebase
                StorageReference reference = storage.getReference().child("Messages_images"); // setting path where we have to go
                StorageReference file =  reference.child(Long.toHexString(Double.doubleToLongBits(Math.random())) + ".jpg"); // setting path with name for new image

                file.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {
                            String img_url = task.getResult().getDownloadUrl().toString(); // Converting image Uri To  Downloadable Image Url


                            // Sending Images To DataBase
                            String  UidRoot  = "messages" + "/" + uid + "/" + current_user;
                            String  UserRoot = "messages" + "/" + current_user + "/" + uid;

                            DatabaseReference   user_msg_id = rootref.child("msg").child(uid).child(current_user).push();

                            String Push_id = user_msg_id.getKey();
                            Map AddMssg = new HashMap();
                            AddMssg.put("message",img_url);
                            AddMssg.put("type","image");
                            AddMssg.put("seen",false);
                            AddMssg.put("time",ServerValue.TIMESTAMP);
                            AddMssg.put("from",uid);

                            Map SendMsg = new HashMap();
                            SendMsg.put(UidRoot + "/" + Push_id,AddMssg);
                            SendMsg.put(UserRoot + "/" + Push_id, AddMssg);

                            //for Last Msg Receive
                            chatcov = FirebaseDatabase.getInstance().getReference().child("chat").child(uid).child(current_user);
                            rootref.child("chat").child(current_user).child(uid).updateChildren(AddMssg);
                            chatcov.updateChildren(AddMssg);

                            rootref.updateChildren(SendMsg);
                            progressDialog.dismiss();


                        }
                        else
                        {
                            Toast.makeText(ChatScreen.this,"Error In Uploading ",Toast.LENGTH_SHORT).show();

                        }

                    }
                }); // Putting Data To FireBase


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }



    //Setting RecyclerView For Fetching message
    @Override
    protected void onStart() {
        super.onStart();
        final int[] positionitem = new int[1];

        Query query = rootref.child("messages").child(uid).child(current_user)
                .limitToLast(10);

        FirebaseRecyclerOptions<messages> options = new FirebaseRecyclerOptions.Builder<messages>()
                .setQuery(query,messages.class)
                .build();

        final FirebaseRecyclerAdapter<messages,MsgViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<messages, MsgViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MsgViewHolder holder, int position, @NonNull final messages model) {

                    positionitem[0] = position;
                    String  id = model.getFrom().toString();
                    String msg = model.getMessage().toString();
                    final String type  = model.getType().toString();
                    Boolean seen = model.getSeen().booleanValue();
                  // Long time = model.getTimestamp().longValue();
                    if(!id.equals(uid))  // if msg is received then fetching sender profile and message
                    {
                        rootref.child("Users").child(current_user).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String image = dataSnapshot.child("image").getValue().toString();
                                holder.setImg(image);
                                holder.setMsg(model.getMessage().toString(),"receive",type);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else {   // if message is sent
                        rootref.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String image = dataSnapshot.child("image").getValue().toString();
                                holder.setImg(image);
                                holder.setMsg(model.getMessage().toString(),"send",type);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

            }



            @Override
            public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_msg_display, parent, false);

                return new MsgViewHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(firebaseRecyclerAdapter.getItemCount());
            }
        });
 
        firebaseRecyclerAdapter.notifyDataSetChanged();





    }

    public static class MsgViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        Context c;


        public MsgViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
        }

        public void setMsg(final String msg , String msg_type , String type)
        {
            TextView textView = (TextView) view.findViewById(R.id.single_msg);
            final ImageView imageView = (ImageView)view.findViewById(R.id.image_msg);

            if((msg_type.equals("send") || msg_type.equals("receive")) && type.equals("image"))
            {
               // Picasso.with(c).load(msg).into(imageView);
                textView.setVisibility(View.GONE);
                Picasso.with(c).load(msg).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(c).load(msg).resize(400,500).into(imageView);
                    }
                });
            }

            if(msg_type.equals("send") && type.equals("text") ) {


                textView.setText(msg);
                imageView.setVisibility(View.GONE);
            }

            if (msg_type.equals("receive") && type.equals("text"))
            {
                textView.setText(msg);
                imageView.setVisibility(View.GONE);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundResource(R.drawable.single_msg_background);
                textView.setBackgroundColor(Color.WHITE);
            }



        }


        public  void setImg(final String img)
        {
                final CircleImageView circleImageView = (CircleImageView)view.findViewById(R.id.single_msg_image);
                //Picasso.with(c).load(img).into(circleImageView);
            Picasso.with(c).load(img).networkPolicy(NetworkPolicy.OFFLINE) .into(circleImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(c).load(img).into(circleImageView);
                }
            });
        }
        public View getView() {
            return view;
        }
    }
}
