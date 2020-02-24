package com.res.anos;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.res.anos.storage.MySharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.res.anos.R;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    EditText mNameEt;
    EditText mEmailEt;
    EditText mPasswordEt;
    Button mSignUpBtn, mLoginBtn;
    FirebaseUser firebaseUser;
    private String name,email,password;
    private FirebaseAuth mAuth;
    private MySharedPreferences sp;

    private CollectionReference mRef;
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "210755118", true);
        setContentView(R.layout.activity_main);

        StartAppAd.disableSplash();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        sp = MySharedPreferences.getInstance(this);
        mNameEt = findViewById(R.id.name);
        mEmailEt = findViewById(R.id.email);
        mPasswordEt = findViewById(R.id.password);
        mSignUpBtn = findViewById(R.id.btn);
        mLoginBtn = findViewById(R.id.login);
        dialog = utils.getAlertDialog(this,"Kayıt Yapılıyor");

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseFirestore.getInstance().collection("users");


        if(firebaseUser!= null){
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(intent);
        }

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataAndLogin();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }

    private void checkDataAndLogin(){
        name = mNameEt.getText().toString();
        email = mEmailEt.getText().toString().trim();
        password = mPasswordEt.getText().toString().trim();

        if(name.isEmpty() || email.isEmpty() || password.length()<6 ){
            Toast.makeText(getApplicationContext(),"Boş Alan Bırakılamaz",Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    } else {
                        saveUserCredentials();
                    }
                }
            });
        }

    }

    private void saveUserCredentials() {

        Map<String,String> map = new HashMap<>();
        map.put("name",name);
        map.put("email",email);
        map.put("password",password);
        map.put("id",mAuth.getUid());
        mRef.document(mAuth.getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    sp.setLogin("1");
                    sp.setUserID(mAuth.getUid());
                    Toast.makeText(getApplicationContext(),"Kayıt Başarılı",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        });

    }
}
