package com.example.learning.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.example.learning.DbApi;
import com.example.learning.HashUtil;
import com.example.learning.MainActivity;
import com.example.learning.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri; import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View; import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;





import java.io.File;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment implements View.OnClickListener{
    SQLiteDatabase db;
    private DbApi dbApi;
    private TextView email;
    private EditText user_name ;
    private EditText phone_number;
    private EditText password;
    private Button save_button;
    private Button cancel_button;
    private ImageView btnProfileBack;
    private int userId;
    private String name_information;
    private String phone_information;
    private Profile mContext;
    private ImageView imgUserPhoto;


    private static final int CODE_PHOTO_REQUEST = 1;
    private static final int CODE_CAMERA_REQUEST = 2;
    private static final int CODE_PHOTO_CLIP = 3;
    String coverPath = "/storage/emulated/0/Android/data/com.example.learning/files/deckCovers/default.png";
    private ImageView selectedImg;




    public Profile(SQLiteDatabase db) {
        // Required empty public constructor
        this.db = db;
    }

    public static Profile newInstance(SQLiteDatabase db) {
        Profile fragment = new Profile(db);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        if (container != null) {
            container.removeAllViews();
        }
        // Inflate the layout for this fragment
        MainActivity main = (MainActivity) getActivity();
        System.out.println("profile create");
        userId = main.getLoginUserId();
        mContext = Profile.this;
        dbApi = new DbApi(db);
        email = view.findViewById(R.id.TextTextmail);
        user_name = view.findViewById(R.id.editTextTextPersonName);
        phone_number = view.findViewById(R.id.editTextTextPhone);
        password = view.findViewById(R.id.editTextTextPassword);
        save_button = view.findViewById(R.id.save_change_btn);
        cancel_button = view.findViewById(R.id.cacel_change_btn);
        imgUserPhoto = view.findViewById(R.id.user_profile);
        imgUserPhoto.setOnClickListener(this);
        ImageUtils.loadProfile(getActivity(), dbApi.queryUserProfileURL(userId), imgUserPhoto);
        showUserInformation();
        save_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

        btnProfileBack = view.findViewById(R.id.btnProfileBack);

        // when the back button is clicked, change to home fragment
        btnProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("change to home");
                Home homeFragment = new Home(db);
                FragmentManager homeFragmentManager = getFragmentManager();
                homeFragmentManager.beginTransaction()
                        .replace(R.id.layoutProfile, homeFragment)
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("profile on resume");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_change_btn:
                updateInfomation(view);
                break;
            case R.id.cacel_change_btn:
                showUserInformation();
                break;
            case R.id.user_profile:
                getPicFromLocal();


//                Toast.makeText(this.getContext(),upload_url+coverPath,Toast.LENGTH_LONG).show();


        }


    }

    private void getPicFromLocal() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
        }

    private void photoClip(Uri uri) { // ????????????????????????????????????
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*"); // ????????????crop=true?????????????????????Intent??????????????????VIEW?????????
        intent.putExtra("crop", "true");
        // aspectX aspectY ??????????????????
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        /*outputX outputY ????????????????????? *????????????????????????????????????????????????????????? * ????????????binder????????????????????????1M?????? * ???TransactionTooLargeException */
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_PHOTO_CLIP);
    }
    private void setImageToHeadView(Intent intent) { Bundle extras = intent.getExtras();
        if (extras != null) { Bitmap photo = extras.getParcelable("data");
        imgUserPhoto.setImageBitmap(photo);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_CANCELED) { Toast.makeText(MainActivity.this, "??????", Toast.LENGTH_LONG).show();
//            return;
//        }
        switch (requestCode) {
            case CODE_PHOTO_REQUEST:
                if (data != null) {
                    try {
                        Uri uriImage = data.getData();
                        Bitmap bitmap = BitmapFactory.decodeStream
                                (getActivity().getContentResolver().openInputStream(uriImage));
//                    String path = RealPathFromUriUtils.getRealPathFromUri(context, uriImage);
//                    System.out.println(path);
                        coverPath = UserImageUtils.saveImageToGallery(this.getActivity(), bitmap, "");
                        saveUserImage();
                        imgUserPhoto.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


            }
        }
    }
    public void saveUserImage(){
        dbApi.updateUserImg(userId,coverPath);

    }





    public void showUserInformation(){
        ArrayList<String> arrayList = dbApi.getUserInfo(userId);
        String email_info =arrayList.get(0);
        String name_info = arrayList.get(1);
        String phone_number_info = arrayList.get(2);
        String password_num = arrayList.get(3);
        email.setText(email_info);
        user_name.setText(name_info);
        phone_number.setText(phone_number_info);
        password.setText(password_num);


    }
    public void updateInfomation(View view){
        register(view);


    }
    public void register(View v){
        String namestring = user_name.getText().toString();
        String passwordstring = password.getText().toString();
        String emailstring = email.getText().toString();
        String phonestring = phone_number.getText().toString();
        //????????????
        if(namestring.length() == 0  ){
            Toast.makeText(v.getContext(),"Name can not be empty!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(namestring.length() > 20  ){
            Toast.makeText(v.getContext(),"Name must be less than 20 characters",Toast.LENGTH_SHORT).show();
            return;
        }
//        if(namestring.length() != 0 && namestring.length() < 8  ){
//            Toast.makeText(getApplicationContext(),"??????????????????8???",Toast.LENGTH_SHORT).show();
//            return;
//        }
        //????????????
        if(passwordstring.length() == 0 ){
            Toast.makeText(v.getContext(),"password can not be empty!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(passwordstring.length() > 16  ){
            Toast.makeText(v.getContext(),"Password must be less than 16 characters!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(passwordstring.length() != 0 && passwordstring.length() < 6  ){
            Toast.makeText(v.getContext(),"Password must be greater than 8 digits!",Toast.LENGTH_SHORT).show();
            return;
        }

        //????????????
        if(emailstring.length() == 0 ){
            Toast.makeText(v.getContext(),"email can not be empty!",Toast.LENGTH_SHORT).show();
            return;
        }

        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;

        p = Pattern.compile(regEx1);
        m = p.matcher(emailstring);

        if (!m.matches()){
            Toast.makeText(v.getContext(),"E-mail format is incorrect!",Toast.LENGTH_SHORT).show();
            return;
        }

        //????????????
        if(phonestring.length() == 0 ){
            Toast.makeText(v.getContext(),"Phone cannot be empty!",Toast.LENGTH_SHORT).show();
            return;
        }

        Pattern p1 = Pattern.compile("^1[3,5,7,8,9][0-9]{9}$");
        Matcher m1 = p1.matcher(phonestring);

        if(!m1.matches()){
            Toast.makeText(v.getContext(),"Incorrect phone format!",Toast.LENGTH_SHORT).show();
            return;
        }
        dbApi.UpdateUserIfo(userId,namestring,phonestring,passwordstring);

        Toast.makeText(v.getContext(),"Congraduation,It is ok!",Toast.LENGTH_SHORT).show();

    }









}