package happysingh.thehappychat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {


    android.support.v7.widget.Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    //Creating A Fragment Page Adaptor Class Objects
    FragmenAdapter fragmenAdapter;

    FirebaseAuth mauth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        tabLayout.setupWithViewPager(viewPager);





    }

    @Override
    protected void onStart() {
        super.onStart();

        // Checking User Is Logging In Or Not;
          FirebaseUser  user= mauth.getCurrentUser();
        if(user==null )
        {
            gotostartpage();
        }
        else if(!user.isEmailVerified())
        {
            //FirebaseAuth.getInstance().signOut();
            //user.delete();
            //gotostartpage();
            databaseReference.child(mauth.getCurrentUser().getUid()).child("online").setValue("true");
        }

        else
        {
            //databaseReference.child(mauth.getCurrentUser().getUid()).child("online").setValue("true");
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        //databaseReference.child(mauth.getCurrentUser().getUid()).child("online").setValue("true");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    // databaseReference.child(mauth.getCurrentUser().getUid()).child("online").setValue("true");
    }

    @Override
    protected void onStop() {
        super.onStop();
     //  databaseReference.child(mauth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }

    // This Is Call When Menu Bar Is Initialized On top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
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
        Intent i = new Intent(MainActivity.this,Splash_screen.class);
        startActivity(i);
        finish();
    }




}
