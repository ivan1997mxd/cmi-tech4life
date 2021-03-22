/*
 * Copyright (c)  2017  Francisco José Montiel Navarro.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.estethapp.media.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.estethapp.media.R;
import com.estethapp.media.adapter.LanguageAdapter;

public class ChangeLanguageFragment extends Fragment {

    Context context;
    static ChangeLanguageFragment instance;
    private ListView lv;

    private String[] languages = new String[]{"English", "Español"};

    public ChangeLanguageFragment() {
    }

    public static ChangeLanguageFragment newInstance(Bundle bundle) {
        ChangeLanguageFragment instance = new ChangeLanguageFragment();
        return instance;
    }

    public static ChangeLanguageFragment getInstance() {
        return instance;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        return inflater.inflate(R.layout.activity_change_language, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getActivity();

        lv = view.findViewById(R.id.listView);
        LanguageAdapter adapter = new LanguageAdapter(getContext(), getActivity(), languages);
        lv.setAdapter(adapter);

    }

}
