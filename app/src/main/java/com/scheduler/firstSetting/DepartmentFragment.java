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

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class DepartmentFragment extends Fragment {

    private FirebaseFirestore firestore;
    private EditText searchField;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> departmentList = new ArrayList<>();
    private String university;
    private SharedViewModel model;


    private void setUniversityKey(String universityKey) {
        university = universityKey;
        showDepartments("");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first_setting, container, false);

        firestore = FirebaseFirestore.getInstance();

        searchField = rootView.findViewById(R.id.search_field);
        listView = rootView.findViewById(R.id.universities_list);

        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, departmentList);
        listView.setAdapter(adapter);

        model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        model.getSelectedUniversity().observe(this, this::setUniversityKey);

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //showing groups that match your search
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (university != null) showDepartments(searchField.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //setting up the chosen group
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String department = departmentList.get(position);
                model.selectDepartment(department);

                SharedPreferences sharedPref = getActivity().getSharedPreferences(
                        getString(R.string.common_preferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.department_name), departmentList.get(position));
                editor.apply();
            }
        });
    }


    //retrieving data (available departments list) from Firebase database
    private void showDepartments(final String search) {

        firestore.collection("universities").document(university).collection("departments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        departmentList.clear();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (search.equals("")) {
                                    departmentList.add(document.getId());

                                } else if (document.getId().contains(search)) {
                                    departmentList.add(document.getId());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
