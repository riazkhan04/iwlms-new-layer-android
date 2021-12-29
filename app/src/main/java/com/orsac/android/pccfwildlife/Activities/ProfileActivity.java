package com.orsac.android.pccfwildlife.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitInterface;
import com.orsac.android.pccfwildlife.SharedPref.SessionManager;
import com.orsac.android.pccfwildlife.Model.FileUploadResponse;
import com.orsac.android.pccfwildlife.Model.ProfileModel.ProfileData;
import com.orsac.android.pccfwildlife.Model.ProfileModel.SuccessResponseData;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.ravikoradiya.zoomableimageview.ZoomableImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    TextView username,designation,division,toolbar_txt,roles,lastnm_txt,mobiletxt,firstnm_txt,emailtxt,address_txt,
                middlenm_txt,edit_image,update_image,image_added_txt,progress_txt,versionNo;
    EditText firstnm_edit_txt,middlenm_txt_edit_txt,email_edit_txt,address_edit_txt;
    Button edit_btn,update_btn;
    ImageView profileImg,toolbar_profile_img,edit_img;
    ImageView toolbar_back_img,email_check_img,refresh_img;
    SessionManager session;
    Toolbar toolbar;
    LinearLayout designation_ll,email_ll,division_ll,role_ll,firstnm_ll,lastnm_ll,middlenm_ll,address_ll,mobiletxt_ll,progress_ll;
    String division_nm="",userId="",userNm="",designationNm="",email_txt="",role_txt="",first_nm="",middle_nm="",
                last_nm="",address="",pictureImagePath="";
    final static int Camera_REQUEST_CODE=104;
    Uri image_uri=null;
    String img_url_str="",view_img_path="";
    Bitmap myBitmap;
    PermissionHelperClass permissionHelperClass;
    private String uploaded_path="";
    ScrollView main_ll;
    boolean firstTimeEmailCheck=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.left_to_right_anim,
                R.anim.left_to_right_anim);
        setContentView(R.layout.activity_profile);

        try {

        initData();

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (edit_btn.getText().toString().equalsIgnoreCase("Cancel")){

                        edit_btn.setText("Edit Profile");

                        middlenm_txt.setVisibility(View.VISIBLE);
                        emailtxt.setVisibility(View.VISIBLE);
                        address_txt.setVisibility(View.VISIBLE);

                        middlenm_txt_edit_txt.setVisibility(View.GONE);
                        email_edit_txt.setVisibility(View.GONE);
                        address_edit_txt.setVisibility(View.GONE);

                        email_check_img.setVisibility(View.GONE);


                    }else {

                        middlenm_txt.setVisibility(View.GONE);
                        emailtxt.setVisibility(View.GONE);
                        address_txt.setVisibility(View.GONE);

                        middlenm_txt_edit_txt.setVisibility(View.VISIBLE);
                        email_edit_txt.setVisibility(View.VISIBLE);
                        address_edit_txt.setVisibility(View.VISIBLE);

                        middlenm_txt_edit_txt.requestFocus();

                        edit_btn.setText("Cancel");

                        Animation animation=AnimationUtils.loadAnimation(ProfileActivity.this,R.anim.right_to_left);
                        middlenm_txt_edit_txt.startAnimation(animation);
                        email_edit_txt.startAnimation(animation);
                        address_edit_txt.startAnimation(animation);

                        email_check_img.setVisibility(View.INVISIBLE);

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PermissionUtils.hideKeyboard(ProfileActivity.this);
                    if (middlenm_txt_edit_txt.getText().toString().trim().equalsIgnoreCase(middle_nm) &&
                        email_edit_txt.getText().toString().trim().equalsIgnoreCase(email_txt) &&
                        address_edit_txt.getText().toString().trim().equalsIgnoreCase(address) &&
                        image_uri==null){

                        image_added_txt.setVisibility(View.GONE);
                        email_check_img.setVisibility(View.INVISIBLE);
                        Toast.makeText(ProfileActivity.this, "No data change for update !", Toast.LENGTH_SHORT).show();

                    }else if (uploaded_path.equalsIgnoreCase("wrong_path")){

                        image_added_txt.setText("Please click update image");
                        image_added_txt.setTextColor(getResources().getColor(R.color.red));
                        image_added_txt.setVisibility(View.VISIBLE);

                        email_check_img.setVisibility(View.INVISIBLE);

                        Animation animation=AnimationUtils.loadAnimation(ProfileActivity.this,R.anim.right_to_left);
                        image_added_txt.startAnimation(animation);
                    }
                    else {

                        image_added_txt.setVisibility(View.GONE);
                        firstTimeEmailCheck=false;

                    if (isValidEmailId(email_edit_txt.getText().toString().trim())){

                        if (middlenm_txt_edit_txt.getText().toString().equals("")){

                        Snackbar.make(main_ll, "Please enter a valid middle name !", Snackbar.LENGTH_SHORT).show();
                        }
                        else if (address_edit_txt.getText().toString().trim().equals("")){
                            Snackbar.make(main_ll, "Please enter a valid address !", Snackbar.LENGTH_SHORT).show();
                        }
                        else{

                            validateEmailApi(email_edit_txt.getText().toString().trim());
                            //call for update api in success of valid email
                        }

                    }else {
                        Snackbar.make(main_ll, "Please enter a valid email!", Snackbar.LENGTH_SHORT).show();
                         }

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        toolbar_back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openBackCamera();
                image_added_txt.setVisibility(View.GONE);
            }
        });

        update_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (img_url_str.equalsIgnoreCase("")){
                        Toast.makeText(ProfileActivity.this, "Please capture image to upload !", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        image_added_txt.setVisibility(View.GONE);

                        uploadProfileImageApi(userNm,img_url_str);
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

//        refresh_img.setVisibility(View.VISIBLE);//for now refresh icon is visible gone
        refresh_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (userNm.equalsIgnoreCase("")){

                    }else {
                        callProfileAPi(userNm,"Loading profile data...Please wait ! ");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    callImageViewDialog(ProfileActivity.this,
                            RetrofitClient.IMAGE_URL+"profile/"+view_img_path,
                            "Profile Image");

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void initData(){

        try {

        username=findViewById(R.id.username_txt);
        designation=findViewById(R.id.designation_txt);
        division=findViewById(R.id.division_txt);
        emailtxt=findViewById(R.id.email_txt);
        roles=findViewById(R.id.role_txt);
        toolbar = findViewById(R.id.toolbar_new_id);
        toolbar_profile_img = toolbar.findViewById(R.id.profile_img);
        toolbar_back_img = toolbar.findViewById(R.id.back_img);
        refresh_img = toolbar.findViewById(R.id.refresh_img);
        toolbar_txt = toolbar.findViewById(R.id.toolbar_txt);
        email_check_img=findViewById(R.id.email_check_img);
        profileImg=findViewById(R.id.profile_circle_img);
        division_ll=findViewById(R.id.division_ll);
        email_ll=findViewById(R.id.email_ll);
        designation_ll=findViewById(R.id.designation_ll);
        role_ll=findViewById(R.id.role_ll);
        firstnm_txt=findViewById(R.id.firstnm_txt);
        middlenm_txt=findViewById(R.id.middlenm_txt);
        lastnm_txt=findViewById(R.id.lastnm_txt);
        address_txt=findViewById(R.id.address_txt);
        mobiletxt=findViewById(R.id.mobile_txt);
        address_ll=findViewById(R.id.address_ll);
        lastnm_ll=findViewById(R.id.lastnm_ll);
        middlenm_ll=findViewById(R.id.middlenm_ll);
        firstnm_ll=findViewById(R.id.firstnm_ll);
        mobiletxt_ll=findViewById(R.id.mobile_ll);
        progress_ll=findViewById(R.id.progress_ll);
        firstnm_edit_txt=findViewById(R.id.firstnm_edit_txt);
        middlenm_txt_edit_txt=findViewById(R.id.middlenm_txt_edit_txt);
        email_edit_txt=findViewById(R.id.email_edit_txt);
        address_edit_txt=findViewById(R.id.address_edit_txt);
        edit_image=findViewById(R.id.edit_image);
        update_image=findViewById(R.id.update_image);
        image_added_txt=findViewById(R.id.image_added_txt);
        main_ll=findViewById(R.id.main_ll);
        progress_txt=findViewById(R.id.progress_txt);
        edit_img=findViewById(R.id.edit_img);
        versionNo=findViewById(R.id.versionNo);

        edit_btn=findViewById(R.id.edit_btn);
        update_btn=findViewById(R.id.update_btn);

        session=new SessionManager(ProfileActivity.this);

        division_nm=session.getDivision();
        userId=session.getUserID();
        userNm=session.getUserName();

        toolbar_profile_img.setVisibility(View.GONE);
        toolbar_back_img.setVisibility(View.VISIBLE);
        toolbar_txt.setText("My Profile");
        permissionHelperClass = new PermissionHelperClass(ProfileActivity.this);

            if (permissionHelperClass.hasCameraNStoragePermission()){

            }else {
                permissionHelperClass.requestCameraNStoragePermission();//request for camera permission
            }

            versionNo.setText("Version - "+PermissionUtils.getVersion_Code_Name(ProfileActivity.this));//for getting version name dynamically


            //Profile Api call
            callProfileAPi(userNm,"Loading profile data...Please wait ! ");

         }catch (Exception e){
        e.printStackTrace();
     }

    }

    private void callProfileAPi(String loginID,String progress_txt_msg){
        try {

            progress_ll.setVisibility(View.VISIBLE);
            progress_txt.setText(progress_txt_msg);
            RetrofitInterface retrofitInterface = RetrofitClient.getClient("profile").create(RetrofitInterface.class);
            retrofitInterface.getprofile_api(loginID).enqueue(new Callback<ProfileData>() {
                @Override
                public void onResponse(Call<ProfileData> call, Response<ProfileData> response) {
                    if (response.isSuccessful()){

                        progress_ll.setVisibility(View.GONE);
                        setData(response.body().getFirstName(),response.body().getLastName(),
                                response.body().getMiddleName(),
                                response.body().getUserAddress(),response.body().getUserDesignation(),
                                userNm,response.body().getJuridicitionName(),response.body().getUserPhoneNumber(),
                                response.body().getEmail(),response.body().getImgPath());

                        first_nm=response.body().getFirstName();
                        middle_nm=response.body().getMiddleName();
                        email_txt=response.body().getEmail();
                        address=response.body().getUserAddress();
                        if (middle_nm==null && email_txt==null && address==null){
                            middle_nm="Null";
                            email_txt="Null";
                            address="Null";
                        }

                        if (edit_btn.getText().toString().equalsIgnoreCase("Cancel")){

                            edit_btn.setText("Edit Profile");

                            middlenm_txt.setVisibility(View.VISIBLE);
                            emailtxt.setVisibility(View.VISIBLE);
                            address_txt.setVisibility(View.VISIBLE);

                            middlenm_txt_edit_txt.setVisibility(View.GONE);
                            email_edit_txt.setVisibility(View.GONE);
                            address_edit_txt.setVisibility(View.GONE);

                        }
                        update_image.setVisibility(View.GONE);
                        edit_image.setVisibility(View.GONE);
//                        edit_image.setVisibility(View.VISIBLE);
                        image_added_txt.setVisibility(View.INVISIBLE);

                    }
                    else {
                        progress_ll.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, "Please try again !", Toast.LENGTH_SHORT).show();
                        Log.i("url",response.raw().toString());
                    }
                }

                @Override
                public void onFailure(Call<ProfileData> call, Throwable t) {
                    progress_ll.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                    Log.i("url",call.request().toString());
                }
            });


        }catch (Exception e){
            progress_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void setData(String first_nm, String last_nm,String middle_nm,String address,String role,String userNm,
                         String division_nm,String mobileNo,String email,String imagePath){

        try {

            if (first_nm==null || first_nm.equalsIgnoreCase("")){

                firstnm_txt.setText("Null");
            }
            else {
                firstnm_txt.setText(first_nm);
                firstnm_edit_txt.setText(first_nm);
            }

            if ( middle_nm==null||
                    middle_nm.equalsIgnoreCase("null") ||
                    middle_nm.equalsIgnoreCase("")
            ){

                middlenm_txt.setText("Null");
                middlenm_txt_edit_txt.setText("Null");
            }
            else {
                middlenm_txt.setText(middle_nm);
                middlenm_txt_edit_txt.setText(middle_nm);
            }
            if ( last_nm==null ||
                    last_nm.equalsIgnoreCase("")){

                lastnm_txt.setText("Null");
            }
            else {
                lastnm_txt.setText(last_nm);
            }
            if (address==null ||
                address.equalsIgnoreCase("")){

                address_txt.setText("Null");
                address_edit_txt.setText("Null");
            }
            else {
                address_txt.setText(address);
                address_edit_txt.setText(address);
            }

            if (userNm.equalsIgnoreCase("")|| userNm==null){

                username.setText("Null");
            }
            else {
                username.setText(userNm);
            }
            if (role.equalsIgnoreCase("")){

                designation.setText("Null");
                designation_ll.setVisibility(View.GONE);
            }
            else {
                designation.setText(role);
            }
            if (division_nm==null ||
                    division_nm.equalsIgnoreCase("")){

                division.setText("Null");
                division_ll.setVisibility(View.GONE);
            }
            else {
                division.setText(division_nm);
            }

            if (email==null||
                email.equalsIgnoreCase("")){

                emailtxt.setText("Null");
                email_edit_txt.setText("Null");
            }
            else {
                emailtxt.setText(email);
                email_edit_txt.setText(email);
            }

            if (role==null||
                    role.equalsIgnoreCase("")|| role.equalsIgnoreCase("default")){

                roles.setText("Null");
                role_ll.setVisibility(View.GONE);
            }
            else {
                roles.setText(role);
            }
            if (mobileNo==null ||
                mobileNo.equalsIgnoreCase("")){

                mobiletxt.setText("Null");
                mobiletxt_ll.setVisibility(View.GONE);
            }
            else {
                mobiletxt.setText(mobileNo);
            }
            if (imagePath==null || imagePath.equalsIgnoreCase("")){

                Glide.with(ProfileActivity.this)
                        .load(R.drawable.ic_elephant)
                        .into(profileImg);
            }else {
                Glide.with(ProfileActivity.this)
                        .load(RetrofitClient.IMAGE_URL+"profile/"+imagePath)
                        .placeholder(R.drawable.ic_elephant)
                        .into(profileImg);

                view_img_path=imagePath;

            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callUpadteProfile(String userNm,String middle_nm,String email_txt,String address,String imagePath,String userPhoneNumber){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("username",userNm);
            if (imagePath.equalsIgnoreCase("")){
                jsonObject.put("imgPath",null);
            }else {
                jsonObject.put("imgPath",imagePath);
            }
            if (middle_nm.equalsIgnoreCase("")){
                jsonObject.put("middleName",null);
            }else {
                jsonObject.put("middleName",middle_nm);
            }
            if (email_txt.equalsIgnoreCase("")){
                jsonObject.put("email",null);
            }else {
                jsonObject.put("email",email_txt);
            }
            if (address.equalsIgnoreCase("")){
                jsonObject.put("userAddress",null);
            }
            else {
                jsonObject.put("userAddress",address);
            }
            if (userPhoneNumber.equalsIgnoreCase("")){
                jsonObject.put("userPhoneNumber",null);
            }
            else {
                jsonObject.put("userPhoneNumber",userPhoneNumber);
            }

            progress_ll.setVisibility(View.VISIBLE);
            progress_txt.setText("Updating profile...Please wait !");
            RequestBody update_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),jsonObject.toString());

            RetrofitInterface retrofitInterface=RetrofitClient.getClient("update").create(RetrofitInterface.class);
            retrofitInterface.updateProfile_api(update_body).enqueue(new Callback<SuccessResponseData>() {
                @Override
                public void onResponse(Call<SuccessResponseData> call, Response<SuccessResponseData> response) {

                    if (response.isSuccessful()){
                        progress_ll.setVisibility(View.GONE);
                        Snackbar.make(main_ll, "Profile updated successfully !", Snackbar.LENGTH_SHORT).show();

                        callProfileAPi(userNm,"Updating profile...Please wait !");
                        update_image.setVisibility(View.GONE);
                        edit_image.setVisibility(View.GONE);
//                        edit_image.setVisibility(View.VISIBLE);
                        image_uri=null;
                        img_url_str="";
                        uploaded_path="";
                    }
                    else {
                        progress_ll.setVisibility(View.GONE);
                        Snackbar.make(main_ll, "Please try again !",  Snackbar.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<SuccessResponseData> call, Throwable t) {
                    progress_ll.setVisibility(View.GONE);
                    Snackbar.make(main_ll, "Failed...Please try again !", Snackbar.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            progress_ll.setVisibility(View.GONE);
        }
    }

    private void openBackCamera() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            if(Build.VERSION.SDK_INT >= 29) {
                //only api 29 above
                // Create an image file name
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File file = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );

                // Save a file: path for use with ACTION_VIEW intents
                pictureImagePath = file.getAbsolutePath();

                Uri outputFileUri = FileProvider.getUriForFile(ProfileActivity.this,
                        getApplicationContext().getPackageName() + ".provider", file);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                //COMPATIBILITY
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
                    cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
                startActivityForResult(cameraIntent, Camera_REQUEST_CODE);


            }else{
                //only api 29 down
                String imageFileName = timeStamp + ".jpg";

                String rootPath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/WildLifeAppImages/";

                File root = new File(rootPath);
                if (!root.exists()) {
                    root.mkdirs();
                }

                pictureImagePath=  rootPath + imageFileName;
                File file = new File(pictureImagePath);
                Uri outputFileUri = FileProvider.getUriForFile(ProfileActivity.this,
                        getApplicationContext().getPackageName() + ".provider", file);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                //COMPATIBILITY
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
                    cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
                startActivityForResult(cameraIntent, Camera_REQUEST_CODE);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case Camera_REQUEST_CODE:

                if (requestCode == Camera_REQUEST_CODE) {
                    try {

                        if (resultCode == RESULT_OK) {
                            String filenm="";
                            File imgFile = new  File(pictureImagePath);
                            if(imgFile.exists()){
                                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                profileImg.setImageBitmap(myBitmap);
                                image_uri=getImageUri(ProfileActivity.this,myBitmap);
                                img_url_str=getRealPathFromURI(image_uri);
                                uploaded_path="wrong_path";

//                                update_image.setVisibility(View.VISIBLE);
                                edit_image.setVisibility(View.GONE);
                                image_added_txt.setVisibility(View.INVISIBLE);

                                try {

                                    if (img_url_str.equalsIgnoreCase("")){
                                        Toast.makeText(ProfileActivity.this, "Please capture image to upload !", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        image_added_txt.setVisibility(View.INVISIBLE);

                                        uploadProfileImageApi(userNm,img_url_str);
                                    }


                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }


                        } else if (resultCode == RESULT_CANCELED) {
                            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private Uri getImageUri(Context applicationContext, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ProfileActivity.this.getContentResolver(), photo, "update_profile_img", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void uploadProfileImageApi(String userId,String imagePath){
        try {
            RetrofitInterface retrofitInterface = RetrofitClient.getNewFileUploadClient().create(RetrofitInterface.class);

            progress_ll.setVisibility(View.VISIBLE);
            File file = new File(imagePath);
            //creating request body for file
            okhttp3.RequestBody requestFile =
                    okhttp3.RequestBody.create(okhttp3.MediaType.parse(getContentResolver().toString()),file);

            MultipartBody.Part image_body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

//            retrofitInterface.updateProfileImage(image_body,userId).enqueue(new Callback<FileUploadResponse>() {
            retrofitInterface.updateImage(image_body,userId,"profile").enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {

                    if (response.isSuccessful()){
                        if (response.code()==200){

//                            Toast.makeText(ProfileActivity.this, "Successfully uploaded !"+response.body().getFileName(), Toast.LENGTH_SHORT).show();
                            progress_ll.setVisibility(View.GONE);
                            uploaded_path=response.body().getFileName();

                            image_added_txt.setVisibility(View.VISIBLE);
                            image_added_txt.setText("Image Added");
                            image_added_txt.setTextColor(getResources().getColor(R.color.green));
                            Animation animation=AnimationUtils.loadAnimation(ProfileActivity.this,R.anim.right_to_left);
                            image_added_txt.startAnimation(animation);

                            update_image.setVisibility(View.GONE);
                            edit_image.setVisibility(View.GONE);
//                            edit_image.setVisibility(View.VISIBLE);

                            callUpadteProfile(userNm,
                                    middlenm_txt_edit_txt.getText().toString().trim(),
                                    email_edit_txt.getText().toString().trim(),
                                    address_edit_txt.getText().toString().trim(),
                                    uploaded_path,mobiletxt.getText().toString().trim()
                            );

                        }
                        else if (response.code()>400){
                            progress_ll.setVisibility(View.GONE);
                            image_added_txt.setVisibility(View.INVISIBLE);
                            Snackbar.make(main_ll, "Internal Server Error !", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        progress_ll.setVisibility(View.GONE);
                        image_added_txt.setVisibility(View.INVISIBLE);
                        Snackbar.make(main_ll, "Please try again !", Snackbar.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    progress_ll.setVisibility(View.GONE);
                    image_added_txt.setVisibility(View.INVISIBLE);
                    Snackbar.make(main_ll, "Failed...Please try again !", Snackbar.LENGTH_SHORT).show();

                }
            });



        }catch (Exception e){
            progress_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public void validateEmailApi(String email){
        try {

//            if (firstTimeEmailCheck==true){
            progress_ll.setVisibility(View.VISIBLE);
                RetrofitInterface retrofitInterface=RetrofitClient.getClient("emailVerification").create(RetrofitInterface.class);

                retrofitInterface.checkValidEmail(email).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                        if (response.isSuccessful()){

                            if (response.body().booleanValue()==true){

//                                Snackbar.make(main_ll, "Existing email !", Snackbar.LENGTH_SHORT).show();
                                progress_ll.setVisibility(View.GONE);
                                callUpadteProfile(userNm,
                                        middlenm_txt_edit_txt.getText().toString().trim(),
                                        email_edit_txt.getText().toString().trim(),
                                        address_edit_txt.getText().toString().trim(),
                                        uploaded_path,mobiletxt.getText().toString().trim()
                                );
//                                email_check_img.setVisibility(View.INVISIBLE);
                            }else {
                                Snackbar.make(main_ll, "New email for update!", Snackbar.LENGTH_SHORT).show();
                                progress_ll.setVisibility(View.GONE);
                                callUpadteProfile(userNm,
                                        middlenm_txt_edit_txt.getText().toString().trim(),
                                        email_edit_txt.getText().toString().trim(),
                                        address_edit_txt.getText().toString().trim(),
                                        uploaded_path,mobiletxt.getText().toString().trim()
                                );
//                                email_check_img.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            progress_ll.setVisibility(View.GONE);
                            Snackbar.make(main_ll, "Please try again !", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        progress_ll.setVisibility(View.GONE);
                        Snackbar.make(main_ll, "Failed...Please try again !", Snackbar.LENGTH_SHORT).show();
                    }
                });


//            }else {
//                firstTimeEmailCheck=true;
//            }

        }catch (Exception e){
            progress_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    public void callImageViewDialog(Context context,String imgpath,String title){
        try {

            Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.image_view_dialog);

            ImageView report_image,close_img;
            TextView cancel_image,title_txt;
            ZoomableImageView report_imgg;
//            report_image=dialog.findViewById(R.id.report_image);
            report_imgg=dialog.findViewById(R.id.report_image);
            close_img=dialog.findViewById(R.id.close_img);
            cancel_image=dialog.findViewById(R.id.cancel_image);
            title_txt=dialog.findViewById(R.id.title);

            cancel_image.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(imgpath)
                    .error(R.drawable.no_image_found)
                    .into(report_imgg);

            cancel_image.setVisibility(View.INVISIBLE);

            title_txt.setText(title);

            cancel_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        dialog.dismiss();
                        pictureImagePath="";
//                        direct_camera_img.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            close_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                }
            });

            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
