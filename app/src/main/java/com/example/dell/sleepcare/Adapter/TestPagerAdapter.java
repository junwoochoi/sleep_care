package com.example.dell.sleepcare.Adapter;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.dell.sleepcare.Model.CardItem;
import com.example.dell.sleepcare.R;

import java.util.ArrayList;
import java.util.List;

public class TestPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;

    public TestPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_test_cardview, container, false);
        container.addView(view);
        bind(mData.get(position), view, position);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    @Nullable
    @Override
    public Parcelable saveState() {
        return super.saveState();
    }

    private void bind(CardItem item, View view, int position) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        LinearLayout inputLayout = view.findViewById(R.id.date_input_layout_test);
        EditText editText = view.findViewById(R.id.edit_text_test);
        RadioGroup radioGroup = view.findViewById(R.id.radiogroup);
        RadioButton radioButton1 = view.findViewById(R.id.radiobutton_1);
        RadioButton radioButton2 = view.findViewById(R.id.radiobutton_2);
        RadioButton radioButton3 = view.findViewById(R.id.radiobutton_3);
        RadioButton radioButton4 = view.findViewById(R.id.radiobutton_4);
        radioGroup.setTag("rg"+ position);

        titleTextView.setText(item.getQuestion());
        if(position<4){
            editText.setTag("et"+position);
            radioGroup.setVisibility(View.GONE);
            inputLayout.setVisibility(View.VISIBLE);
        }
        else if( position > 3 && position <14 || position==15 || position==16) {
            inputLayout.setVisibility(View.GONE);
            radioGroup.setVisibility(View.VISIBLE);
            radioButton1.setText(item.getmHashMap().get("a-1"));
            radioButton2.setText(item.getmHashMap().get("a-2"));
            radioButton3.setText(item.getmHashMap().get("a-3"));
            radioButton4.setText(item.getmHashMap().get("a-4"));
        } else if( position == 14){
            inputLayout.setVisibility(View.GONE);
            radioGroup.setVisibility(View.VISIBLE);
            radioButton1.setText(item.getmHashMap().get("b-1"));
            radioButton2.setText(item.getmHashMap().get("b-2"));
            radioButton3.setText(item.getmHashMap().get("b-3"));
            radioButton4.setText(item.getmHashMap().get("b-4"));
        } else if( position ==17) {
            inputLayout.setVisibility(View.GONE);
            radioGroup.setVisibility(View.VISIBLE);
            radioButton1.setText(item.getmHashMap().get("c-1"));
            radioButton2.setText(item.getmHashMap().get("c-2"));
            radioButton3.setText(item.getmHashMap().get("c-3"));
            radioButton4.setText(item.getmHashMap().get("c-4"));
        } else {
            inputLayout.setVisibility(View.GONE);
            radioGroup.setVisibility(View.VISIBLE);
            radioButton1.setText(item.getmHashMap().get("d-1"));
            radioButton2.setText(item.getmHashMap().get("d-2"));
            radioButton3.setText(item.getmHashMap().get("d-3"));
            radioButton4.setText(item.getmHashMap().get("d-4"));
        }
    }

}
