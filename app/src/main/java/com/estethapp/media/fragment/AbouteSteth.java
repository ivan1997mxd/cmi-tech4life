package com.estethapp.media.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.crashlytics.android.Crashlytics;
import com.estethapp.media.R;
import com.estethapp.media.helper.ExminatingHelper;
import io.fabric.sdk.android.Fabric;

public class AbouteSteth extends Fragment{
    AbouteSteth fragment;
    Context context;
    static AbouteSteth instance;
    View root;

    WebView aboutWebView;

    public static FragmentManager fragmentManager;

    ExminatingHelper exminatingHelper = ExminatingHelper.getInstance();


    public AbouteSteth() {
    }

    public static AbouteSteth newInstance(Bundle bundle) {
        AbouteSteth instance = new AbouteSteth();
        return instance;
    }

    public static AbouteSteth getInstance() {
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
        return inflater.inflate(R.layout.about_esteth, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        root = view;
        fragment = this;
        Fabric.with(context, new Crashlytics());

        aboutWebView = (WebView) root.findViewById(R.id.aboutwebView);
        aboutWebView.setVerticalScrollBarEnabled(false);

        String summary = "<html><body style='text-align: justify; text-justify: distribute;'> eSteth is a world class digital stethoscope, designed by Tech4Life Enterprises which is a socially motivated innovative research and design company, specialized in telemedicine & point-of-care devices. Tech4Life Enterprises designs innovative technology solutions to empower the health providers and ensure affordable access to healthcare globally.\n" +
                "eSteth is one of the innovative products in healthcare technology which provides high quality heart & lung sounds to physicians & nurses for better diagnosis. It is setting new standards with 100x amplification of heart & lung sounds. eSteth is capable of state-of-the-art support for Telemedicine; you can make the most of eSteth by performing real-time transfer of heart-sounds. You can connect your eSteth with your Smartphone, Tablet & PC via Bluetooth or 3.5mm audio cable.</br></br>" +
                "For more information about eSteth, you can visit: <font color='blue'><a href='http://tech4lifeenterprises.com/esteth-products/'>http://tech4lifeenterprises.com/esteth-products/</a></font> or to know more about the amazing team behind eSteth, visit: </br><font color='blue'><a href='http://tech4lifeenterprises.com/'>www.tech4lifeenterprises.com</a></font></br></br>" +
                "<b>Contact us:</b></br>" +
                "For queries & suggestions, kindly write to us at: <font color='blue'><a href='mailto:info@eSteth.com'>info@eSteth.com</a></font></br>" +
                "eSteth ®© 2020 – Sounds Beyond Boundaries </body></html>";

        aboutWebView.loadData(summary, "text/html; charset=utf-8", "utf-8");

    }


}
