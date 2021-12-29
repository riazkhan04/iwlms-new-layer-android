package com.orsac.android.pccfwildlife.MyUtils;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.orsac.android.pccfwildlife.Model.ViewReportAllData.ViewReportItemData_obj;
import com.orsac.android.pccfwildlife.R;

import net.ozaydin.serkan.easy_csv.EasyCsv;
import net.ozaydin.serkan.easy_csv.FileCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


/**
 * Created by Rk on 16/09/20.
 */

public class PermissionUtils
{

    Context context;
    public static AppCompatActivity current_activity;
//    public static final String DATABASE_NAME = "pccfwl.db";
//    public static final String DATABASE_NAME = "pccfwl_new.db";
//    public static final String DATABASE_NAME = "pccfwl_v1.db";
    public static final int DATABASE_VERSION = 3;
    public static int WRITE_PERMISSON_REQUEST_CODE = 1;
    Activity activity;

    PermissionResultCallback permissionResultCallback;
    SQLiteDatabase mDb;
    ArrayList<String> permission_list=new ArrayList<>();
    ArrayList<String> listPermissionsNeeded=new ArrayList<>();

    String dialog_content="";
    int req_code;

    public PermissionUtils(Context context)
    {
        this.context=context;
        current_activity= (AppCompatActivity) context;
        permissionResultCallback= (PermissionResultCallback) context;
    }

//    public PermissionUtils(Activity activity) {
//        this.activity = activity;
//    }

    public PermissionUtils(Context context, PermissionResultCallback callback)
    {
        this.context=context;
        current_activity= (AppCompatActivity) context;
        permissionResultCallback= callback;
    }


    /**
     * Check the API Level & Permission
     *
     * @param permissions
     * @param dialog_content
     * @param request_code
     */

    public void check_permission(ArrayList<String> permissions, String dialog_content, int request_code)
    {
        this.permission_list=permissions;
        this.dialog_content=dialog_content;
        this.req_code=request_code;

        if(Build.VERSION.SDK_INT >= 23)
        {
            if (checkAndRequestPermissions(permissions, request_code))
            {
                permissionResultCallback.PermissionGranted(request_code);
                Log.i("all permissions", "granted");
                Log.i("proceed", "to callback");
            }
        }
        else
        {
            permissionResultCallback.PermissionGranted(request_code);

            Log.i("all permissions", "granted");
            Log.i("proceed", "to callback");
        }

    }


    /**
     * Check and request the Permissions
     *
     * @param permissions
     * @param request_code
     * @return
     */

    private  boolean checkAndRequestPermissions(ArrayList<String> permissions, int request_code) {

        if(permissions.size()>0)
        {
            listPermissionsNeeded = new ArrayList<>();

            for(int i=0;i<permissions.size();i++)
            {
                int hasPermission = ContextCompat.checkSelfPermission(current_activity,permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }

            }

            if (!listPermissionsNeeded.isEmpty())
            {
                ActivityCompat.requestPermissions(current_activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),request_code);
                return false;
            }
        }

