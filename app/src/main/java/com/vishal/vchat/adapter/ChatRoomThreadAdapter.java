package com.vishal.vchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import com.vishal.vchat.R;
import com.vishal.vchat.model.Message;

import static com.vishal.vchat.adapter.ChatRoomsAdapter.getTimeStamp;

/**
 * Created by MUJ on 12-Dec-16.
 */

public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG=ChatRoomThreadAdapter.class.getSimpleName();
    private int userId;
    private int SELF=100;
    private static String today;
    private Context mContext;
    private ArrayList<Message> messageArrayList;
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView message,timestamp;
        public ViewHolder(View view){
            super(view);
            message=(TextView)itemView.findViewById(R.id.message);
            timestamp=(TextView)itemView.findViewById(R.id.timestamp);
        }
    }
    public ChatRoomThreadAdapter(Context mContext,ArrayList<Message> messages,int userId){
        this.mContext=mContext;
        this.messageArrayList=messages;
        this.userId=userId;
        Calendar calendar=Calendar.getInstance();
        today=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View itemView;
        if(viewType==SELF)
            itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_self,parent,false);
        else
            itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other,parent,false);
        return new ViewHolder(itemView);
    }
    public int getItemViewType(int position){
        Message message=messageArrayList.get(position);
        int id=Integer.parseInt(message.getMessage().substring(0,message.getMessage().indexOf(";")));
       if(id==userId)
            return SELF;
        else
            return position;
    }
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,int position){
        Message message=messageArrayList.get(position);
        String s=message.getMessage();
        ((ViewHolder)holder).message.setText(s.substring(s.indexOf(";")+1));
        String timestamp=message.getCreatedAt();
        /*if(message.getUser().getName()!=null)
            timestamp=message.getUser().getName()+","+timestamp;*/
        //((ViewHolder)holder).timestamp.setText(timestamp);
    }
    public int getItemCount(){
        return messageArrayList.size();
    }
}
