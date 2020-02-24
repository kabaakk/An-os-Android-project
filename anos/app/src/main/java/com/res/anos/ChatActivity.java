package com.res.anos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.res.anos.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMensajes;
    private EditText etName;
    private EditText etMensaje;
    private ImageButton btnSend;

    FirebaseAuth mAuth;

    private List<MensajeVO> lstMensajes;
    private AdapterRVMensajes mAdapterRVMensajes;

    private void setComponents(){
        rvMensajes = findViewById(R.id.rvMensajes);
        etName = findViewById(R.id.name);
        etMensaje = findViewById(R.id.etMensaje);
        btnSend = findViewById(R.id.btnSend);

        lstMensajes = new ArrayList<>();
        mAdapterRVMensajes = new AdapterRVMensajes(lstMensajes);
        rvMensajes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvMensajes.setAdapter(mAdapterRVMensajes);
        rvMensajes.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();

        FirebaseFirestore.getInstance().collection("Chat")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(DocumentChange mDocumentChange : queryDocumentSnapshots.getDocumentChanges()){
                            if(mDocumentChange.getType() == DocumentChange.Type.ADDED){
                                lstMensajes.add(mDocumentChange.getDocument().toObject(MensajeVO.class));
                                mAdapterRVMensajes.notifyDataSetChanged();
                                rvMensajes.smoothScrollToPosition(lstMensajes.size());
                            }
                        }
                    }
                });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMensaje.length() == 0)
                    return;
                MensajeVO mMensajeVO = new MensajeVO();
                mMensajeVO.setMessage(etMensaje.getText().toString());
                mMensajeVO.setName(mAuth.getCurrentUser().getEmail());
                FirebaseFirestore.getInstance().collection("Chat").add(mMensajeVO);
                etMensaje.setText("");

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setComponents();
    }
}
