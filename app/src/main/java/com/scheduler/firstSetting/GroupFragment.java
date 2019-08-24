package com.scheduler.firstSetting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.scheduler.R;

import java.util.ArrayList;
import java.util.Objects;

public class GroupFragment extends Fragment {

    private FirebaseFirestore firestore;
    private EditText searchField;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> groupList = new ArrayList<>();
    private String university;
    private String department;


    private void setGroupKey(String universityName) {
        university = universityName;
    }

    private void setDepartmentKey(String departmentKey) {
        department = departmentKey;
        showGroups("");
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first_setting, container, false);

        firestore = FirebaseFirestore.getInstance();

        searchField = rootView.findViewById(R.id.search_field);
        listView = rootView.findViewById(R.id.universities_list);

        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, groupList);
        listView.setAdapter(adapter);

        SharedViewModel model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        model.getSelectedUniversity().observe(this, this::setGroupKey);
        model.getSelectedDepartment().observe(this, this::setDepartmentKey);

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (department != null) showGroups("");

        //showing groups that match your search
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (department != null) showGroups(searchField.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //setting up the chosen group
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    getString(R.string.common_preferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.group_name), groupList.get(position));
            editor.apply();
        });
    }


    //retrieving data (available groups list) from Firebase database
    private void showGroups(final String search) {

        firestore.collection("universities").document(university).collection("departments")
                .document(department).collection("groups")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        groupList.clear();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (search.equals("")) {
                                    groupList.add(document.getId());

                                } else if (document.getId().contains(search)) {
                                    groupList.add(document.getId());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("GroupFragment", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
