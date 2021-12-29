package com.orsac.android.pccfwildlife.SharedPref;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.orsac.android.pccfwildlife.Activities.GajaBandhuActivities.LoginActivityNewGajaBandhu;

/**
 * Created by Narendra/Riaz on 6/8/2020.
 */

public class SessionManager {

//    SharedPreferences sharedprefernce;
//    SharedPreferences.Editor editor;
//    SharedPreferences sharedprefernceCoupon;
//    SharedPreferences.Editor editorCoupon;
    SharedPreferences loginsharedprefernce;
    SharedPreferences.Editor editor_login;
    Context context;
    int PRIVATE_MODE=0;

    private static final String PREF_NAME="sharedcheckLogin";
    private static final String PREF_NAME2="sharedcheckLogin2";
    private static final String PREF_NAME_LOGIN="logindetails";
    private static final String User_Id="userid";
    private static final String UserName ="uname";
    private static final String FirstName="fname";
    private static final String ProfilePic="img";
    private static final String LastName="lname";
    private static final String Email="email";
    private static final String roles="roles";
    private static final String Phone="phone";
    private static final String Token="token";
    private static final String Designation="designation";
    private static final String Juridiction="juridiction";
    private static final String JuridictionID="juridictionId";
    private static final String roleId="roleId";
    private static final String circleId="circleId";
    private static final String divisionId="divisionId";
    private static final String rangeId="rangeId";
    private static final String sectionId="sectionId";
    private static final String beatId="beatId";
    private static final String Division="division";
    private static final String DisplayName ="dname";
    private static final String Ranger="ranger";
    private static final String EmailVerifyStatus="emailverifystatus";
    private static final String Otp="otp";
    private static final String OtpKey="otpkey";
    private static final String IS_LOGIN="islogin";
    private static final String languageSelect="language";
    private static final String gajaBandhu_UserId="gajabandhu_UserId";


    public SessionManager(Context context){

        this.context =  context;
//        sharedprefernce = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
//        editor=sharedprefernce.edit();

//        sharedprefernceCoupon=context.getSharedPreferences(PREF_NAME2,PRIVATE_MODE);
//        editorCoupon=sharedprefernceCoupon.edit();

        loginsharedprefernce = context.getSharedPreferences(PREF_NAME_LOGIN, PRIVATE_MODE);
        editor_login=loginsharedprefernce.edit();

    }

    public Boolean isLogin(){
        return loginsharedprefernce.getBoolean(IS_LOGIN, false);

    }
    public void setLogin(){

        editor_login.putBoolean(IS_LOGIN, true);
        editor_login.commit();

    }

    public void setUserID(String id ){

        editor_login.putString(User_Id,id);
        editor_login.commit();
    }

    public String getUserID(){

        return loginsharedprefernce.getString(User_Id,"DEFAULT");
    }

    public void setGajaBandhu_UserId(String id ){

        editor_login.putString(gajaBandhu_UserId,id);
        editor_login.commit();
    }
    public String getGajaBandhu_UserId(){

        return loginsharedprefernce.getString(gajaBandhu_UserId,"");
    }


    public void setRoles(String rolenm){

        editor_login.putString(roles,rolenm);
        editor_login.commit();
    }

    public String getRoles() {
        return loginsharedprefernce.getString(roles,"DEFAULT");
    }

    public void setFirstName(String fname){

        editor_login.putString(FirstName,fname);
        editor_login.commit();
    }

    public String getFirstName(){

        return loginsharedprefernce.getString(FirstName,"Default");
    }

    public void setUserName(String uname){

        editor_login.putString(UserName,uname);
        editor_login.commit();
    }

    public String getUserName(){

        return loginsharedprefernce.getString(UserName,"Admin");
    }

    public void setToken(String token){

        editor_login.putString(Token,token);
        editor_login.commit();
    }

    public String getToken() {
        return loginsharedprefernce.getString(Token,"token");
    }

    public void setImgUrl(String img){

        editor_login.putString(ProfilePic,img);
        editor_login.commit();
    }

    public String getImgUrl(){

        return loginsharedprefernce.getString(ProfilePic,"Default");
    }


    public void setLastName(String lname){

        editor_login.putString(LastName,lname);
        editor_login.commit();
    }

