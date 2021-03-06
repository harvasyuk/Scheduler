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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.scheduler.R;

import java.util.ArrayList;
import java.util.Objects;


public class UniversityFragment extends Fragment {

    private EditText searchField;
    private ListView listView;
    private ArrayList<String> universitiesList = new ArrayList<>();
    private ArrayList<String> universitiesKeyList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private SharedPreferences sharedPreferences;
    private SharedViewModel model;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first_setting, container, false);

        firestore = FirebaseFirestore.getInstance();

        searchField = rootView.findViewById(R.id.search_field);
        listView = rootView.findViewById(R.id.universities_list);

        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, universitiesList);
        listView.setAdapter(adapter);

        model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

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
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String university = universitiesKeyList.get(position);
            model.selectUniversity(university);

            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    getString(R.string.common_preferences), Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.university_name), university);
            editor.apply();
        });
    }


    //retrieving processData (available universities list) from Firebase database
    private void showUniversities(final String search) {

        firestore.collection("universities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        universitiesKeyList.clear();
                        universitiesList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (search.equals("")) {
                                universitiesKeyList.add(document.getId());
                                universitiesList.add(document.getString("name"));

                            } else if (document.getString("name").contains(search)) {
                                universitiesKeyList.add(document.getId());
                                universitiesList.add(document.getString("name"));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("UniversityFragment", "Error getting documents: ", task.getException());
                    }
                });
    }

}
