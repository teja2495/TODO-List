package com.example.teja2.todolist_viewpager;

/* HW07 - Group 32
Created by
1. Bala Guna Teja Karlapudi
2. Mandar Phapale
*/

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class currentTasks extends Fragment {

    List<tasks> doingList;
    customAdapter customAdapter;
    ListView lv;
    DatabaseReference mRootRef;
    ImageView taskImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_tasks, container, false);

        mRootRef = FirebaseDatabase.getInstance().getReference().child("tasksViewpager");

        lv = view.findViewById(R.id.doing);
        taskImage=view.findViewById(R.id.taskImage);
        doingList = new ArrayList<>();

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                doingList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    tasks rtask = new tasks();
                    rtask.setTaskTitle(postSnapshot.child("taskTitle").getValue().toString());
                    rtask.setTaskDeadline(postSnapshot.child("taskDeadline").getValue().toString());
                    rtask.setStatus(postSnapshot.child("status").getValue().toString());
                    rtask.setTaskID(postSnapshot.child("taskID").getValue().toString());
                    if (rtask.getStatus().equals("doing"))
                        doingList.add(rtask);
                }
                Collections.reverse(doingList);
                customAdapter = new customAdapter(doingList);
                lv.setAdapter(customAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    class customAdapter extends BaseAdapter {

        List<tasks> list;

        public customAdapter(List<tasks> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.custom_layout, null);
            taskImage=view.findViewById(R.id.taskImage);
            TextView task = view.findViewById(R.id.task);
            TextView deadline = view.findViewById(R.id.time);
            task.setText(list.get(position).getTaskTitle());
            deadline.setText("Deadline: "+doingList.get(position).getTaskDeadline());

            taskImage.setBackgroundResource(R.drawable.completed);
            taskImage.setTag(position);
            taskImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) taskImage.getTag();
                    mRootRef.child(list.get(position).getTaskID()).child("status").setValue("completed");
                }
            });
            return view;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
