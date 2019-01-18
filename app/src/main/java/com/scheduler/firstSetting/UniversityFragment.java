package com.scheduler.firstSetting;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scheduler.R;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class UniversityFragment extends Fragment {

    private SendUniversityName sendName;

    private DatabaseReference reference;
    private EditText searchField;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> universitiesList = new ArrayList<>();
    private ArrayList<String> universitiesKeyList = new ArrayList<>();


    interface SendUniversityName {
        void sendData(String universityName);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sendName = (SendUniversityName) getActivity();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first_setting, container, false);

        reference = FirebaseDatabase.getInstance().getReference("universities");

        searchField = rootView.findViewById(R.id.search_field);
        listView = rootView.findViewById(R.id.universities_list);

        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, universitiesList);
        listView.setAdapter(adapter);

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showUniversities("");

        //showing universities that match your search
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showUniversities(searchField.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //getting key value of chosen university from database
        //and sending it to GroupFragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String university = universitiesKeyList.get(position);
                sendName.sendData(university);
                //TimeManager time = new TimeManager(getActivity().getApplication());
                //time.getTimetableFromFirestore(university);
            }
        });
    }


    //retrieving processData (available universities list) from Firebase database
    private void showUniversities(final String search) {

        //clearing old lists before showing new
        universitiesKeyList.clear();
        universitiesList.clear();

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if (Objects.equals(ds.getKey(), "name") && search.equals("")) {
                        universitiesList.add(ds.getValue(String.class));
                        universitiesKeyList.add(dataSnapshot.getKey());

                    } else if (Objects.equals(ds.getKey(), "name") &&
                            Objects.requireNonNull(ds.getValue(String.class)).toLowerCase().contains(search)) {
                        universitiesList.add(ds.getValue(String.class));
                        universitiesKeyList.add(dataSnapshot.getKey());
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
