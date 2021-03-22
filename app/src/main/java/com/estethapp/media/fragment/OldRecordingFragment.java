package com.estethapp.media.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.appcompat.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.estethapp.media.ActivityNewPlayer;
import com.estethapp.media.R;
import com.estethapp.media.adapter.FileArrayAdapter;
import com.estethapp.media.helper.Item;
import com.estethapp.media.util.Prefs;
import com.estethapp.media.util.UserModel;
import com.estethapp.media.util.Util;
import com.google.gson.Gson;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class OldRecordingFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    OldRecordingFragment fragment;
    Context context;
    static OldRecordingFragment instance;
    public UserModel currentUser;
    View root;
    private File currentDir;
    private FileArrayAdapter adapter;
    TextView textViewName, textViewPassword, textViewGender;
    GridView fileGridView;
    UserModel profileModel;
    private String selectedFilePath;
    private String selectedFileName;

    private SharedPreferences prefsInstance;
    private Prefs prefs;

    List<Item> dir = new ArrayList<Item>();
    List<Item> fls = new ArrayList<Item>();

    public OldRecordingFragment() {
        // Required empty public constructor
    }

    public static OldRecordingFragment newInstance(Bundle bundle) {
        OldRecordingFragment instance = new OldRecordingFragment();
        return instance;
    }

    public static OldRecordingFragment getInstance() {
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        return inflater.inflate(R.layout.file_view, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        root = view;
        fragment = this;
        Fabric.with(context, new Crashlytics());
        fileGridView = (GridView) root.findViewById(R.id.fileGrid_view);

        String recordFileDir = Util.getDirectory();
        currentDir = new File(recordFileDir);
        fill(currentDir);


        prefs = Prefs.getInstance();
        prefsInstance = prefs.init(context);

        Gson gson = new Gson();
        String json = prefsInstance.getString(prefs.USERMODEL, "");
        profileModel = gson.fromJson(json, UserModel.class);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        dir.clear();
        fls.clear();

        String recordFileDir = Util.getDirectory();
        currentDir = new File(recordFileDir);
        fill(currentDir);
    }


    private void fill(File f) {
        File[] dirs = f.listFiles();
        //  List<Item> dir = new ArrayList<Item>();
        //   List<Item>fls = new ArrayList<Item>();
        try {
            for (File ff : dirs) {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if (ff.isDirectory()) {


                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if (fbuf != null) {
                        buf = fbuf.length;
                    } else buf = 0;
                    String num_item = String.valueOf(buf);
                    if (buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    //String formated = lastModDate.toString();
                    dir.add(new Item(ff.getName(), ff.getAbsolutePath(), R.mipmap.folder));
                } else {
                    File file = new File(ff.getAbsolutePath());
                    if (file.getPath().endsWith(".wav"))
                        fls.add(new Item(ff.getName(), ff.getAbsolutePath(), R.mipmap.file_icon));
                }
            }
        } catch (Exception e) {

        }
        Collections.sort(fls);
        Collections.sort(dir);
        dir.addAll(fls);
        if (!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0, new Item("..", f.getParent(), R.mipmap.folder));
        adapter = new FileArrayAdapter(context, R.layout.file_row, dir);
        fileGridView.setAdapter(adapter);


        fileGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Item o = adapter.getItem(position);
                if (o.getImage() == R.mipmap.folder) {
                    dir.clear();
                    fls.clear();

                    currentDir = new File(o.getPath());
                    fill(currentDir);
                } else {
                    onFileClick(v, o);
                }
            }
        });

    }

    private Item selectedItem;

    private void onFileClick(View view, Item item) {
//        currentDir = new File(o.getPath());
//        Intent intent =new Intent(context, ActivityNewPlayer.class);
//        intent.putExtra("dir",o.getPath().toString());
//        intent.putExtra("fileName",o.getName().toString());
//        if(profileModel.ProfilePhoto !=null){
//            intent.putExtra("imgurl",profileModel.ProfilePhoto);
//        }
//        startActivity(intent);
        selectedItem = item;
        selectedFilePath = item.getPath();
        selectedFileName = item.getName();
        showMenu(view);
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this.getContext(), v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.button_menu);
        popup.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_delete:

                showGPSDisabledAlertToUser();

                return true;
            case R.id.id_rename:
                showInputFileNameDailog(selectedItem);

                return true;
            case R.id.id_view:

//                currentDir = new File(selectedItem.getPath());
                Intent intent = new Intent(context, ActivityNewPlayer.class);
                intent.putExtra("dir", selectedFilePath);
                intent.putExtra("fileName", selectedFileName);
                intent.putExtra("isFromRecordings", true);
                if (profileModel.ProfilePhoto != null) {
                    intent.putExtra("imgurl", profileModel.ProfilePhoto);
                }
                startActivity(intent);
                return true;
            case R.id.id_share:

                Uri uri = FileProvider.getUriForFile(context, "com.estethapp.media.fileprovider", new File(selectedFilePath));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.setType("audio/mp3");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, getString(R.string.txt_share_sound_file)));

                return true;
            default:
                return false;
        }
    }


    private void showGPSDisabledAlertToUser() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(
                        context.getString(R.string.txt_confirm_delete))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.txt_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                File fdelete = new File(selectedFilePath);
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        dir.clear();
                                        fls.clear();

                                        fill(new File(Util.getDirectory()));
                                        System.out.println("file Deleted :" + selectedFilePath);
                                    } else {
                                        System.out.println("file not Deleted :" + selectedFilePath);
                                    }
                                }
                            }
                        });
        alertDialogBuilder.setNegativeButton(context.getString(R.string.txt_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void showInputFileNameDailog(final Item item) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        userInput.setText(item.getName().substring(0, item.getName().indexOf(".")));
        userInput.setSelection(userInput.getText().toString().length());

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.txt_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!userInput.getText().toString().equals("")) {
                                    prepareFileToRename(item,
                                            userInput.getText().toString() + ".wav");

                                } else {
                                    Toast.makeText(context, getString(R.string.txt_name_required), Toast.LENGTH_SHORT).show();
                                }


                            }
                        })
                .setNegativeButton(context.getString(R.string.txt_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void prepareFileToRename(Item o, String newFileName) {
        currentDir = new File(o.getPath());
        File to = new File(currentDir.getParent(), newFileName);
        if (currentDir.exists()) {
            if (currentDir.renameTo(to)) {

                dir.clear();
                fls.clear();

                fill(new File(Util.getDirectory()));

                selectedFilePath = currentDir.getPath();
                selectedFileName = currentDir.getName();
            }
        }

    }


}

