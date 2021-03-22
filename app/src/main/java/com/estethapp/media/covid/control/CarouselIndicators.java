package com.estethapp.media.covid.control;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.estethapp.media.R;

public class CarouselIndicators extends Fragment
{
    private LinearLayout Layout;
    private int PageIndex = 0;
    private int PageCount = 0;

    public static CarouselIndicators newInstance()
    {
        return new CarouselIndicators();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View indicators = inflater.inflate(R.layout.carousel_fragment, container, false);

        Layout = indicators.findViewById(R.id.DotLayout);

        return indicators;
    }

    public void setPage(int index)
    {
        if (index >= PageCount || index < 0)
        {
            throw new IndexOutOfBoundsException();
        }

        PageIndex = index;

        final Activity activity = getActivity();

        if (activity != null)
        {
            final Drawable selected = ContextCompat.getDrawable(activity, R.drawable.selected);
            final Drawable unselected = ContextCompat.getDrawable(activity, R.drawable.unselected);

            for (int i = 0; i < PageCount; i++)
            {
                final View child = Layout.getChildAt(i);

                if (child instanceof ImageView)
                {
                    final ImageView image = (ImageView)child;

                    if (i == index)
                    {
                        image.setImageDrawable(selected);
                    }
                    else
                    {
                        image.setImageDrawable(unselected);
                    }
                }
            }
        }
    }

    public void setPageCount(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException();
        }

        Layout.removeAllViews();

        PageCount = count;

        final int margin = getResources().getDimensionPixelSize(R.dimen.carousel_indicator_margin);

        for (int i = 0; i < count; i++)
        {
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final ImageView image = new ImageView(getContext());

            layoutParams.setMargins(margin, 0, margin, 0);

            image.setLayoutParams(layoutParams);

            Layout.addView(image);
        }

        setPage(PageIndex);
    }
}
