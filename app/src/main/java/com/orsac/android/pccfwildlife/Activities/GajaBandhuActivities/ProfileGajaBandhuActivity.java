package com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuProfileModel;
import com.orsac.android.pccfwildlife.MyUtils.PermissionHelperClass;
import com.ravikoradiya.zoomableimageview.ZoomableImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileGajaBandhuActivity extends AppCompatActivity {

    TextView username,designation,division,toolbar_txt,roles,lastnm_txt,mobiletxt,firstnm_txt,age_txt,address_txt,
            middlenm_txt,edit_image,update_image,image_added_txt,progress_txt,section_txt,beat_txt;
    Button edit_btn,update_btn;
    ImageView profileImg,toolbar_profile_img,edit_img;
    ImageView toolbar_back_img,email_check_img,refresh_img;
    SessionManager session;
    Toolbar toolbar;
    LinearLayout designation_ll,email_ll,division_ll,role_ll,firstnm_ll,lastnm_ll,middlenm_ll,address_ll,mobiletxt_ll,progress_ll,section_ll,beat_ll;
    String division_nm="",userId="",userNm="",designationNm="",email_txt="",role_txt="",first_nm="",middle_nm="",
            last_nm="",address="",pictureImagePath="";
    final static int Camera_REQUEST_CODE=104;
    Uri image_uri=null;
    String img_url_str="";
    Bitmap myBitmap;
    PermissionHelperClass permissionHelperClass;
    private String uploaded_path="";
    private final String type="gajabandhu";
    private String imagePath="";
    ScrollView main_ll;
    EditText firstnm_edit_txt,middlenm_txt_edit_txt,email_edit_txt,address_edit_txt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_gaja_bandhu);

        try {
            initData();

            clickFunction();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initData() {
        try {
            username=findViewById(R.id.username_txt);
            designation=findViewById(R.id.designation_txt);
            division=findViewById(R.id.division_txt);
            age_txt=findViewById(R.id.age_txt);
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
            section_ll=findViewById(R.id.section_ll);
            beat_ll=findViewById(R.id.beat_ll);
            edit_img=findViewById(R.id.edit_img);

            firstnm_edit_txt=findViewById(R.id.firstnm_edit_txt);
            middlenm_txt_edit_txt=findViewById(R.id.middlenm_txt_edit_txt);
            email_edit_txt=findViewById(R.id.email_edit_txt);
            address_edit_txt=findViewById(R.id.address_edit_txt);
            edit_image=findViewById(R.id.edit_image);
            update_image=findViewById(R.id.update_image);
            image_added_txt=findViewById(R.id.image_added_txt);
            main_ll=findViewById(R.id.main_ll);
            progress_txt=findViewById(R.id.progress_txt);
            section_txt=findViewById(R.id.section_txt);
            beat_txt=findViewById(R.id.beat_txt);

            edit_btn=findViewById(R.id.edit_btn);
            update_btn=findViewById(R.id.update_btn);

            session=new SessionManager(ProfileGajaBandhuActivity.this);

            division_nm=session.getDivision();
//            userId="7978328829";

            userId=session.getGajaBandhu_UserId();
            userNm=session.getUserName();

            toolbar_profile_img.setVisibility(View.GONE);
            toolbar_back_img.setVisibility(View.VISIBLE);
            toolbar_txt.setText("My Profile");
            permissionHelperClass = new PermissionHelperClass(ProfileGajaBandhuActivity.this);

            if (permissionHelperClass.hasCameraNStoragePermission()){

            }else {
                permissionHelperClass.requestCameraNStoragePermission();//request for camera permission
            }

            callProfileAPi(userId,"Loading profile...Please wait ! ");

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void clickFunction() {
        try {

            toolbar_back_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            edit_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openBackCamera();
                    image_added_txt.setVisibility(View.GONE);
                }
            });

            refresh_img.setVisibility(View.GONE);
            refresh_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    callProfileAPi(userId,"Loading profile...Please wait ! ");
                }
            });

            profileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (imagePath!=null){
                            callImageViewDialog(ProfileGajaBandhuActivity.this,
                                    RetrofitClient.IMAGE_URL+"gajabandhu/" +imagePath,
                                    "Profile Image");
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void callProfileAPi(String userID,String progress_txt_msg) {
        try {
            progress_ll.setVisibility(View.VISIBLE);
            progress_txt.setText(progress_txt_msg);

            RetrofitInterface retrofitInterface= RetrofitClient.getGajaBandhuRequestClient().create(RetrofitInterface.class);

            retrofitInterface.getaGajaBandhuprofile_api(userID).enqueue(new Callback<GajaBandhuProfileModel>() {
                @Override
                public void onResponse(Call<GajaBandhuProfileModel> call, Response<GajaBandhuProfileModel> response) {
                    if (response.isSuccessful()){

                        progress_ll.setVisibility(View.GONE);

                        if (response.body().getUsername()==null){
                            username.setText("NA");
                        }else {
                            username.setText(response.body().getUsername());
                        }

                        if (response.body().getAge()==null){
                            age_txt.setText("NA");
                        }else {
                            age_txt.setText(response.body().getAge());
                        }

                        if (response.body().getMobile()==null){
                            mobiletxt.setText("NA");
                        }else {
                            mobiletxt.setText(response.body().getMobile());
                        }

                        if (response.body().getDivision()==null){
                            division.setText("NA");
                        }else {
                            division.setText(response.body().getDivision());
                        }

                        if (response.body().getRange()==null){
                            address_txt.setText("NA");
                        }else {
                            address_txt.setText(response.body().getRange());
                        }

                        if (response.body().getSection()==null){
                            section_txt.setText("NA");
                        }else {
                            section_txt.setText(response.body().getSection());
                        }
                        if (response.body().getBeat()==null){
                            beat_txt.setText("NA");
                        }else {
                            beat_txt.setText(response.body().getBeat());
                        }
                        imagePath=response.body().getImagePath();

                        if (imagePath==null || imagePath.equalsIgnoreCase("")){

                            Glide.with(ProfileGajaBandhuActivity.this)
                                    .load(R.drawable.ic_elephant)
                                    .into(profileImg);
                        }else {
                            Glide.with(ProfileGajaBandhuActivity.this)
                                    .load(RetrofitClient.IMAGE_URL+"gajabandhu/"+imagePath)
                                    .placeholder(R.drawable.ic_elephant)
                                    .into(profileImg);

                        }




                    }else {
                        progress_ll.setVisibility(View.GONE);
                        Toast.makeText(ProfileGajaBandhuActivity.this, "Please try again !", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GajaBandhuProfileModel> call, Throwable t) {
                    progress_ll.setVisibility(View.GONE);
                    Toast.makeText(ProfileGajaBandhuActivity.this, "Failed...Please try again !", Toast.LENGTH_SHORT).show();
                }
            });




        } catch (Exception e) {
            progress_ll.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void openBackCamera() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = timeStamp + ".jpg";
            String rootPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/WildLifeAppImages/";
            File root = new File(rootPath);
            if (!root.exists()) {
                root.mkdirs();
            }

            pictureImagePath=  rootPath + imageFileName;
            File file = new File(pictureImagePath);
            Uri outputFileUri = FileProvider.getUriForFile(ProfileGajaBandhuActivity.this,
                    getApplicationContext().getPackageName() + ".provider", file);

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, Camera_REQUEST_CODE);

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
                                image_uri=getImageUri(ProfileGajaBandhuActivity.this,myBitmap);
                                img_url_str=getRealPathFromURI(image_uri);
                                uploaded_path="wrong_path";

                                update_image.setVisibility(View.GONE);
                                edit_image.setVisibility(View.GONE);
                                image_added_txt.setVisibility(View.GONE);

                                uploadProfileImageApi(type,img_url_str);

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
        String path = MediaStore.Images.Media.insertImage(ProfileGajaBandhuActivity.this.getContentResolver(), photo, "update_profile_img", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    //update of profile image
    private void uploadProfileImageApi(String type,String imagePath){
        try {
            RetrofitInterface retrofitInterface = RetrofitClient.getNewGajaBandhuProfileUpload().create(RetrofitInterface.class);

            progress_ll.setVisibility(View.VISIBLE);
            File file = new File(imagePath);
            //creating request body for file
            okhttp3.RequestBody requestFile =
                    okhttp3.RequestBody.create(okhttp3.MediaType.parse(getContentResolver().toString()),file);

            RequestBody requestType = RequestBody.create(MediaType.parse(getContentResolver().toString()),type);
            RequestBody requestUserID = RequestBody.create(MediaType.parse(getContentResolver().toString()),userId);

            MultipartBody.Part image_body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            retrofitInterface.updateGajaProfileImage(image_body,requestType,requestUserID).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if (response.isSuccessful()){
                        if (response.code()==200){

//                            Toast.makeText(ProfileActivity.this, "Successfully uploaded !"+response.body().getFileName(), Toast.LENGTH_SHORT).show();
                            progress_ll.setVisibility(View.GONE);
//                            uploaded_path=response.body().toString();

                            image_added_txt.setVisibility(View.VISIBLE);
                            image_added_txt.setText("Image Added");
                            image_added_txt.setTextColor(getResources().getColor(R.color.green));
                            Animation animation= AnimationUtils.loadAnimation(ProfileGajaBandhuActivity.this,R.anim.right_to_left);
                            image_added_txt.startAnimation(animation);

                            update_image.setVisibility(View.GONE);
                            edit_image.setVisibility(View.GONE);


                        }
                        else if (response.code()>400){
                            progress_ll.setVisibility(View.GONE);
                            image_added_txt.setVisibility(View.GONE);
                            Snackbar.make(main_ll, "Internal Server Error !", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        progress_ll.setVisibility(View.GONE);
                        image_added_txt.setVisibility(View.GONE);
                        update_image.setVisibility(View.GONE);
                        edit_image.setVisibility(View.GONE);
                        Snackbar.make(main_ll, "Please try again !", Snackbar.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progress_ll.setVisibility(View.GONE);
                    image_added_txt.setVisibility(View.GONE);
                    update_image.setVisibility(View.GONE);
                    edit_image.setVisibility(View.GONE);
                    Snackbar.make(main_ll, "Failed...Please try again !", Snackbar.LENGTH_SHORT).show();

                }
            });



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

            cancel_image.setVisibility(View.INVISIBLE);
            title_txt.setText(title);

            Glide.with(context)
                    .load(imgpath)
                    .error(R.drawable.no_image_found)
                    .into(report_imgg);


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
