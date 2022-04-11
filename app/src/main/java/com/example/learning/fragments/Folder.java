package com.example.learning.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.learning.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Folder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Folder extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView folder_viewer;
    FolderAdapter adapter;
    private View rootView;
    private SearchView searchView;
    private ImageView addFolder;
    private View addView;
    List<FolderItem> folderList = new ArrayList<FolderItem>();

    public Folder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Folder.
     */
    // TODO: Rename and change types and number of parameters
    public static Folder newInstance(String param1, String param2) {
        Folder fragment = new Folder();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView != null){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null){
                parent.removeView(rootView);
            }
        }
        rootView = inflater.inflate(R.layout.fragment_folder, container, false);
        addView = inflater.inflate(R.layout.create_folder,container,false);
        addView.setBackgroundColor(Color.WHITE);
        folder_viewer = rootView.findViewById(R.id.foler_viewer);
        searchView = rootView.findViewById(R.id.folder_search);
        addFolder = rootView.findViewById(R.id.folder_add);

        for (int i = 0; i < 10; i++){
            folderList.add(new FolderItem("Folder" + Integer.toString(i), "It is a long long long long long long long long long example of description"));
        }
        Context context = getActivity();
        adapter = new FolderAdapter(folderList);
        folder_viewer.setAdapter(adapter);
        folder_viewer.setLayoutManager(new LinearLayoutManager(context));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.filter(s);
                return true;
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
        final AlertDialog dialog = builder.create();
        // dialog.setTitle("Add folder");

        dialog.setView(addView);
        addFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                TextView cancel = addView.findViewById(R.id.add_folder_cancel);
                TextView save = addView.findViewById(R.id.add_folder_save);
                final EditText title = addView.findViewById(R.id.create_folder_input);
                EditText description = addView.findViewById(R.id.create_folder_description);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return rootView;
    }
}
