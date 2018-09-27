package com.vishal.vchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vishal.vchat.R;
import com.vishal.vchat.model.User;

import java.util.Calendar;
import java.util.List;

/**
 * Created by MUJ on 30-Mar-17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private Context mContext;
    private List<User> usersArrayList;
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public ViewHolder(View view){
            super(view);
            name=(TextView)view.findViewById(R.id.textView);

        }
    }
    public ContactsAdapter(Context mContext,List<User> contactsArrayList){
        this.mContext=mContext;
        this.usersArrayList=contactsArrayList;
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_row,parent,false);
        return new ViewHolder(itemView);
    }
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position){
        User contacts=usersArrayList.get(position);
        holder.name.setText(contacts.getName());
    }
    public int getItemCount(){
        return usersArrayList.size();
    }
    public interface ClickListener {
        void onClick(View view,int position);
        void onLongClick(View view,int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private ContactsAdapter.ClickListener clickListener;
        public RecyclerTouchListener(Context context,final RecyclerView recyclerView,final ContactsAdapter.ClickListener clickListener) {
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
