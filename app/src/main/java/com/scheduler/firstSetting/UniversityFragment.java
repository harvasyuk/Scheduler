package com.scheduler.firstSetting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.scheduler.R;
import com.scheduler.logic.TimeManager;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class UniversityFragment extends Fragment {

    private SendUniversityName sendName;

    private EditText searchField;
    private ListView listView;
    private ArrayList<String> universitiesList = new ArrayList<>();
    private ArrayList<String> universitiesKeyList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private SharedPreferences sharedPreferences;


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

        firestore = FirebaseFirestore.getInstance();

        searchField = rootView.findViewById(R.id.search_field);
        listView = rootView.findViewById(R.id.universities_list);

        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_list_item_1, universitiesList);
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

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("universityName", university);
                editor.apply();
            }
        });
    }


    //retrieving processData (available universities list) from Firebase database
    private void showUniversities(final String search) {

        firestore.collection("universities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
