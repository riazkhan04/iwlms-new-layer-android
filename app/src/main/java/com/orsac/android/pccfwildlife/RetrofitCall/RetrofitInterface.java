package com.orsac.android.pccfwildlife.RetrofitCall;

import com.orsac.android.pccfwildlife.Model.AllLayerModel.StGeojsonObj;
import com.orsac.android.pccfwildlife.Model.AllReportCountModel;
import com.orsac.android.pccfwildlife.Model.AllReportCountModelInADay;
import com.orsac.android.pccfwildlife.Model.AppVersionModel;
import com.orsac.android.pccfwildlife.Model.ChangePasswordModel.ChangePasswordRequestModel;
import com.orsac.android.pccfwildlife.Model.ChangePasswordModel.ChangeUserNameModel;
import com.orsac.android.pccfwildlife.Model.ChangePasswordModel.OtpVerifiedModel;
import com.orsac.android.pccfwildlife.Model.IncidentDataModel;
import com.orsac.android.pccfwildlife.Model.AllLayerModel.MapDataResponse;
import com.orsac.android.pccfwildlife.Model.ViewIncidentReportCount;
import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
import com.orsac.android.pccfwildlife.Model.VulnerabilityModel.VulnerableCircleResponse;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllCircleData;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllDivisionModel;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllDivision_data;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.AllNewBeatModels;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.BeatDataBySecId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.DivisionDataByCircleId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.RangeDataByDivId;
import com.orsac.android.pccfwildlife.Model.AllDivisionModelData.SectionDataByRangeId;
import com.orsac.android.pccfwildlife.Model.AllLatLngModel;
import com.orsac.android.pccfwildlife.Model.EditReportResponseData;
import com.orsac.android.pccfwildlife.Model.FileUploadResponse;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuCreateUserModel;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuProfileModel;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuReportDataModel;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GenerateOtpResponseModel;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.ElephantDeathIncidentDataModel;
import com.orsac.android.pccfwildlife.Model.IncidentReportData.IncidentReportDataModel;
import com.orsac.android.pccfwildlife.Model.LoginModel.LoginData;
import com.orsac.android.pccfwildlife.Model.ProfileModel.ProfileData;
import com.orsac.android.pccfwildlife.Model.ProfileModel.SuccessResponseData;
import com.orsac.android.pccfwildlife.Model.ReportResponse.ReportAddResponse;
import com.orsac.android.pccfwildlife.Model.ReportResponse.ViewReportResponse;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

//    @Headers("Content-Type: application/json")
    @POST("signin")
    Call<LoginData> getlogin_api(@Body RequestBody loginRequest);
//    Call<ArrayList<LoginData>> getlogin_api(@Body RequestBody loginRequest);

    @GET("getAllCircle")
    Call<ArrayList<AllCircleData>> getAllCircle();

    @GET("GetAllDivision")
    Call<AllDivision_data> get_alldivisions();

    @GET("getAllDivision")
    Call<ArrayList<AllDivisionModel>> getAlldivision();

    @GET("getAllBeat")
    Call<ArrayList<AllNewBeatModels>> getAllBeats();

    @POST("addReport")
    Call<ReportAddResponse> addreportApi(@Query("fieName") String fileName,
                                         @Body RequestBody reportRequest);

    @Multipart
    @POST("uploadFile")
    Call<FileUploadResponse> addfileUpload(@Query("iemiNo") String imeiNo,
                                           @Query("userId") String userId,
                                           @Query("type") String type,
                                           @Part MultipartBody.Part image);
//    @Multipart
//    @POST("addReport")
//    Call<ReportAddResponse> addreportApi(@Query("iemiNo") String imeiNo,
//                                         @Body RequestBody reportRequest);

    @POST("addNillReport")
    Call<ReportAddResponse> add_NilReportApi(@Body RequestBody reportRequest);

    @GET("viewAllReport")
    Call<ViewReportResponse> get_ViewAllReport(@Query("division")String division);
//                                            @Query("userId")String uid,
//                                            @Query("startDate")String sighting_date_from,
//                                            @Query("endDate")String sighting_date_to);
    @POST("EditReport")
    Call<EditReportResponseData> editReport(@Query("report_id")String report_id,@Body RequestBody reportRequest);

    @GET("getLatLong")
    Call<ArrayList<AllLatLngModel>> get_allLatlng(@Query("division")String division);

    @GET("getAllReportCount")
    Call<AllReportCountModel> getAllReportCount(@Query("division")String division);
