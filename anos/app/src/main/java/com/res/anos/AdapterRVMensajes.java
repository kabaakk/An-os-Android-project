package com.res.anos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterRVMensajes extends RecyclerView.Adapter<AdapterRVMensajes.MensajeHolder> {

    private List<MensajeVO> lstMensajes;

    public AdapterRVMensajes(List<MensajeVO> lstMensajes) {
        this.lstMensajes = lstMensajes;
    }

    @NonNull
    @Override
    public MensajeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message,viewGroup,false);
        return new MensajeHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeHolder mensajeHolder, int i) {
        mensajeHolder.tvName.setText(lstMensajes.get(i).getName());
        mensajeHolder.tvMessage.setText(lstMensajes.get(i).getMessage());
    }

    @Override
    public int getItemCount() {
        return lstMensajes.size();
    }

    class MensajeHolder extends RecyclerView.ViewHolder{
        private TextView tvName,tvMessage;
        public MensajeHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
