package com.estethapp.media.covid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.estethapp.media.R;
import com.estethapp.media.covid.device.PendingPermission;
import com.estethapp.media.covid.device.SaveData;
import com.estethapp.media.covid.ui.HubFragment;
import com.estethapp.media.covid.ui.MainFragment;
import com.estethapp.media.covid.ui.OnboardingFragment;
import com.estethapp.media.fragment.AbouteSteth;

import java.util.ArrayList;

public class BlueToothActivity extends AppCompatActivity {

    private final ArrayList<PendingPermission> PendingPermissions = new ArrayList<>();

    public void addPendingPermission(PendingPermission permission)
    {
        PendingPermissions.add(permission);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);

        if (savedInstanceState == null)
        {
            final MainFragment fragment = new MainFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.container_main, fragment).commitNow();
            fragment.setChild(new HubFragment(fragment));
            if (SaveData.IsLaunched(this))
            {
                fragment.setChild(new HubFragment(fragment));
            }
            else
            {
//                addShortcutToHomeScreen(this);
                fragment.setChild(new OnboardingFragment(fragment));
            }
        }
    }
    public static void addShortcutToHomeScreen(Context context)
    {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context))
        {
            ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(context, "#1")
                    .setIntent(new Intent(context, BlueToothActivity.class).setAction(Intent.ACTION_MAIN)) // !!! intent's action must be set on oreo
                    .setShortLabel("T4L Triage Assistant")
                    .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_icon))
                    .build();
            ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null);
        }
        else
        {
            // Shortcut is not supported by your launcher
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (grantResults.length == 0) return;

        PendingPermission toRemove = null;

        for (PendingPermission perm : PendingPermissions)
        {
            if (perm.getId() == requestCode)
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    perm.accept();
                }
                else
                {
                    perm.deny();
                }

                toRemove = perm;
                break;
            }
        }

        if (toRemove != null) PendingPermissions.remove(toRemove);
    }
}