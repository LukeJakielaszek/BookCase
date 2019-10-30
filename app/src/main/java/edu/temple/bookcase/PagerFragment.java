package edu.temple.bookcase;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagerFragment extends Fragment {
    private ViewPager viewPager;
    private ArrayList fragments;
    private MyViewPagerAdapter myViewPagerAdapter;
    private ArrayList<String> bookList;


    public static final String BOOK_LIST_KEY = "book_list";

    public PagerFragment() {
        // Required empty public constructor
    }

    public static PagerFragment newInstance(ArrayList<String> bookList){
        PagerFragment pf = new PagerFragment();

        // set our book list array
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BOOK_LIST_KEY, bookList);
        pf.setArguments(bundle);

        return pf;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get our book list array
        if(getArguments() != null){
            this.bookList = getArguments().getStringArrayList(BOOK_LIST_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pager, container, false);

        fragments = new ArrayList<BookDetailsFragment>();

        for(String baseTitle : this.bookList){
            fragments.add(BookDetailsFragment.newInstance(baseTitle));
        }

        viewPager = v.findViewById(R.id.viewPager);

        myViewPagerAdapter = new MyViewPagerAdapter(getFragmentManager(), fragments);

        viewPager.setAdapter(myViewPagerAdapter);

        return v;
    }

    class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<BookDetailsFragment> fragments;

        public MyViewPagerAdapter(@NonNull FragmentManager fm, ArrayList<BookDetailsFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
