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
import com.estethapp.media.covid.selfassessment.AssessmentBeginFragment;
import com.estethapp.media.covid.selfassessment.AssessmentEndFragment;
import com.estethapp.media.covid.selfassessment.Question;
import com.estethapp.media.covid.selfassessment.QuestionFragment;
import com.estethapp.media.covid.selfassessment.Response;

import java.util.ArrayList;

public class SelfAssessmentFragment extends Fragment {
    private HubFragment Parent;
    private Question[] Questions;
    private Response[] Responses;

    public SelfAssessmentFragment(HubFragment parent) {
        Parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.onboarding_fragment, container, false);
        final FragmentManager manager = getFragmentManager();
        final Fragment indicatorsFragment = getChildFragmentManager().findFragmentById(R.id.DotIndicators);
        final ViewPager pager = fragment.findViewById(R.id.ViewPager);

        loadQuestions();

        if (manager != null && indicatorsFragment instanceof CarouselIndicators) {
            final CarouselIndicators indicators = (CarouselIndicators) indicatorsFragment;

            final ViewPagerAdapter adapter = new ViewPagerAdapter(manager);

            adapter.addChild(new AssessmentBeginFragment());

            Responses = new Response[Questions.length];

            for (int i = 0; i < Questions.length; i++) {
                final Question question = Questions[i];
                final Response response = new Response();

                adapter.addChild(new QuestionFragment(question.getTitle(), question.getContent(), response));

                Responses[i] = response;
            }

            adapter.addChild(new AssessmentEndFragment(this));

            final int pages = adapter.getCount();

            pager.setAdapter(adapter);

            if (pages > 0) {
                indicators.setPageCount(pages);

                pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        indicators.setPage(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            }
        }

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public Response[] getResponses() {
        return Responses;
    }

    public void finishAssessment() {
        Parent.finishTask();
    }

    private void loadQuestions() {
        final String[] values = getResources().getStringArray(R.array.assessment_questions);
        Questions = new Question[values.length];

        for (int i = 0; i < values.length; i++) {
            final String[] fields = values[i].split("\u001E");

            switch (fields.length) {
                case 0:
                    Questions[i] = new Question("", "");
                    break;
                case 1:
                    Questions[i] = new Question(fields[0], "");
                    break;
                default:
                    Questions[i] = new Question(fields[0], fields[1]);
                    break;
            }
        }
    }

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final ArrayList<Fragment> Children = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addChild(Fragment fragment) {
            Children.add(fragment);
        }

        public void addChildren(Iterable<Fragment> fragments) {
            for (Fragment fragment : fragments) {
                addChild(fragment);
            }
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return Children.get(position);
        }

        @Override
        public int getCount() {
            return Children.size();
        }
    }
}
