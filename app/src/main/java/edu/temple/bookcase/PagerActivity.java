package edu.temple.bookcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import java.util.ArrayList;

public class PagerActivity extends AppCompatActivity {
    ViewPager viewPager;
    ArrayList fragments;
    MyViewPagerAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        fragments = new ArrayList<BookDetailsFragment>();

        for(String baseTitle : getResources().getStringArray(R.array.book_list)){
            fragments.add(BookDetailsFragment.newInstance(baseTitle));
        }

        viewPager = findViewById(R.id.viewPager);

        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(myViewPagerAdapter);
    }

    class MyViewPagerAdapter extends FragmentStatePagerAdapter{

        ArrayList<BookDetailsFragment> fragments;

        public MyViewPagerAdapter(@NonNull FragmentManager fm, ArrayList<BookDetailsFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position % fragments.size());
        }

        @Override
        public int getCount() {
            return fragments.size()+4;
        }
    }
}
