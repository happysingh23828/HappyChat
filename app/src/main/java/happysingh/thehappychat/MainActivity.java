package happysingh.thehappychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {


    android.support.v7.widget.Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    ProgressDialog progressDialog;
    private AdView mAdView;


    //Creating A Fragment Page Adaptor Class Objects
    FragmenAdapter fragmenAdapter;

    FirebaseAuth mauth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        // Setting ToolBar Own
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Happy Chat");

        //Setting Fragments for Tabs
        viewPager = (ViewPager)findViewById(R.id.tab_pager);
        fragmenAdapter = new FragmenAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmenAdapter);

        //Setting Tab Layout with pager
        tabLayout = (TabLayout)findViewById(R.id.tab_main);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Checking User Is Logging In Or Not;
          final FirebaseUser  user= mauth.getCurrentUser();
        if(user==null )
        {
            gotostartpage();
        }

    }




    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser  user= mauth.getCurrentUser();
        if(user!=null) {

            databaseReference.child(mauth.getCurrentUser().getUid()).child("online").setValue("true");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseUser  user= mauth.getCurrentUser();
        if(user!=null)
        {
            databaseReference.child(mauth.getCurrentUser().getUid()).child("online").setValue("true");

        }
    }

    // This Is Call When Menu Bar Is Initialized On top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser  user= mauth.getCurrentUser();
        if(user!=null)
        {

            databaseReference.child(mauth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);

        }

    }


    // This is call When  Menu Bar Items Are Selected

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.menu_logout) // When User Clickes Logout In Menu
        {
            databaseReference.child(mauth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();
             Toast.makeText(this,"Logout Successfuly",Toast.LENGTH_SHORT).show();
            gotostartpage();
        }

        if(item.getItemId()==R.id.Account_setting) //When User Clicks Account Setting
        {
            Intent i = new Intent(MainActivity.this,SettingActivity.class);
            startActivity(i);

        }

        if(item.getItemId()==R.id.users)
        {
            Intent i = new Intent(MainActivity.this,AllUsers.class);
            startActivity(i);
        }
        return  true;
    }

    // This Is Created For Going To back Main Start PAge
    private void gotostartpage() {
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
    }




}
