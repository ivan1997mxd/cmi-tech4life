package com.estethapp.media.covid.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.estethapp.media.R;
import com.estethapp.media.fragment.AbouteSteth;

public class MainFragment extends Fragment
{
    private Fragment CurrentChild;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public Fragment getChild()
    {
        return CurrentChild;
    }

    public void setChild(Fragment fragment)
    {
        final FragmentManager manager = getFragmentManager();

        if (manager != null)
        {
            manager.beginTransaction()
                    .replace(R.id.ContentRoot, fragment)
                    .commitNow();
        }

        CurrentChild = fragment;
    }
}
