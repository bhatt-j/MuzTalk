package com.example.muztalk.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muztalk.Adapters.AdapterUsers;
import com.example.muztalk.Models.ModelUsers;
import com.example.muztalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersearchFragment extends Fragment {
    ProgressBar PROGRESSBAR;
    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUsers> usersList;
    EditText SEARCH_USER;
    public UsersearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_usersearch, container, false);

        recyclerView = view.findViewById(R.id.users_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                                                                        //init user list
        usersList = new ArrayList<>();
        PROGRESSBAR=view.findViewById(R.id.progressBar);
        PROGRESSBAR.setVisibility(View.GONE);                                                                                          //getalluser
        getAllUsers();

        SEARCH_USER = view.findViewById(R.id.search_user);

        SEARCH_USER.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search_user(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private void search_user(String s) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);

                    if (!modelUsers.getId().equals(fuser.getUid())) {
                        usersList.add(modelUsers);
                    }
                    else
                    {
                        Toast.makeText(getContext(),"no user found",Toast.LENGTH_SHORT).show();
                    }
                    adapterUsers = new AdapterUsers(getActivity(), usersList);
                    recyclerView.setAdapter(adapterUsers);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

            private void getAllUsers() {
                //get current user
                PROGRESSBAR.setVisibility(View.VISIBLE);
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                //get data from path
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                            if (!modelUsers.getId().equals(fUser.getUid())) {
                                usersList.add(modelUsers);
                            }
                            PROGRESSBAR.setVisibility(View.GONE);
                            adapterUsers = new AdapterUsers(getActivity(), usersList);
                            recyclerView.setAdapter(adapterUsers);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
