package com.example.kitapla_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitapla_project.R;
import com.example.kitapla_project.activities.ChatActivity;
import com.example.kitapla_project.models.User;

import java.util.List;

public class AdapterAllUsers extends RecyclerView.Adapter<AdapterAllUsers.MyHolder>{
    Context context;
    List<User> usersList;

    public AdapterAllUsers(Context context, List<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String nameSurname = usersList.get(position).getNameSurname();
        final String hisUID = usersList.get(position).getUid();
        String onlineStatus = usersList.get(position).getOnlineStatus();

        holder.TVnameSurname.setText(nameSurname);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+nameSurname, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("nameSurname", nameSurname);
                intent.putExtra("hisUid", hisUID);
                intent.putExtra("onlineStatus",onlineStatus);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView TVnameSurname;
        CardView container;

        public MyHolder(@NonNull View view){
            super(view);

            TVnameSurname = view.findViewById(R.id.TVnameSurname);
            container = view.findViewById(R.id.container);
        }
    }
}
