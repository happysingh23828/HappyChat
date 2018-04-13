package happysingh.thehappychat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class friendsfragment extends Fragment {

    DatabaseReference databaseReference;
    DatabaseReference userdatabase;
   RecyclerView recyclerView;
   String current_userid;
   FirebaseAuth firebaseAuth;
   View view;
    public friendsfragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_friendsfragment2, container, false);


        //Setting FireBase
        current_userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("friend_data").child(current_userid);
        databaseReference.keepSynced(true);
        userdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        // Offline Text Synced
        userdatabase.keepSynced(true);

        //setting RecyclerView
        recyclerView = (RecyclerView)view.findViewById(R.id.friend_recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = databaseReference
                .limitToLast(50);


        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();

        FirebaseRecyclerAdapter<Friends, FriendViewHolder> adapter = new FirebaseRecyclerAdapter<Friends, FriendViewHolder>(options) {
            @Override

            public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_single_show, parent, false);

                return new FriendViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendViewHolder holder, int position, @NonNull Friends model) {
                holder.setDate(model.getDate());

                // Getting Friend Full Details
                final String List_user_id = getRef(position).getKey();
                userdatabase.child(List_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String username = dataSnapshot.child("name").getValue().toString();

                        final String img = dataSnapshot.child("image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {
                            String online = dataSnapshot.child("online").getValue().toString();
                            holder.setOnline(online);
                        }
                        else
                        {
                            holder.setOnline("false");
                        }

                        // Setting To Display Component
                        holder.setName(username);
                        holder.setImage(img);

                        // Setting Options when User Clicked On Profile Option
                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{"Open Profile","Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Choose Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which==0)
                                        {

                                            Intent i = new Intent(getContext(),Profile_Activity.class);
                                            i.putExtra("user_id",List_user_id);
                                            startActivity(i);
                                        }
                                        else
                                        {
                                            FirebaseDatabase.getInstance().getReference().child("chat").child(List_user_id).child(current_userid)
                                                    .child("seen").setValue(true);
                                            Intent i = new Intent(getContext(),ChatScreen.class);
                                            i.putExtra("user_id",List_user_id);
                                            i.putExtra("user_name",username);
                                            i.putExtra("img",img);
                                            startActivity(i);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        static Context context;
        FriendViewHolder f;
        public FriendViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
        }


        public void  setDate(String date)
        {
            TextView textView = (TextView)view.findViewById(R.id.status_id_single_user);
            textView.setText(" Friend Since -" +date);
        }

        public  void setName(String name)
        {

            TextView textView = (TextView)view.findViewById(R.id.name_id_single_user);
            textView.setText(name);
        }

        public void setImage(final String image) {

            final ImageView img = (ImageView) view.findViewById(R.id.profile_id_single_user);;
            //
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(img, new Callback() {
                        @Override
                        public void onSuccess() {
                            ////________For Offile Download
                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(image).into(img); // this is use to
                        }
                    });
        }

        public  void setOnline(String status)
        {

            ImageView img = (ImageView)view.findViewById(R.id.online);

            if(status.equals("true"))
            img.setVisibility(View.VISIBLE);
            else
                img.setVisibility(View.GONE);
        }






    }
}
