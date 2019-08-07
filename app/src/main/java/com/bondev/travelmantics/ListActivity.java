package com.bondev.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.listactivity_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.insert_deal);
        if(FirebaseUtil.isAdmin==true){
            menuItem.setVisible(true);
        }else{
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_deal:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return  true;
            case R.id.signout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("log out", " User loged out");
                                FirebaseUtil.attarchLitener();
                            }
                        });
                FirebaseUtil.dettarchLitener();
                return  true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFbReference("traveldeals",this );
        FirebaseUtil.checkTypeOfUser(FirebaseUtil.my_firebase_auth.getUid());
        RecyclerView rvDeals=findViewById(R.id.rv_deals);
        final DealAdapter adapter= new DealAdapter();
        rvDeals.setAdapter(adapter);
        LinearLayoutManager deals_layout_manager= new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        rvDeals.setLayoutManager(deals_layout_manager);

        FirebaseUtil.attarchLitener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.dettarchLitener();
    }

    public void showMenu(){
        invalidateOptionsMenu();
    }
}
