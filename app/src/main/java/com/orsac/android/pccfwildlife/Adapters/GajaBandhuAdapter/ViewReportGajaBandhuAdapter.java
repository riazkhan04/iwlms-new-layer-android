package com.orsac.android.pccfwildlife.Adapters.GajaBandhuAdapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orsac.android.pccfwildlife.Activities.IncidentReportViewActivity;
import com.orsac.android.pccfwildlife.MyUtils.PermissionUtils;
import com.orsac.android.pccfwildlife.R;
import com.orsac.android.pccfwildlife.RetrofitCall.RetrofitClient;
import com.potyvideo.library.AndExoPlayerView;
import com.orsac.android.pccfwildlife.Model.GajaBandhuModel.GajaBandhuReportDataModel;
import com.ravikoradiya.zoomableimageview.ZoomableImageView;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ViewReportGajaBandhuAdapter extends RecyclerView.Adapter<ViewReportGajaBandhuAdapter.ViewHolder> {

    ArrayList<GajaBandhuReportDataModel> viewReportArr;
    Context context;
    String filePath="",type="";

    public ViewReportGajaBandhuAdapter(ArrayList<GajaBandhuReportDataModel> viewReportArr, Context context) {
        this.viewReportArr = viewReportArr;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.view_report_item_gaja_bandhu,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {

            holder.date_txt.setText(PermissionUtils.convertDate(viewReportArr.get(position).getReportingDate(),
                    "yyyy-MM-dd HH:mm:sss","dd-MM-yyyy"));

            if (!viewReportArr.get(position).getImgPath().equalsIgnoreCase("")&&
                    (viewReportArr.get(position).getImgPath()!=null)
                    ){
                holder.view_txt.setText("View your Image report");
                holder.audio_video_img.setImageResource(R.drawable.ic_menu_gallery);
                holder.audio_video_img.setVisibility(View.VISIBLE);
            }else if (!viewReportArr.get(position).getAudioMessagePath().equalsIgnoreCase("")&&
                    viewReportArr.get(position).getAudioMessagePath()!=null
                    ){
                holder.view_txt.setText("View your audio report");
                holder.audio_video_img.setImageResource(R.drawable.ic_record);
                holder.audio_video_img.setVisibility(View.VISIBLE);
            }else if (!viewReportArr.get(position).getVideoMessagePath().equalsIgnoreCase("")&&
                    viewReportArr.get(position).getVideoMessagePath()!=null
                    ){
                holder.view_txt.setText("View your video report");
                holder.audio_video_img.setImageResource(R.drawable.ic_play);
                holder.audio_video_img.setVisibility(View.VISIBLE);
            } else {
                holder.view_txt.setText("View your text report");
                holder.audio_video_img.setVisibility(View.INVISIBLE);
            }

            holder.item_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewReportArr.get(position).getImgPath()!=null
                    && !viewReportArr.get(position).getImgPath().equalsIgnoreCase("")){
                        type="image";
                        filePath=viewReportArr.get(position).getImgPath();
                    }else if (viewReportArr.get(position).getAudioMessagePath()!=null
                            && !viewReportArr.get(position).getAudioMessagePath().equalsIgnoreCase("")){
                        type="audio";
                        filePath=viewReportArr.get(position).getAudioMessagePath();
                    }else if (viewReportArr.get(position).getVideoMessagePath()!=null
                            && !viewReportArr.get(position).getVideoMessagePath().equalsIgnoreCase("")){
                        type="video";
                        filePath=viewReportArr.get(position).getVideoMessagePath();
                    }

                    if (viewReportArr.get(position).getImgPath().equalsIgnoreCase("")&&
                            viewReportArr.get(position).getAudioMessagePath().equalsIgnoreCase("") &&
                            viewReportArr.get(position).getVideoMessagePath().equalsIgnoreCase("") ||
                            viewReportArr.get(position).getImgPath()==null &&
                            viewReportArr.get(position).getAudioMessagePath()==null &&
                            viewReportArr.get(position).getVideoMessagePath()==null){
                        type="text";
                    }
                    callAudioVideoDialog(context,viewReportArr.get(position).getDescription(),
                            filePath,type,viewReportArr.get(position));
                }
            });






        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return viewReportArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView view_txt,date_txt;
        ImageView audio_video_img;
        LinearLayout item_ll;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view_txt=itemView.findViewById(R.id.view_txt);
            date_txt=itemView.findViewById(R.id.date_txt);
            audio_video_img=itemView.findViewById(R.id.audio_video_img);
            item_ll=itemView.findViewById(R.id.item_ll);
        }
    }
    public void callAudioVideoDialog(Context context,String textMsg,String filePath,String type,
                                     GajaBandhuReportDataModel viewReportArr){
        try {
            Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.audio_video_dialog);

            AndExoPlayerView andExoPlayerView;
            ImageView cancel_img,report_image;
            TextView text_msg,division_txt,range_txt,section_txt,
                    beat_txt,date_txt,title,status_txt;
            text_msg=dialog.findViewById(R.id.textMsg);
            cancel_img=dialog.findViewById(R.id.cancel_img);
            report_image=dialog.findViewById(R.id.report_image);
            andExoPlayerView=dialog.findViewById(R.id.andExoPlayerView);
            division_txt=dialog.findViewById(R.id.division_txt);
            range_txt=dialog.findViewById(R.id.range_txt);
            section_txt=dialog.findViewById(R.id.section_txt);
            beat_txt=dialog.findViewById(R.id.beat_txt);
            date_txt=dialog.findViewById(R.id.date_txt);
            title=dialog.findViewById(R.id.title);
            status_txt=dialog.findViewById(R.id.status_txt);

            if (textMsg!=null){
                text_msg.setText(textMsg);
                text_msg.setVisibility(View.VISIBLE);
            }else {
                text_msg.setVisibility(View.GONE);
            }
            if (viewReportArr.getDivision()!=null){
                String div=""+viewReportArr.getDivision();
                String rng=""+viewReportArr.getRange();
                String sec=""+viewReportArr.getSection();
                String beat=""+viewReportArr.getBeat();

//                division_txt.setText(context.getResources().getString(R.string.division_txt)+viewReportArr.getDivision());
                division_txt.setText( changeTextBold(div,0,11));
                range_txt.setText( changeTextBold(rng,0,8));
                section_txt.setText( changeTextBold(sec,0,10));
                beat_txt.setText( changeTextBold(beat,0,7));
                date_txt.setText( PermissionUtils.convertDate(viewReportArr.getReportingDate(),
                        "yyyy-MM-dd HH:mm:sss","dd-MM-yyyy"));
                String statuss=viewReportArr.getStatus();
                if (!statuss.equalsIgnoreCase("")||statuss!=null){
                    status_txt.setText(statuss);
                }
            }

            cancel_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            if (type.equalsIgnoreCase("image")){
                andExoPlayerView.setVisibility(View.GONE);
                report_image.setVisibility(View.VISIBLE);

                Glide.with(context)
//                        .load("http://203.129.207.133:8080/wildlife/api/v1/uploadController/downloadFile?location="
                        .load(RetrofitClient.IMAGE_URL+"gajabandhu/"
                                +filePath)
                        .error(R.drawable.ic_menu_gallery)
                        .into(report_image);
                title.setText(type.toUpperCase()+" REPORT");

            }
            else if (type.equalsIgnoreCase("text")){
                andExoPlayerView.setVisibility(View.GONE);
                report_image.setVisibility(View.GONE);
                title.setText(type.toUpperCase()+" REPORT");
            }
            else if (type.equalsIgnoreCase("audio")){
                report_image.setVisibility(View.GONE);
                andExoPlayerView.setVisibility(View.VISIBLE);
                andExoPlayerView.setSource(RetrofitClient.IMAGE_URL+"gajabandhu/" +
                        filePath);
                title.setText(type.toUpperCase()+" REPORT");
//                        "gb_voice_-375807259.mp3");
//                andExoPlayerView.setSource("https://host2.rj-mw1.com/media/podcast/mp3-192/Tehranto-41.mp3");
//                andExoPlayerView.setSource("https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
            }
            else {
                title.setText(type.toUpperCase()+" REPORT");
                report_image.setVisibility(View.GONE);
                andExoPlayerView.setVisibility(View.VISIBLE);
//                andExoPlayerView.setSource("https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_5mb.mp4");
                andExoPlayerView.setSource(RetrofitClient.IMAGE_URL+"gajabandhu/" +
                        filePath);
            }

            report_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (filePath!=null){
                        callImageViewDialog(context, RetrofitClient.IMAGE_URL+"gajabandhu/" +filePath);
                    }
                }
            });


            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String  changeTextBold(String text_area,int start,int end){
        String changeTxt="";
        try {
             SpannableString ss = new SpannableString(text_area);
            ForegroundColorSpan green = new ForegroundColorSpan(Color.GREEN);
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            ss.setSpan(green, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            changeTxt=""+ss;
        }catch (Exception e){
            e.printStackTrace();
        }
        return changeTxt;
    }

    public void callImageViewDialog(Context context,String imgpath){
        try {

            Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.image_view_dialog);

            ImageView report_image,close_img;
            ZoomableImageView report_imgg;
//            report_image=dialog.findViewById(R.id.report_image);
            report_imgg=dialog.findViewById(R.id.report_image);
            close_img=dialog.findViewById(R.id.close_img);

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
//https://github.com/halilozercan/BetterVideoPlayer
//https://github.com/khizar1556/MKVideoPlayer