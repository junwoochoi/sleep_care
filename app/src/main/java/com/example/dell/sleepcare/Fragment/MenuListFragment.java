package com.example.dell.sleepcare.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.Activitity.SettingsActivity;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.Utils.SharedPrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MenuListFragment extends Fragment implements MainActivity.OnBackPressedListener {

    @BindView(R.id.vNavigation)
    NavigationView vNavigation;
    Unbinder unbinder;

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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_list, container,
                false);

        SharedPreferences sp = SharedPrefUtils.getInstance(view.getContext()).getPrefs();

        vNavigation = view.findViewById(R.id.vNavigation);


        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fm = getFragmentManager();
                switch (item.getItemId()) {
                    case 2131296473:
                        Intent intent = new Intent(getContext(), SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case 2131296470:
                        ((MainActivity) getActivity()).onGraphClicked();
                        break;
                    case 2131296474:
                        ((MainActivity) getActivity()).onTestClicked();
                        break;
                    case 2131296472:
                        ((MainActivity) getActivity()).onTestResultClicked();
                        break;
                }
                ((MainActivity) getActivity()).mDrawer.closeMenu(true);

                return false;
            }
        });
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        Log.d("onAttach", "fragment attched" + context.toString());
        super.onAttach(context);
        ((MainActivity) context).setOnBackPressedListener(this);

    }

    @Override
    public void onDetach() {
        Log.d("onDetach", "fragment detatached");
        super.onDetach();
    }

    @Override
    public void onBack() {
        Log.e("Other", "onBack()");
        // 리스너를 설정하기 위해 Activity 를 받아옵니다.
        MainActivity activity = (MainActivity) getActivity();
        // 한번 뒤로가기 버튼을 눌렀다면 Listener 를 null 로 해제해줍니다.
        activity.setOnBackPressedListener(null);

        ((MainActivity) getActivity()).mainContentLayout.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).mainFragmentContainer.setVisibility(View.GONE);

        // Activity 에서도 뭔가 처리하고 싶은 내용이 있다면 하단 문장처럼 호출해주면 됩니다.
        // activity.onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

