package com.bondev.travelmantics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class DealActivity extends AppCompatActivity {

   private FirebaseDatabase my_firebase_database;
   private DatabaseReference my_Database_reference;
   private static final int PICTURE_RESULT=42;
    EditText txtTitle;
    EditText txtDescription;
    EditText txtPrice;
    Button btn_upload_img;
    public static StorageReference fbStorageRef;
    private static ListActivity caller;
    TravelDeal deal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUtil.openFbReference("traveldeals",caller );
        my_firebase_database = FirebaseUtil.my_firebase_database;
        my_Database_reference =FirebaseUtil.my_database_reference;
        txtTitle=findViewById(R.id.txtTitle);
        txtDescription=findViewById(R.id.txtDescription);
        txtPrice=findViewById(R.id.txtPrice);
        Intent intent=getIntent();
        TravelDeal selected_deal=(TravelDeal) intent.getSerializableExtra("Selected_deal");
        if(selected_deal==null){
            selected_deal=new TravelDeal();
        }
        this.deal=selected_deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
        btn_upload_img =findViewById(R.id.btn_upload);
    }

    public void uploadImage(View view){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(intent.createChooser
                (intent,"Upload image"), PICTURE_RESULT);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this,"Deal saved",Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return  true;
            case R.id.delete_menu:
               delete_deal();
               backToList();
                return  true;
                default:
                    return  super.onOptionsItemSelected(item);
        }
    }

    private void clean() {
        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {
        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        if(deal.getId()==null) {
            my_Database_reference.push().setValue(deal);
        }else{
            my_Database_reference.child(deal.getId()).setValue(deal);
        }
    }

    public void delete_deal(){
        if(deal.getId()==null){
            Toast.makeText(this,"please save a deal first before trying to delete",Toast.LENGTH_LONG).show();;
            return;
        }else{
            my_Database_reference.child(deal.getId()).removeValue();
            Toast.makeText(this,"Deal deleted successfully",Toast.LENGTH_LONG).show();;
        }
    }

   public void backToList(){
        Intent intent=new Intent(this,ListActivity.class);
        startActivity(intent);
   }

   public void enableEditText( boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
       txtDescription.setEnabled(isEnabled);
       txtPrice.setEnabled(isEnabled);
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if(FirebaseUtil.isAdmin==true){
            menu.findItem(R.id.save_menu).setVisible(true);
            menu.findItem(R.id.delete_menu).setVisible(true);
            enableEditText(true);
        }else{
            menu.findItem(R.id.save_menu).setVisible(false);
            menu.findItem(R.id.delete_menu).setVisible(false);
            enableEditText(false);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICTURE_RESULT && resultCode==RESULT_OK){
            Uri imgUri=data.getData();
            fbStorageRef=FirebaseUtil.my_storage_reference.child(imgUri.getLastPathSegment());
            fbStorageRef.putFile(imgUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   String url= taskSnapshot.getStorage().getDownloadUrl().toString();
                    deal.setImgUrl(url);
                }
            });
        }
    }
}
