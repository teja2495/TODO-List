package com.example.teja2.todolist_viewpager;

/* HW07 - Group 32
Created by
1. Bala Guna Teja Karlapudi
2. Mandar Phapale
*/

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class pendingTasks extends Fragment {

    List<tasks> pendingList;
    customAdapter customAdapter;
    EditText et, etDeadline;
    ListView lv;
    Button add;
    DatabaseReference mRootRef;
    ImageView taskImage;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    Date mdate, currentDate, updatedDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_tasks, container, false);

        mRootRef = FirebaseDatabase.getInstance().getReference().child("tasksViewpager");

        et = view.findViewById(R.id.newTask);
        etDeadline = view.findViewById(R.id.deadline);
        lv = view.findViewById(R.id.pendingList);
        add = view.findViewById(R.id.createNew);
        pendingList = new ArrayList<>();

         date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                etDeadline.setText(sdf.format(myCalendar.getTime()));
            }
        };

         add.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 if(!etDeadline.getText().toString().isEmpty()){
                     currentDate=Calendar.getInstance().getTime();
                     SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                     try {
                         mdate = sdf.parse(etDeadline.getText().toString());
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                 }

                 if(etDeadline.getText().toString().isEmpty() || et.getText().toString().trim().isEmpty())
                     Toast.makeText(getActivity(), "Missing Values", Toast.LENGTH_LONG).show();
                 else if(currentDate.after(mdate)){
                     Toast.makeText(getActivity(), "Please pick date Greater than Today", Toast.LENGTH_LONG).show();
                 }
                 else{
                     tasks tasks= new tasks();
                     tasks.setTaskTitle(et.getText().toString());
                     tasks.setTaskDeadline(etDeadline.getText().toString());
                     tasks.setTaskID(mRootRef.push().getKey());
                     tasks.setStatus("pending");
                     mRootRef.child(tasks.getTaskID()).setValue(tasks);
                 }
             }
         });

        etDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                pendingList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    tasks rtask = new tasks();
                    rtask.setTaskTitle(postSnapshot.child("taskTitle").getValue().toString());
                    rtask.setTaskDeadline(postSnapshot.child("taskDeadline").getValue().toString());
                    rtask.setStatus(postSnapshot.child("status").getValue().toString());
                    rtask.setTaskID(postSnapshot.child("taskID").getValue().toString());
                    if (rtask.getStatus().equals("pending"))
                        pendingList.add(rtask);
                }
                Collections.reverse(pendingList);
                customAdapter = new customAdapter(pendingList);
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
            deadline.setText("Deadline: "+pendingList.get(position).getTaskDeadline());

            taskImage.setBackgroundResource(R.drawable.ongoing);
            taskImage.setTag(position);
            taskImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) taskImage.getTag();
                    mRootRef.child(list.get(position).getTaskID()).child("status").setValue("doing");
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