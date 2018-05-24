package com.example.dell.sleepcare.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.Adapter.TestPagerAdapter;
import com.example.dell.sleepcare.Model.CardItem;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.ShadowTransformer;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class TestFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.test_fragment_menu)
    ImageView testFragmentMenu;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private ShadowTransformer mCardShadowTransformer;
    private TestPagerAdapter mCardAdapter;
    HashMap hashMap;

    public TestFragment() {
        // Required empty public constructor
    }


    public static TestFragment newInstance() {
        TestFragment fragment = new TestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_test, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        hashMap = new HashMap<String, String>();

        mCardAdapter = new TestPagerAdapter();
        setQuestion();

        mCardShadowTransformer = new ShadowTransformer(viewPager, mCardAdapter);

        viewPager.setAdapter(mCardAdapter);
        viewPager.setPageTransformer(false, mCardShadowTransformer);

        return rootView;
    }

    private void setQuestion() {
        hashMap.put("a-1", Objects.requireNonNull(getContext()).getResources().getString(R.string.test_answer_a_1));
        hashMap.put("a-2", getContext().getResources().getString(R.string.test_answer_a_2));
        hashMap.put("a-3", getContext().getResources().getString(R.string.test_answer_a_3));
        hashMap.put("a-4", getContext().getResources().getString(R.string.test_answer_a_4));
        hashMap.put("b-1", getContext().getResources().getString(R.string.test_answer_b_1));
        hashMap.put("b-2", getContext().getResources().getString(R.string.test_answer_b_2));
        hashMap.put("b-3", getContext().getResources().getString(R.string.test_answer_b_3));
        hashMap.put("b-4", getContext().getResources().getString(R.string.test_answer_b_4));
        hashMap.put("c-1", getContext().getResources().getString(R.string.test_answer_c_1));
        hashMap.put("c-2", getContext().getResources().getString(R.string.test_answer_c_2));
        hashMap.put("c-3", getContext().getResources().getString(R.string.test_answer_c_3));
        hashMap.put("c-4", getContext().getResources().getString(R.string.test_answer_c_4));
        hashMap.put("d-1", getContext().getResources().getString(R.string.test_answer_d_1));
        hashMap.put("d-2", getContext().getResources().getString(R.string.test_answer_d_2));
        hashMap.put("d-3", getContext().getResources().getString(R.string.test_answer_d_3));
        hashMap.put("d-4", getContext().getResources().getString(R.string.test_answer_d_4));


        mCardAdapter.addCardItem(new CardItem(R.string.test_question_1, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_2, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_3, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_4, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_a, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_b, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_c, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_d, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_e, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_f, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_g, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_h, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_i, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_j, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_6, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_7, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_8, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_9, hashMap));
        mCardAdapter.addCardItem(new CardItem(R.string.test_question_10, hashMap));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.test_fragment_menu)
    public void onViewClicked() {
        ((MainActivity) Objects.requireNonNull(getActivity())).mDrawer.openMenu();
    }

    @OnClick({R.id.button_next_test, R.id.button_prev_test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_next_test:
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                break;
            case R.id.button_prev_test:
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
                break;
        }
    }
}
