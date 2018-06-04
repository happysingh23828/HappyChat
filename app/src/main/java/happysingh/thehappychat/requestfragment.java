package happysingh.thehappychat;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static happysingh.thehappychat.friendsfragment.FriendViewHolder.context;


/**
 * A simple {@link Fragment} subclass.
 */
public class requestfragment extends Fragment {
    DatabaseReference databaseReference,getUserdatabase,friend_request;
    DatabaseReference userdatabase;
    RecyclerView recyclerView;
    String current_userid;
    FirebaseAuth firebaseAuth;
    View view;
    Boolean flag = false;
    public requestfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_requestfragment, container, false);

        //setting RecyclerView
        recyclerView = (RecyclerView)view.findViewById(R.id.request_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Setting Firease
        firebaseAuth = FirebaseAuth.getInstance();
        current_userid = firebaseAuth.getCurrentUser().getUid().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("friend_request_data").child(current_userid);
        getUserdatabase =  FirebaseDatabase.getInstance().getReference().child("Users");
        return  view;
    }

    public void onStart() {
        super.onStart();

        Query query = databaseReference
                .limitToLast(50);

        FirebaseRecyclerOptions<requests> options =
                new FirebaseRecyclerOptions.Builder<requests>()
                        .setQuery(query, requests.class)
                        .build();

        FirebaseRecyclerAdapter<requests,RequestViewHolder> adapter = new FirebaseRecyclerAdapter<requests, RequestViewHolder>(options) {
            @Override
            public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_request_single_show, parent, false);


                return new RequestViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull requests model) {

                 final String user_id = getRef(position).getKey().toString();

                final String[] user_name = new String[1];
                final String[] img = new String[1];

                    getUserdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            user_name[0] = dataSnapshot.child(user_id).child("name").getValue(String.class);
                            img[0] = dataSnapshot.child(user_id).child("image").getValue(String.class);
                            holder.setImage(img[0]);
                            holder.setName(user_name[0]);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                if(model.getReq_type().toString().equals("received"))
                {
                    holder.setButton("received",user_id);

                }

                else if(model.getReq_type().toString().equals("sent"))
                {
                    holder.setButton("sent",user_id);
                }



            }
        };

         adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public  class RequestViewHolder extends  RecyclerView.ViewHolder
    {

        View view;
        public RequestViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
        }

        public void setImage(final String image)
        {
            final CircleImageView img = (CircleImageView) view.findViewById(R.id.single_profile_friend_request);

            //
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(img, new Callback() {
                        @Override
                        public void onSuccess() {
                            ////________For Offile Download
                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(image).resize(100,100).centerCrop().into(img); // this is use to
                        }
                    });
        }

        public  void  setName(String name)
        {
            TextView textView = (TextView)view.findViewById(R.id.single_name_friend);
            textView.setText(name);
        }

        public  void  setButton(String type, final String send_user_id)
        {
            final Button accept = (Button)view.findViewById(R.id.single_accept_request);
            Button reject = (Button)view.findViewById(R.id.single_reject_request);


            if(type.equals("sent"))
            {
                accept.setText("Cancel Sent Request");
                accept.setBackgroundColor(Color.BLUE);
                accept.setTextColor(Color.WHITE);
                reject.setVisibility(View.GONE);
              accept.setPadding(10,10,10,10);

            }

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(accept.getText().toString().equals("Cancel Sent Request"))
                    {
                        friend_request = FirebaseDatabase.getInstance().getReference().child("friend_request_data");
                        friend_request.child(current_userid).child(send_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful())
                                {
                                    friend_request.child(send_user_id).child(current_userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getContext()," Friend Request Cancelled ",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else
                    {
                        final String date = DateFormat.getLongDateFormat(getContext()).format(new Date());

                        String  UidRoot  = "friend_data" + "/" + send_user_id + "/" + current_userid;
                        String  UserRoot = "friend_data" + "/" + current_userid + "/" + send_user_id;
                        DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();

                        Map map = new HashMap();
                        map.put("date",date);

                        mRoot.child(UidRoot).updateChildren(map);
                        mRoot.child(UserRoot).updateChildren(map);



                        // Sending Images To DataBase

                        //Toast.makeText(getContext(), "m  coming dude "+send_user_id, Toast.LENGTH_SHORT).show();
                        userdatabase = FirebaseDatabase.getInstance().getReference().child("friend_data").child(current_userid);
                        userdatabase.child(send_user_id).child("date").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    friend_request = FirebaseDatabase.getInstance().getReference().child("friend_request_data");
                                    friend_request.child(current_userid).child(send_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                friend_request.child(send_user_id).child(current_userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {


                                                        userdatabase = FirebaseDatabase.getInstance().getReference().child("friend_data").child(send_user_id);
                                                        userdatabase.child(current_userid).child("date").setValue(date);
                                                        Toast.makeText(getContext(), "Friend Request Accepted", Toast.LENGTH_SHORT).show();


                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }

                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friend_request = FirebaseDatabase.getInstance().getReference().child("friend_request_data");
                    friend_request.child(current_userid).child(send_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                friend_request.child(send_user_id).child(current_userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getContext()," Friend Request Deleted ",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });

                }
            });



        }
    }
}
