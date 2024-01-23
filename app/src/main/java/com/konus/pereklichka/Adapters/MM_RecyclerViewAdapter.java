package com.konus.pereklichka.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konus.pereklichka.rv_models.MemberModel;
import com.konus.pereklichka.R;

import java.util.ArrayList;

public class MM_RecyclerViewAdapter extends RecyclerView.Adapter<MM_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<com.konus.pereklichka.rv_models.MemberModel> MemberModel;
    public MM_RecyclerViewAdapter(Context context, ArrayList<MemberModel> MemberModel){
        this.context = context;
        this.MemberModel = MemberModel;
    }

    @NonNull
    @Override
    public MM_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recylcer_view_row_g, parent, false);
        return new MM_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MM_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.txtName.setText("  "+MemberModel.get(position).getTxtName());//temporary measure
        holder.txtLName.setText(MemberModel.get(position).getTxtLName());
        try{
            System.out.println(MemberModel.get(position).getImage());
            if (MemberModel.get(position).getImage()) {
                holder.image.setImageResource(R.drawable.checked);
            }
            else {
                holder.image.setImageResource(R.drawable.cancel);
            }}
        catch (Exception e){
            holder.image.setImageResource(R.drawable.cancel);
            System.err.println(e);
        }


    }

    @Override
    public int getItemCount() {
        return MemberModel.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView txtName, txtLName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            txtName = itemView.findViewById(R.id.txtName);
            txtLName = itemView.findViewById(R.id.txtLName);
        }
    }
}
