package com.QuizActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.Data.MyServerData;

public class QuestionPagerAdapter extends FragmentStatePagerAdapter {

    public QuestionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        QuestionFragment questionFragment = new QuestionFragment();
        questionFragment.setArguments(bundle);
        return questionFragment;
    }

    @Override
    public int getCount() {
        return MyServerData.getInstance().getTotalQuestions()+2;
    };
}
