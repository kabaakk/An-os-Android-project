package com.res.anos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.res.anos.pojo.QuotesPojo;
import com.res.anos.storage.Constants;
import com.res.anos.storage.MySharedPreferences;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.res.anos.R;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    RelativeLayout mParent;
    FloatingActionButton mAddBtn,mChat;
    MySharedPreferences sp;
    RecyclerView mRecyclerView;
    FirebaseAuth auth;

    private CollectionReference mPostRef;
    private FirestoreRecyclerOptions options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "210755118", true);
        setContentView(R.layout.activity_home);
        StartAppAd.disableSplash();

        sp = MySharedPreferences.getInstance(this);
        saveUserData();
        mPostRef = FirebaseFirestore.getInstance().collection(Constants.POSTS);
        mParent = findViewById(R.id.relative);
        mRecyclerView = findViewById(R.id.rvMensajes);
        auth = FirebaseAuth.getInstance();
        mAddBtn = findViewById(R.id.addBtn);
        mChat = findViewById(R.id.chat);



        options = new FirestoreRecyclerOptions.Builder<QuotesPojo>()
                .setQuery(mPostRef,QuotesPojo.class)
                .build();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPostDialog();
            }
        });

        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,ChatActivity.class);
                startActivity(intent);
            }
        });



    }

    private void showPostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Anınızı Paylaşın veya Düşüncenizi Yazın");
        View view = LayoutInflater.from(this).inflate(R.layout.add_quote, null, false);
        final EditText editText = view.findViewById(R.id.edittext);
        builder.setView(view);
        builder.setPositiveButton("GÖNDER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quote = editText.getText().toString().trim();
                if(quote.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Boş Bırakılamaz",Toast.LENGTH_SHORT).show();
                } else {
                    postQuote(dialog,quote);
                }
            }
        });

        builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void postQuote(final DialogInterface dialog, String quote) {
        Map<String,Object> map = new HashMap<>();
        map.put(Constants.QUOTE,quote);
        map.put(Constants.TIMESTAMP,(System.currentTimeMillis()/1000));
        map.put(Constants.ID,sp.getUserID());
        map.put(Constants.NAME,sp.getUserData(Constants.NAME));
        mPostRef.document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Anınız Eklendi",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserData(){
        CollectionReference ref = FirebaseFirestore.getInstance().collection("users");
        final DocumentReference documentReference = ref.document(sp.getUserID());

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String name = documentSnapshot.getString(Constants.NAME);
                sp.setUserData(Constants.NAME,name);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirestoreRecyclerAdapter<QuotesPojo,MyViewHolder> adapter = new FirestoreRecyclerAdapter<QuotesPojo, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int i, @NonNull final QuotesPojo model) {
                holder.quote.setText(model.getQuote());
                holder.name.setText(model.getName());
                holder.time.setText(getTime(model.getTimestamp()));
                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sharableText = model.getQuote() + "\n \n-"+model.getName();
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT,sharableText);
                        startActivity(Intent.createChooser(sharingIntent, "Şununla paylaş: "));
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.post_list_layout,viewGroup,false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuLogout){
            auth.signOut();
            finish();
            startActivity(new Intent(HomeActivity.this,MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private String getTime(long timestamp){
        long ts = timestamp*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String time = sdf.format(new Date(ts));
        return time;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView quote,name,time;
        LinearLayout share,info;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            quote = itemView.findViewById(R.id.quote);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            share = itemView.findViewById(R.id.share);
            info = itemView.findViewById(R.id.info);
        }
    }
}
