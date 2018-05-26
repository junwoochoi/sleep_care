package com.example.dell.sleepcare.Fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.Adapter.TestPagerAdapter;
import com.example.dell.sleepcare.Model.CardItem;
import com.example.dell.sleepcare.Model.PSQIScore;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.ShadowTransformer;

import java.util.ArrayList;
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
    @BindView(R.id.button_next_test)
    MaterialButton buttonNextTest;

    private ShadowTransformer mCardShadowTransformer;
    private TestPagerAdapter mCardAdapter;
    HashMap hashMap;

    private EditText editText;
    private ArrayList<String> answerResults = new ArrayList<>();

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

        buttonNextTest.setTag("nextBtn");
        hashMap = new HashMap<String, String>();

        mCardAdapter = new TestPagerAdapter();
        setQuestion();

        mCardShadowTransformer = new ShadowTransformer(viewPager, mCardAdapter);

        viewPager.setAdapter(mCardAdapter);
        viewPager.setPageTransformer(false, mCardShadowTransformer);
        viewPager.setOffscreenPageLimit(18);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==18){
                    buttonNextTest.setText("제출");
                } else {
                    buttonNextTest.setText("다음");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                if(buttonNextTest.getText().equals("제출")){
                    if(onGetResult()!=null){
                        answerResults = onGetResult();
                        PSQIScore psqiScore = new PSQIScore(answerResults);

                        AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                        dialog.setTitle("PSQI 결과 확인")
                                .setMessage("체크하신 문항의 결과를 확인하시겠습니까?")
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FragmentManager fm = getFragmentManager();
                                        TestResultFragment fragment = TestResultFragment.newInstance(psqiScore);
                                        fragment.show(fm, "TestResult");
                                    }
                                }).create().show();
                        Log.e("받은 문항 대답:", onGetResult().toString());

                    } else {
                        Toast.makeText(getContext(), "답하지 않은 답변이 존재합니다. 다시 시도해주십시오", Toast.LENGTH_LONG).show();
                    }
                }

                break;
            case R.id.button_prev_test:
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

                break;
        }
    }

    ArrayList<String> onGetResult(){
        View view;
        EditText editText;
        ArrayList<String> answers = new ArrayList<>();
        RadioGroup radioGroup;
        for(int i=0; i<19; i++){
            if(i<4){
                view = viewPager.getChildAt(i);
                editText = view.findViewById(R.id.edit_text_test);
                if(editText.getText()==null || editText.getText().toString().equals("")){
                    Log.e("null들어온 부분: ", String.valueOf(i));
                    return null;
                } else {
                answers.add(editText.getText().toString());
            } }else if(i>4){
                view = viewPager.getChildAt(i);
                radioGroup = view.findViewById(R.id.radiogroup);
                if(radioGroup.getCheckedRadioButtonId()==-1) {
                    Log.e("null들어온 부분", String.valueOf(i));
                } else {
                    answers.add(String.valueOf(radioGroup.getCheckedRadioButtonId()));
                }
                 }
        }
        return answers;
    }
}