    public String getLastName(){

        return loginsharedprefernce.getString(LastName,"Default");
    }


    public void setEmail(String email){

        editor_login.putString(Email,email);
        editor_login.commit();
    }
    public void setJuridiction(String juridiction){

        editor_login.putString(Juridiction,juridiction);
        editor_login.commit();
    }
    public void setJuridictionID(String juridictionId){

        editor_login.putString(JuridictionID,juridictionId);
        editor_login.commit();
    }
    public void setRoleId(String role_Id){

        editor_login.putString(roleId,role_Id);
        editor_login.commit();
    }
    public String getRoleId() {
        return loginsharedprefernce.getString(roleId,"Default");
    }
    public void setCircleId(String circle_Id){

        editor_login.putString(circleId,circle_Id);
        editor_login.commit();
    }
    public String getCircleId() {
        return loginsharedprefernce.getString(circleId,"Default");
    }

    public void setDivisionId(String division_Id){
        editor_login.putString(divisionId,division_Id);
        editor_login.commit();
    }
    public String getDivisionId() {
        return loginsharedprefernce.getString(divisionId,"Default");
    }

    public void setRangeId(String range_Id){
        editor_login.putString(rangeId,range_Id);
        editor_login.commit();
    }
    public String getRangeId() {
        return loginsharedprefernce.getString(rangeId,"Default");
    }
    public void setSectionId(String section_Id){
        editor_login.putString(sectionId,section_Id);
        editor_login.commit();
    }
    public String getSectionId() {
        return loginsharedprefernce.getString(sectionId,"Default");
    }
    public void setBeatId(String beat_Id){
        editor_login.putString(beatId,beat_Id);
        editor_login.commit();
    }
    public String getBeatId() {
        return loginsharedprefernce.getString(beatId,"Default");
    }



    public String getEmail(){

        return loginsharedprefernce.getString(Email,"Default");
    }

    public String getJuridiction() {
        return loginsharedprefernce.getString(Juridiction,"Default");
    }

    public String getJuridictionID() {
        return loginsharedprefernce.getString(JuridictionID,"Default");
    }

    public void setPhone(String phone){

        editor_login.putString(Phone,phone);
        editor_login.commit();
    }

    public String getPhone(){

        return loginsharedprefernce.getString(Phone,"Default");
    }


    public void setDesignation(String designation){

        editor_login.putString(Designation,designation);
        editor_login.commit();
    }

    public String getDesignation(){

        return loginsharedprefernce.getString(Designation,"Default");
    }


    public void setDivision(String division){

        editor_login.putString(Division,division);
        editor_login.commit();
    }

    public String getDivision(){

        return loginsharedprefernce.getString(Division,"Default");
    }


    public void setDisplayName(String dname){

        editor_login.putString(DisplayName,dname);
        editor_login.commit();
    }

    public String getDisplayName(){

        return loginsharedprefernce.getString(DisplayName,"Default");
    }


    public void setRanger(String ranger){

        editor_login.putString(Ranger,ranger);
        editor_login.commit();
    }


    public String getRanger(){

        return loginsharedprefernce.getString(Ranger,"Default");
    }


    public void setEmailVerifyStatus(String emailVerifyStatus){

        editor_login.putString(EmailVerifyStatus,emailVerifyStatus);
        editor_login.commit();
    }

    public void setOtp(String oTp){

        editor_login.putString(Otp,oTp);
        editor_login.commit();
    }
    public String getOtp(){

        return loginsharedprefernce.getString(Otp,"Default");
    }



    public void setOtpKey(String otpKey){

        editor_login.putString(OtpKey,otpKey);
        editor_login.commit();
    }
    public String getOtpKey(){

        return loginsharedprefernce.getString(OtpKey,"Default");
    }

    public void setLanguage(String language){

        editor_login.putString(languageSelect,language);
        editor_login.commit();
    }


    public String getLanguageSelect() {
        return loginsharedprefernce.getString(languageSelect,"");
    }

    public void clear(){
        try {
            editor_login.clear();
            editor_login.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void logoutUser(Context context){
        // Clearing all data from Shared Preferences
        try {

            editor_login.clear();
            editor_login.commit();
//        Intent i = new Intent(context, LoginActivity.class);
            Intent i = new Intent(context, LoginActivityNewGajaBandhu.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}