//                                                @Query("startDate")String startDate,
//                                                @Query("endDate")String endDate);

    @GET("viewReport")
    Call<ArrayList<ViewReportItemData_obj>> get_ViewReport(@Query("division")String division,
                                                           @Query("circle")String circle,
                                                           @Query("range")String range,
                                                           @Query("section")String section,
                                                           @Query("beat")String beat,
                                                           @Query("startDate")String sighting_date_from,
                                                           @Query("endDate")String sighting_date_to,
                                                           @Query("reportType")String reportType);

    @GET("getuserById")
    Call<ProfileData> getprofile_api(@Query("loginId") String loginId);

    @PUT("updateuser")
    Call<SuccessResponseData> updateProfile_api(@Body RequestBody reportRequest);

    @Multipart
//    @POST("uploadProfileImage")
    @POST("uploadImage")
    Call<FileUploadResponse> updateImage(@Part MultipartBody.Part image,
                                    @Query("userId") String userId,
                                    @Query("type") String type);

    @GET("getTotalIncidentCount")
    Call<ArrayList<IncidentDataModel>> getTotalIncidentCountApi(@Query("circlreId")String circlreId,
                                                                @Query("divisionId")String divisionId,
                                                                @Query("rangeId")String rangeId,
                                                                @Query("sectionId")String sectionId,
                                                                @Query("role")String role);

//   @POST("addIncidentReport")
//    Call<ReportAddResponse> add_IncidentReportApi(@Query("fileName") String fileName,
//                                                  @Body RequestBody reportRequest);
    @POST("addIncidentReport")
    Call<ReportAddResponse> add_IncidentReportApi(@Query("fileName") String fileName,
                                                  @Body RequestBody reportRequest);

    @GET("getAllReportCountIn24Hrs")
    Call<AllReportCountModelInADay> getAllReportCountIn24hrs(@Query("division") String division);

    @GET("getAllReportCountIn24Hrsss")
    Call<AllReportCountModelInADay> getAllReportCountIn24hrsss(@Query("circle") String circle,
                                                               @Query("division") String division,
                                                               @Query("range") String range,
                                                               @Query("startDate")String startDate,
                                                               @Query("endDate")String endDate
                                                                );

    @GET("viewAllIncidentReport")
    Call<ArrayList<ViewIncidentReportCount>> getviewAllIncidentReportCount(@Query("circlreId")String circlreId,
                                                                           @Query("divisionId")String divisionId,
                                                                           @Query("rangeId")String rangeId,
                                                                           @Query("startDate")String startDate,
                                                                           @Query("endDate")String endDate);

    @GET("getAllDivisionByCircleId")
    Call<ArrayList<DivisionDataByCircleId>> getAllDivisionByCircleId(@Query("id") String id);

    @GET("getAllRangeByDivid")
    Call<ArrayList<RangeDataByDivId>> getAllRangeByDivid(@Query("id") String id);

    @GET("getAllSectionByRangeid")
    Call<ArrayList<SectionDataByRangeId>> getAllSectionByRangeid(@Query("id") String id);

    @GET("getAllBeatBySecId")
    Call<ArrayList<BeatDataBySecId>> getAllBeatBySecId(@Query("id") String id);

    @GET("viewAllElephantDeathReport")
    Call<ArrayList<ViewIncidentReportCount>> getviewAllElephantDeathReportCount(@Query("circlreId")String circlreId,
                                                                           @Query("divisionId")String divisionId,
                                                                           @Query("rangeId")String rangeId,
                                                                           @Query("startDate")String startDate,
                                                                           @Query("endDate")String endDate
                                                                            );

    @POST("addElephantDeathReport")
    Call<ReportAddResponse> add_ElephantDeathReportApi(@Query("fileName") String fileName,
                                                       @Body RequestBody reportRequest);

    @GET("getAllLatLongIn24")
    Call<ArrayList<AllLatLngModel>> get_allLatlngIn24hrs();

    @GET("viewReportLast24hrs")
    Call<ArrayList<ViewReportItemData_obj>> getviewReportLast24ForMap(@Query("circle")String circle,
                                                                           @Query("division")String division,
                                                                           @Query("range")String range,
                                                                           @Query("startDate")String sighting_date_from,
                                                                           @Query("endDate")String sighting_date_to);

    @GET("checkEmailAvail")
    Call<Boolean> checkValidEmail(@Query("email")String email);

    @GET("viewAllElephantDeathReportzz")
    Call<ArrayList<ElephantDeathIncidentDataModel>> get_ViewElephantDeathReport(@Query("circlreId")String circle,
                                                                                @Query("divisionId")String division,
                                                                                @Query("rangeId")String range,
                                                                                @Query("sectionId")String section,
                                                                                @Query("beatId")String beat,
                                                                                @Query("startDate")String sighting_date_from,
                                                                                @Query("endDate")String sighting_date_to);

    @GET("viewAllIncidentReportzz")
    Call<ArrayList<IncidentReportDataModel>> get_ViewIncidentReport(@Query("circlreId")String circle,
                                                                    @Query("divisionId")String division,
                                                                    @Query("rangeId")String range,
                                                                    @Query("sectionId")String section,
                                                                    @Query("beatId")String beat,
                                                                    @Query("startDate")String sighting_date_from,
                                                                    @Query("endDate")String sighting_date_to,
                                                                    @Query("reportType")String reportType);

    @GET("viewReportsByReportsId")
    Call<ArrayList<ViewReportItemData_obj>>  getviewReportsByUserId(@Query("surveyorUserId")String userId,
                                                                      @Query("sightingDateFrom")String fromDate,
                                                                      @Query("sightingDateTo")String toDate);

    @Multipart
    @POST("report")
    Call<GajaBandhuReportDataModel> addGajaBandhuReport(@Part("textMessage") RequestBody textMsg,
                                                        @Part("userID") RequestBody userId,
                                                        @Part("latitude") RequestBody latitude,
                                                        @Part("longitude") RequestBody longitude,
                                                        @Part MultipartBody.Part image,
                                                        @Part MultipartBody.Part audio,
                                                        @Part MultipartBody.Part video,
                                                        @Part("type") RequestBody folderName,
                                                        @Part("status") RequestBody status,
                                                        @Part("reportingDate") RequestBody reportingDate
                                                        );