        return true;
    }

    /**
     *
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0)
                {
                    Map<String, Integer> perms = new HashMap<>();

                    for (int i = 0; i < permissions.length; i++)
                    {
                        perms.put(permissions[i], grantResults[i]);
                    }

                    final ArrayList<String> pending_permissions=new ArrayList<>();

                    for (int i = 0; i < listPermissionsNeeded.size(); i++)
                    {
                        if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED)
                        {
                            if(ActivityCompat.shouldShowRequestPermissionRationale(current_activity,listPermissionsNeeded.get(i)))
                                pending_permissions.add(listPermissionsNeeded.get(i));
                            else
                            {
                                Log.i("Go to settings","and enable permissions");
                                permissionResultCallback.NeverAskAgain(req_code);
                                Toast.makeText(current_activity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                    }

                    if(pending_permissions.size()>0)
                    {
                        showMessageOKCancel(dialog_content,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                check_permission(permission_list,dialog_content,req_code);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                Log.i("permisson","not fully given");
                                                if(permission_list.size()==pending_permissions.size())
                                                    permissionResultCallback.PermissionDenied(req_code);
                                                else
                                                    permissionResultCallback.PartialPermissionGranted(req_code,pending_permissions);
                                                break;
                                        }


                                    }
                                });

                    }
                    else
                    {
                        Log.i("all","permissions granted");
                        Log.i("proceed","to next step");
                        permissionResultCallback.PermissionGranted(req_code);

                    }



                }
                break;
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(current_activity)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public interface PermissionResultCallback
    {
        void PermissionGranted(int request_code);
        void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);
        void PermissionDenied(int request_code);
        void NeverAskAgain(int request_code);
    }

    public static String getVersion_Code_Name(Context context){
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionCode = info.versionCode;
        String versionName = info.versionName;

//        Toast.makeText(this, ""+versionCode, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, ""+versionName, Toast.LENGTH_SHORT).show();
        return versionName;
    }

    public static String check_InternetConnection(Context context){
            String status = null,internet_status="true";
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    status = "Wifi enabled";
                    internet_status="true";
                    return internet_status;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    status = "Mobile data enabled";
                    internet_status="true";
                    return internet_status;
                }
            } else {
                status = "No internet is available";
                internet_status="false";
                return internet_status;
            }
            return internet_status;
    }
    public static void replaceFragment(int itemId, Fragment fragmentt) {

        //initializing the fragment object which is selected
        FragmentTransaction ft = current_activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main, fragmentt);
//                ft.addToBackStack(""+itemId);
        ft.commit();
//        ft.commitAllowingStateLoss();

    }
    public static String calculateElephant(int totalNo,int herdNo,int tuskerNo,int maleNo,int femaleNo,int calfNo,Context context){

        int sum=tuskerNo+maleNo+femaleNo+calfNo;
        String type="";
        if (totalNo==sum && totalNo!=0){
            type="match";
//            Toast.makeText(context, type, Toast.LENGTH_SHORT).show();
        }
        else {
            type="not_match";
//            Toast.makeText(context, type, Toast.LENGTH_SHORT).show();
        }
        return type;
    }

    public static void no_internet_Dialog(Context context, String dialog_title, AsyncTask<Integer ,Integer, List<Integer>> asyncTask)
    {
        Dialog dialog=new Dialog(context);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.no_internet_layout);

        Button try_again=dialog.findViewById(R.id.try_again_btn);
        TextView no_internet_txt=dialog.findViewById(R.id.no_internet_txt);
        ImageView close=dialog.findViewById(R.id.close);

        try_again.setText("OK");
        if (!dialog_title.equalsIgnoreCase("")) {
            no_internet_txt.setText(dialog_title);
        }
        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                if (PermissionUtils.check_InternetConnection(context)=="true"){

                    if (asyncTask!=null){
                        asyncTask.execute();

                    }
                }else {
                    PermissionUtils.no_internet_Dialog(context,"No Internet Connection !",asyncTask);
                }

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public static void showProgress(Context context, ProgressBar progressBar){
        progressBar.setVisibility(View.VISIBLE);
    }
    public static void hideProgress(Context context, ProgressBar progressBar){
        progressBar.setVisibility(View.GONE);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void createPdf(Context context, ArrayList<ViewReportItemData_obj> jsontxt, String fromDate, String toDate){
        try {

        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
//        canvas.drawCircle(50, 50, 30, paint);
        canvas.drawText(jsontxt.toString(), 10, 20, paint);
        //canvas.drawt
        // finish the page
        document.finishPage(page);
// draw text on the graphics object of the page
        // Create Page 2

        pageInfo = new PdfDocument.PageInfo.Builder(300, 600, jsontxt.size()).create();
        page = document.startPage(pageInfo);
//        canvas = page.getCanvas();
//        canvas.drawCircle(100, 100, 100, paint);
        document.finishPage(page);
        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/wildlifePdf/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path+fromDate+"_"+toDate+".pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(context, "PDF Created !", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(context, "Something went wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void createPdfFile(Context context, ArrayList<ViewReportItemData_obj> jsontxt, String fromDate, String toDate){
        try {

            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/wildlifePdf/";
            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String targetPdf = directory_path+fromDate+"_"+toDate+".pdf";

            Document document=new Document(PageSize.A4, 10, 10, 10, 10);
            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(targetPdf));

            document.open();
            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("Android School");


            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            /**
             * How to USE FONT....
             */
            BaseFont urName = BaseFont.createFont("", "utf-8", BaseFont.EMBEDDED);
            // Title Order Details...
            // Adding Title....
            Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            // Creating Chunk
            Chunk mOrderDetailsTitleChunk = new Chunk("View Reports", mOrderDetailsTitleFont);
            // Creating Paragraph to add...
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            // Setting Alignment for Heading
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            // Finally Adding that Chunk
            document.add(mOrderDetailsTitleParagraph);

            // Fields of Order Details...
