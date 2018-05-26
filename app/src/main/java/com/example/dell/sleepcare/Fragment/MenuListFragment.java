package com.example.dell.sleepcare.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.sleepcare.R;


public class MenuListFragment extends Fragment {

       public MenuListFragment() {
        // Required empty public constructor
    }


    public static MenuListFragment newInstance() {
        MenuListFragment fragment = new MenuListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    NavigationView vNavigation;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_list, container,
                false);
        SharedPreferences sp = view.getContext().getSharedPreferences("userData", Context.MODE_PRIVATE);
        String userName = sp.getString("name","");


        vNavigation = view.findViewById(R.id.vNavigation);
        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast.makeText(getActivity(),item.getTitle(),Toast.LENGTH_SHORT).show();
                Log.d("menuItenSelected", item.getTitle().toString());
                return false;
            }
        });
        return  view;
    }

    @Override
    public void onAttach(Context context) {
        Log.d("onAttach", "fragment attched"+context.toString());
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d("onDetach", "fragment detatached");
        super.onDetach();
    }

}