//    @Headers("Content-Type: application/json")
    @POST("createUser")
    Call<GenerateOtpResponseModel> add_GajaBandhuUser(@Body GajaBandhuCreateUserModel createRequest);

    @GET("getAllReportByUserID")
    Call<ArrayList<GajaBandhuReportDataModel>> getAllReportOfGajaBandhuByUserId(@Query("userId")String userId);

    @POST("generateOtp")
    Call<GenerateOtpResponseModel> call_generateOtp(@Query("mobile")String mobile);

    @GET("getUserById")
    Call<GajaBandhuProfileModel> getaGajaBandhuprofile_api(@Query("userId") String userId);

    //  for gajaBandhu Profile image update
    @Multipart
    @POST("uploadPhoto")
    Call<String> updateGajaProfileImage(@Part MultipartBody.Part image,
                                       @Part("type") RequestBody longitude,
                                       @Part("userId") RequestBody userId);

    @GET("getVulnerabilityCountReports")
    Call<ArrayList<VulnerableCircleResponse>> getVulnerableDetails(@Query("circle") String circle,
                                                                   @Query("division") String division,
                                                                   @Query("range") String range,
                                                                   @Query("startDate")String startDate,
                                                                   @Query("endDate")String endDate);

    @POST("changePassWord")
    Call<SuccessResponseData> changePassword(@Body ChangePasswordRequestModel createRequest);

    @POST("changeUsername")
    Call<SuccessResponseData> changeUsername(@Body ChangeUserNameModel createRequest);

    @POST("generateotp")
    Call<SuccessResponseData> generateOtp(@Query("mobileNumber") String mobileNo);

    @POST("otpVerified")
    Call<SuccessResponseData> otpVerified(@Body OtpVerifiedModel createRequest);

    @PUT("resetPasswordWithMobile")
    Call<SuccessResponseData> resetPasswordWithMobile(@Query("mobile") String mobileNo,
                                                      @Query("newPassword") String newPassword);
    @PUT("resetUsernameWithMobile")
    Call<SuccessResponseData> resetUsernameWithMobile(@Query("mobile") String mobileNo,
                                                      @Query("newUsername") String newUsername);
    @GET("getAppVersion")
    Call<AppVersionModel> getAppVersion();

    @POST("{layer}")
//    @POST("getCircleGeojson")
    @FormUrlEncoded
    Call<MapDataResponse> callMapApi(@FieldMap Map<String, String> params, @Path("layer") String layer);

    @GET("{layer}")
    Call<ArrayList<StGeojsonObj>> callAllLayerMapApi(@Path("layer") String layer);

}
