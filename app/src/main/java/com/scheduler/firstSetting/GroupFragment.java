package com.scheduler.firstSetting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scheduler.R;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class GroupFragment extends Fragment {

    private DatabaseReference reference;
    private FirebaseDatabase database;
    private EditText searchField;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> groupList = new ArrayList<>();
    private String groupKey;


    void setGroupKey(String universityName)
    {
        groupKey = universityName;
    }


    //this method is used to saveItems the reference after group has been chosen
    void updateReference() {
        reference = database.getReference("universities/" + groupKey + "/group");
        showGroups("");
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first_setting, container, false);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("universities/" + groupKey + "/group");

        searchField = rootView.findViewById(R.id.search_field);
        listView = rootView.findViewById(R.id.universities_list);

        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, groupList);
        listView.setAdapter(adapter);

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showGroups("");

        //showing groups that match your search
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                showGroups(searchField.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //setting up the chosen group
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("groupListPreference", groupList.get(position));
                editor.apply();
            }
        });
    }


    //retrieving data (available groups list) from Firebase database
    private void showGroups(final String search) {
        //clearing old list before showing the new one
        groupList.clear();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (search.equals("")) {
                        groupList.add(ds.getKey());

                    } else if (Objects.requireNonNull(ds.getKey()).contains(search)) {
                        groupList.add(ds.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
}
