package com.example.kitapla_project.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitapla_project.R;
import com.example.kitapla_project.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.kitapla_project.R.layout.row_chat_left;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    Context context;
    List<Chat> chatList;

    FirebaseUser firebaseUser;

    public AdapterChat(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new MyHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(row_chat_left,parent,false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimeStamp();

        //convert time stamp to dd/mm/yy hh:mm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm",cal).toString();

        //set data
        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);

        //görüldü veya iletildi (seen/delivered) setleme
        if(position==chatList.size()-1){
            if(chatList.get(position).isSeen())
                holder.isSeenTv.setText("Goruldu");
            else
                holder.isSeenTv.setText("Iletildi");
        }else
            holder.isSeenTv.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_TYPE_RIGHT;
        else return MSG_TYPE_LEFT;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView messageTv, timeTv, isSeenTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.seenTv);
        }
    }
}