// Adding Chunks for Title and value
            Font mOrderIdFont = new Font(urName, 15.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderIdChunk = new Chunk("Date: "+jsontxt.get(0).getSighting_date(), mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);

            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            document.close();

            Toast.makeText(context, "PDF Created !", Toast.LENGTH_LONG).show();


        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Error creating Pdf !", Toast.LENGTH_LONG).show();
        }
    }
    public static String createPdfFromImage(Activity context,String fromDate, String toDate,Bitmap bitmap){
        String targetPdf="";
        try {

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            float hight = displaymetrics.heightPixels ;
            float width = displaymetrics.widthPixels ;

            int convertHighet = (int) hight, convertWidth = (int) width;

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();


            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawPaint(paint);

            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

            paint.setColor(Color.BLUE);
            canvas.drawBitmap(bitmap, 0, 0 , null);
            document.finishPage(page);

            // write the document content
            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/wildlifePdf/";
            targetPdf = directory_path+fromDate+"_"+toDate+".pdf";
            File filePath = new File(targetPdf);
            try {
                document.writeTo(new FileOutputStream(filePath));
//                btn_convert.setText("Check PDF");
                Toast.makeText(context, "PDF Created !", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
            }

            // close the document
            document.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return targetPdf;
    }

    public static Bitmap createImageScreenShotForView(View view, Context context){
        Bitmap bm=null;
        try {
            view.setDrawingCacheEnabled(true);
            int totalHeight = view.getHeight();
            int totalWidth = view.getWidth();
            view.layout(0, 0, totalWidth, totalHeight);
            view.buildDrawingCache(true);
            bm = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
//                    Toast.makeText(IncidentReportingActivity.this, "Taking Screenshot", Toast.LENGTH_SHORT).show();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, null, null);

//            image_uri=getImageUri(context,bm);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bm;
    }
    private Uri getImageUri(Context contexts, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(contexts.getContentResolver(), photo, "reports", null);
        return Uri.parse(path);
    }

    //create bitmap from the ScrollView
    public static Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            Drawable bgDrawable = view.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            view.draw(canvas);
        }catch (Exception e){
            e.printStackTrace();
        }

        return bitmap;
    }
    public static void sharePdfInSocialMedia(Context context,String pdffile_nm){
        try {

            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/wildlifePdf";
            File pdf_path= new File(pdffile_nm);//change with your path
//            File pdf_path= new File(directory_path + "/"+pdffile_nm+".pdf");//change with your path

            Toast.makeText(context, "Please wait for sharing in social media...!", Toast.LENGTH_SHORT).show();
            if (pdf_path.exists()) {
                Uri pdfPAthUri= FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pdf_path);//used for higher version as uri not supported file provider used
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM,pdfPAthUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdf_path));
                intent.setType("application/pdf");
                context.startActivity(Intent.createChooser(intent,"Choose File"));
//                context.startActivity(intent);
            }
            else {
                Log.i("DEBUG", "File doesn't exist");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getCurrentDate(String format){
        String current_date="";
        try {
            Date d = new Date();
            current_date  = DateFormat.format(format, d.getTime()).toString();

        }catch (Exception e){
            e.printStackTrace();
        }
        return current_date;
    }
    public static String getCurrentDateMinusOne(String format){
        String current_date="";
        try {
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.DATE,-1);
//            Date d = new Date();
            current_date  = DateFormat.format(format, calendar.getTime()).toString();

        }catch (Exception e){
            e.printStackTrace();
        }
        return current_date;
    }

    public static String getTimeMinusOne(String format){
        String time="";
        try {
            Calendar calendar=Calendar.getInstance();
            calendar.add(Calendar.HOUR,-1);
           time=DateFormat.format(format,calendar.getTime()).toString();

        }catch (Exception e){
            e.printStackTrace();
        }
        return time;
    }
    public static String getCurrentTime(String format){
        String time="";
        try {
            Calendar calendar=Calendar.getInstance();
            time=DateFormat.format(format,calendar.getTime()).toString();

        }catch (Exception e){
            e.printStackTrace();
        }
        return time;
    }

    public static String convertDate(String dateStr,String givenFormat,String outputFormat){
        SimpleDateFormat input = new SimpleDateFormat(givenFormat);
        String date_formatted="";
        SimpleDateFormat output = new SimpleDateFormat(outputFormat);
        Date d = null;
        try
        {
            d = input.parse(dateStr);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        date_formatted = output.format(d);
        Log.i("DATE", "" + date_formatted);
        return date_formatted;
    }

    public static long convert_date_to_timestamp1(String inputformat,String dates){

        try {
            java.text.DateFormat formatter = new SimpleDateFormat(inputformat, Locale.US);
            Date date = formatter.parse(dates);
            return date.getTime();
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    public static void setLocale(String lang, Activity activity) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config,
                activity.getBaseContext().getResources().getDisplayMetrics());

    }
    public static void loadExcel(Activity activity,String path,ViewReportItemData_obj viewReportItemData_obj){
        try {

            EasyCsv easyCsv = new EasyCsv(activity);

            List<String> headerList = new ArrayList<>();
//            headerList.add("Name.Surname.Age-");
            headerList.add("Date.From_date.To_Date.Division.Range.Section.Beat.Total.Herd.Tusker.Makhna.Female.Calf,Latitude.Longitude-");

            ArrayList<ViewReportItemData_obj> arrayList=new ArrayList<>();
            List<String> dataList = new ArrayList<>();
            arrayList.add(viewReportItemData_obj);
            for (int i=0;i<arrayList.size();i++){
                dataList.add(viewReportItemData_obj.getSighting_date()+","+viewReportItemData_obj.getSighting_time_from()+","+
                        viewReportItemData_obj.getSighting_time_to()+"."+viewReportItemData_obj.getDivision()+","
                        +viewReportItemData_obj.getRange()+","+viewReportItemData_obj.getSection()+","+
                        viewReportItemData_obj.getBeat()+","+viewReportItemData_obj.getTotal()+","+
                        viewReportItemData_obj.getHeard()+","+viewReportItemData_obj.getTusker()+","+
                        viewReportItemData_obj.getMukhna()+","+viewReportItemData_obj.getFemale()+","+
                        viewReportItemData_obj.getCalf()+","+viewReportItemData_obj.getLatitude()+","+
                        viewReportItemData_obj.getLongitudes()+"-");
            }

//            dataList.add("Riaz.Khan.29-");

            easyCsv.setSeparatorColumn(",");
            easyCsv.setSeperatorLine("-");

            String fileName="EasyCsv";


            easyCsv.createCsvFile(path, headerList, dataList, WRITE_PERMISSON_REQUEST_CODE, new FileCallback() {
                @Override
                public void onSuccess(File file) {
                    Toast.makeText(activity,"Saved file",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(String err) {
                    Toast.makeText(activity,"Error",Toast.LENGTH_SHORT).show();

                }
            });

//            String dataDir = Utils.getSharedDataDir(ImportingFromJson.class) + "Data/";

// Instantiating a Workbook object
//            Workbook workbook = new Workbook();
//            Worksheet worksheet = workbook.getWorksheets().get(0);
//
//// Read File
//            File file = new File(dataDir + "Test.json");
//            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//            String jsonInput = "";
//            String tempString;
//            while ((tempString = bufferedReader.readLine()) != null) {
//                jsonInput = jsonInput + tempString;
//            }
//            bufferedReader.close();
//
//// Set Styles
//            CellsFactory factory = new CellsFactory();
//            Style style = factory.createStyle();
//            style.setHorizontalAlignment(TextAlignmentType.CENTER);
//            style.getFont().setColor(Color.getBlueViolet());
//            style.getFont().setBold(true);
//
//// Set JsonLayoutOptions
//            JsonLayoutOptions options = new JsonLayoutOptions();
//            options.setTitleStyle(style);
//            options.setArrayAsTable(true);
//
//// Import JSON Data
//            JsonUtility.importData(jsonInput, worksheet.getCells(), 0, 0, options);
//
//// Save Excel file
//            workbook.save(dataDir + "ImportingFromJson.out.xlsx");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void openFileFolder(String folderNm,Context context){
        try {
            File file = new File(folderNm);
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
            String type = map.getMimeTypeFromExtension(ext);
            if (type == null)
                type = "*/*";

            Intent intent = new Intent(Intent.ACTION_VIEW);
//            Uri data = Uri.fromFile(file);
            Uri data = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//without permission we cannot read file
            intent.setDataAndType(data, type);
            context.startActivity(Intent.createChooser(intent, "Open with"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}


