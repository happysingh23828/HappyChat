package happysingh.thehappychat;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class AllUsers extends AppCompatActivity {

    Toolbar toolbar;
    ListView ls;
    RecyclerView userslist;
    DatabaseReference databaseReference;
    FirebaseAuth mauth = FirebaseAuth.getInstance();;
    EditText search_text;
    String searching_friend;
    ImageView search_btn;
    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mAdView = findViewById(R.id.adViewalluser);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //setting Toolbar
        search_text =(EditText)findViewById(R.id.search_user_text);
        search_btn = (ImageView)findViewById(R.id.search_button) ;



        // Setting  Firebase Database


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);


        //setting Recyclerview
        userslist = (RecyclerView) findViewById(R.id.recycle_view_id);
        userslist.setHasFixedSize(true);
        userslist.setLayoutManager(new LinearLayoutManager(AllUsers.this));


        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searching_friend = s.toString();
                onStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searching_friend = s.toString();
                onStart();
            }

            @Override
            public void afterTextChanged(Editable s) {
                searching_friend = s.toString();
                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        Query query = databaseReference.orderByChild("name")
                .startAt(searching_friend)
                .endAt(searching_friend + "\uf8ff")
                .limitToLast(20);


        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, final int position, @NonNull Users model) {

                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getImage());

                if(!model.isemailverified)
                {
                    holder.view.setLayoutParams(new ViewGroup.LayoutParams(0,0));
                }

                if(getRef(position).getKey().equals(mauth.getCurrentUser().getUid()))
                {
                    holder.view.setLayoutParams(new ViewGroup.LayoutParams(0,0));

                }

                  // Identifying User profile On Which   Clicked
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userid = getRef(position).getKey();
                        Intent i = new Intent(AllUsers.this,Profile_Activity.class);
                        i.putExtra("user_id",userid.toString());
                        startActivity(i);

                    }
                });

            }

            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_user_display, parent, false);

                return new UsersViewHolder(view);
            }
        };

        userslist.setAdapter(adapter);
        adapter.startListening();



    }




    public  class UsersViewHolder extends RecyclerView.ViewHolder {

        View view;
        Context c;

        public UsersViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name) {

            TextView txtUserName = (TextView) view.findViewById(R.id.name_id_single_user);
            txtUserName.setText(name);


        }

        public void setStatus(String status) {
            TextView txtstatus = (TextView) view.findViewById(R.id.status_id_single_user);
            txtstatus.setText(status);
        }

        public void setImage(final String image) {

            final CircleImageView img = (CircleImageView) view.findViewById(R.id.profile_id_single_user);;

          //  Picasso.with(c).load(image).into(img);

            Picasso.with(c).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(img, new Callback() {
                @Override
                public void onSuccess() {
                            // Offline Download
                }

                @Override
                public void onError() {
                    Picasso.with(c).load(image).into(img);
                }
            });

        }

        public View getView() {
            return view;

        }
    }

    protected void onResume() {
        super.onResume();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
        databaseReference.keepSynced(true);

        databaseReference.child("online").setValue("true");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
        databaseReference.keepSynced(true);

        databaseReference.child("online").setValue("true");


    }
}






