package happysingh.thehappychat;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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

import static happysingh.thehappychat.friendsfragment.FriendViewHolder.context;


/**
 * A simple {@link Fragment} subclass.
 */
public class chatsfragment extends Fragment {

    DatabaseReference databaseReference,getUserdatabase;
    DatabaseReference userdatabase;
    RecyclerView recyclerView;
    String current_userid;
    FirebaseAuth firebaseAuth;
    View view;

    public chatsfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chatsfragment, container, false);

        // Setting RecyclerView
        recyclerView = (RecyclerView)view.findViewById(R.id.chat_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setting Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        current_userid = firebaseAuth.getCurrentUser().getUid().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("chat").child(current_userid);
        getUserdatabase =  FirebaseDatabase.getInstance().getReference().child("Users");
        return  view;
    }


    @Override
    public void onStart() {
        super.onStart();

        Query query = databaseReference;


        FirebaseRecyclerOptions<chat> options =
                new FirebaseRecyclerOptions.Builder<chat>()
                        .setQuery(query, chat.class)
                        .build();


        FirebaseRecyclerAdapter<chat,ChatViewHolder> adapter = new FirebaseRecyclerAdapter<chat, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull final chat model) {

                // fetching name and image of user that chatted before;
                final String single_chat_user_id = getRef(position).getKey();
                final String[] name = new String[1];
                final String[] img = new String[1];

                holder.setTime(model.getTimestamp());

                holder.setMsg(model.getMessage(),model.getType(),model.getFrom(),model.getSeen());







                    getUserdatabase.child(single_chat_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                             name[0] = dataSnapshot.child("name").getValue().toString();
                             img[0] = dataSnapshot.child("image").getValue().toString();
                            holder.setName(name[0]);
                            holder.setImage(img[0]);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // Open Chat Window TO Specific User When There Chat View IS Clicked
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!model.getFrom().equals(current_userid))
                            {
                                FirebaseDatabase.getInstance().getReference().child("chat").child(single_chat_user_id).child(current_userid)
                                        .child("seen").setValue(true);

                                FirebaseDatabase.getInstance().getReference().child("chat").child(current_userid).child(single_chat_user_id)
                                        .child("seen").setValue(true);
                            }

                            Intent i = new Intent(getContext(),ChatScreen.class);
                            i.putExtra("user_id",single_chat_user_id);
                            i.putExtra("user_name", name[0]);
                            i.putExtra("img", img[0]);
                            startActivity(i);
                        }
                    });
            }

            @Override
            public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_chat_screen_show, parent, false);
                return new ChatViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public  class ChatViewHolder extends  RecyclerView.ViewHolder{

        View view;
        public ChatViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
        }

        public  void  setName(String name)
        {
            TextView textView = (TextView)view.findViewById(R.id.chat_single_name);
           // textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camera_alt_black_24dp,0,0,0); // Setting Photo Icon
            textView.setText(name);

        }

        public  void  setImage(final String image)
        {
            final ImageView img = (ImageView) view.findViewById(R.id.chat_profile);

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

        public  void  setTime(Long timestamp)
        {

            String time =  DateUtils.formatDateTime(getContext(), timestamp, DateUtils.FORMAT_SHOW_TIME);
            TextView textView = (TextView)view.findViewById(R.id.chat_time);
            textView.setText(time);

        }


        public  void setMsg(String msg,String type ,String getfrom ,Boolean seen)
        {
            TextView textView =(TextView)view.findViewById(R.id.chat_last_msg);
            ImageView imageView =(ImageView)view.findViewById(R.id.chat_seen);
            if(type.equals("image") && getfrom.equals(current_userid)  )
        {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camera_alt_black_24dp,0,0,0); // Setting Photo Icon
            textView.setText("  Photo");
            setSeen(seen);
        }

            if(type.equals("text") && getfrom.equals(current_userid))
            {
                textView.setText(msg);
                imageView.setImageResource(R.drawable.seen);
                setSeen(seen);
            }

            if(type.equals("image") && !getfrom.equals(current_userid)  )
            {
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camera_alt_black_24dp,0,0,0); // Setting Photo Icon
                textView.setText("  Photo");

                if(!seen)
                {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.comemsg));

                }
                else
                    imageView.setVisibility(View.GONE);

            }

            if(type.equals("text") && !getfrom.equals(current_userid))
            {
                textView.setText(msg);
                if(!seen)
                {
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.comemsg));

                }
                else
                    imageView.setVisibility(View.GONE);
            }

        }

        public  void  setSeen(boolean isSeen)
        {
            ImageView imageView =(ImageView)view.findViewById(R.id.chat_seen);

            if(isSeen)
            {
                imageView.setImageResource(R.drawable.seen);
            }
            else
                imageView.setImageResource(R.drawable.unseen);


        }
    }
}
