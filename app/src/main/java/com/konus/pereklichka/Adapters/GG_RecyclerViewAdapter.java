package com.konus.pereklichka.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konus.pereklichka.rv_models.GroupModel;
import com.konus.pereklichka.GroupsInterface;
import com.konus.pereklichka.R;

import java.util.ArrayList;

public class GG_RecyclerViewAdapter extends RecyclerView.Adapter<GG_RecyclerViewAdapter.MyViewHolder> {
    private final GroupsInterface groupsInterface;
    Context context;
    ArrayList<com.konus.pereklichka.rv_models.GroupModel> GroupModel;
    public GG_RecyclerViewAdapter(Context context, ArrayList<GroupModel> GroupModel,GroupsInterface groupsInterface){
        this.groupsInterface = groupsInterface;
        this.context = context;
        this.GroupModel = GroupModel;
    }

    @NonNull
    @Override
    public GG_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.group_rv_item, parent, false);
        return new GG_RecyclerViewAdapter.MyViewHolder(view,groupsInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull GG_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.group_name_txt.setText("       "+GroupModel.get(position).getgroup_name_txt());
        holder.memberAmountTxt.setText(GroupModel.get(position).getmemberAmountTxt());
    }

    @Override
    public int getItemCount() {
        return GroupModel.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView group_name_txt, memberAmountTxt;
        public MyViewHolder(@NonNull View itemView, GroupsInterface groupsInterface) {
            super(itemView);

            memberAmountTxt = itemView.findViewById(R.id.memberAmountTxt);
            group_name_txt = itemView.findViewById(R.id.group_name_txt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (groupsInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            groupsInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
