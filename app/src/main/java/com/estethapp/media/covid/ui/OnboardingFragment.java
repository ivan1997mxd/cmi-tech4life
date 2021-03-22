package com.estethapp.media.covid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.estethapp.media.R;
import com.estethapp.media.covid.control.CarouselIndicators;
import com.estethapp.media.covid.device.SaveData;
import com.estethapp.media.covid.onboarding.Bluetooth;
import com.estethapp.media.covid.onboarding.SetupCompleted;
import com.estethapp.media.covid.onboarding.Storage;

import java.util.ArrayList;

public class OnboardingFragment extends Fragment
{
    private MainFragment Parent;

    public OnboardingFragment(MainFragment parent)
    {
        Parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View fragment = inflater.inflate(R.layout.onboarding_fragment, container, false);
        final FragmentManager manager = getFragmentManager();
        final Fragment indicatorsFragment = getChildFragmentManager().findFragmentById(R.id.DotIndicators);
        final ViewPager pager = fragment.findViewById(R.id.ViewPager);

        if (manager != null && indicatorsFragment instanceof CarouselIndicators)
        {
            final CarouselIndicators indicators = (CarouselIndicators)indicatorsFragment;

            final ViewPagerAdapter adapter = new ViewPagerAdapter(manager);
            adapter.addChild(new Bluetooth());
            adapter.addChild(new Storage());
            adapter.addChild(new SetupCompleted(this));

            final int pages = adapter.getCount();

            pager.setAdapter(adapter);

            if (pages > 0)
            {
                indicators.setPageCount(pages);

                pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
                {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                    @Override
                    public void onPageSelected(int position)
                    {
                        indicators.setPage(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {}
                });
            }
        }

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    public void setupFinished()
    {
        SaveData.SetLaunched(getContext(),true);

        Parent.setChild(new HubFragment(Parent));
    }

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter
    {
        private final ArrayList<Fragment> Children = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm)
        {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addChild(Fragment fragment)
        {
            Children.add(fragment);
        }

        public void addChildren(Iterable<Fragment> fragments)
        {
            for (Fragment fragment : fragments)
            {
                addChild(fragment);
            }
        }

        @NonNull
        @Override
        public Fragment getItem(int position)
        {
            return Children.get(position);
        }

        @Override
        public int getCount()
        {
            return Children.size();
        }
    }
}