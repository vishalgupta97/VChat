package com.vishal.vchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.vishal.vchat.R;
import com.vishal.vchat.activity.NewGroup;
import com.vishal.vchat.model.ChatRoom;
import com.vishal.vchat.model.User;

/**
 * Created by MUJ on 11-Dec-16.
 */

public class NewGroupAdapter extends RecyclerView.Adapter<NewGroupAdapter.ViewHolder>{
    private Context mContext;
    private List<User> userArrayList;
    boolean arr[];
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public CheckBox box;
        public ViewHolder(View view){
            super(view);
            name=(TextView)view.findViewById(R.id.newGroup_name);
            box=(CheckBox)view.findViewById(R.id.newGroup_box);
        }
    }
    public NewGroupAdapter(Context mContext, List<User> userArrayList){
        this.mContext=mContext;
        this.userArrayList=userArrayList;
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.new_group_row,parent,false);
        return new ViewHolder(itemView);
    }
    public void onBindViewHolder(final ViewHolder holder, int position){
        User user=userArrayList.get(position);
        holder.name.setText(user.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.box.setActivated(true);
            }
        });
    }
    public int getItemCount(){
        return userArrayList.size();
    }
    public interface ClickListener {
        void onClick(View view,int position);
        void onLongClick(View view,int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private NewGroupAdapter.ClickListener clickListener;
        public RecyclerTouchListener(Context context,final RecyclerView recyclerView,final NewGroupAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null)
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            });
        }
        public boolean onInterceptTouchEvent(RecyclerView rv,MotionEvent e){
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null&&clickListener!=null&&gestureDetector.onTouchEvent(e))
                clickListener.onClick(child,rv.getChildPosition(child));
            return false;
        }
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
