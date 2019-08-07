package com.bondev.travelmantics;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebaseUtil {

    public static FirebaseDatabase my_firebase_database;
    public static DatabaseReference my_database_reference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth my_firebase_auth;
    public  static FirebaseStorage my_firebase_storage;
    public static StorageReference my_storage_reference;
    public static FirebaseAuth.AuthStateListener my_auth_litener;
    public static ArrayList<TravelDeal> deal;
    private static final int RC_SIGN_IN=123;
    private static ListActivity caller;
    public  static boolean isAdmin;


    public FirebaseUtil(){}

    public static void openFbReference(String ref,  final ListActivity callerActivity){
        if(firebaseUtil==null){
            firebaseUtil= new FirebaseUtil();
            my_firebase_database=FirebaseDatabase.getInstance();
            my_firebase_auth=FirebaseAuth.getInstance();
            caller=callerActivity;
            my_auth_litener= new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                   if (my_firebase_auth.getCurrentUser()==null){
                    FirebaseUtil.signIn();
                   }else {
                      String userId = my_firebase_auth.getUid();
                      checkTypeOfUser(userId);
                   }
                      // Toast.makeText(callerActivity.getBaseContext(), "Welcome back", Toast.LENGTH_LONG).show();
                }
            };
            connectToFbStorage();
        }
        deal= new ArrayList<TravelDeal>();
        my_database_reference =my_firebase_database.getReference().child(ref);
    }

   public static void checkTypeOfUser(String userId) {
       firebaseUtil.isAdmin=false;
       DatabaseReference ref=my_firebase_database.getReference().child("administrators").child(userId);
        ChildEventListener adminlistener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                firebaseUtil.isAdmin=true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(adminlistener);
    }

    private static void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attarchLitener(){
        my_firebase_auth.addAuthStateListener(my_auth_litener);
    }

    public static void dettarchLitener(){
        my_firebase_auth.removeAuthStateListener(my_auth_litener);
    }

    public static void connectToFbStorage(){
        my_firebase_storage=FirebaseStorage.getInstance();
        my_storage_reference=my_firebase_storage.getReference().child("deals_Pictures");
    }


}
