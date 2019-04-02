package io.ideaction.sketchproject.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.khizar1556.mkvideoplayer.MKPlayer;
import com.khizar1556.mkvideoplayer.MKPlayerActivity;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.ideaction.sketchproject.Activities.MediaPlayerAc;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.FileUtils;
import io.ideaction.sketchproject.Models.BannerModer;
import io.ideaction.sketchproject.R;

public class GridImgAdapter extends BaseAdapter {
   
   List<BannerModer> list;
   Context context;
   
   
   public GridImgAdapter(List<BannerModer> list, Context context) {
      this.list = list;
      this.context = context;
   }
   
   @Override
   public int getCount() {
      return list.size();
   }
   
   @Override
   public Object getItem(int position) {
      return null;
   }
   
   @Override
   public long getItemId(int position) {
      return 0;
   }
   
   @Override
   public View getView(final int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View rowView = inflater.inflate(R.layout.cell_gallerry, parent, false);
      ImageView img = rowView.findViewById(R.id.img);
      RelativeLayout videoFrame = rowView.findViewById(R.id.videoFrame);
      ImageView imgVideo = rowView.findViewById(R.id.videoimg);
      if (list.get(position).isVideo()) {
         videoFrame.setVisibility(View.VISIBLE);
         img.setVisibility(View.GONE);
       if (list.get(position).getUrl().contains("http") ) {
          Picasso.get().load(list.get(position).getVideoThimb()).placeholder(R.drawable.default_image).into(imgVideo);
          } else  {
          Bitmap bitmap=ThumbnailUtils.createVideoThumbnail(list.get(position).getUrl(),MediaStore.Video.Thumbnails.MINI_KIND);
          imgVideo.setImageBitmap(bitmap);

       }
         rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent inten = new Intent(context , MediaPlayerAc.class);
                inten.putExtra("path" , list.get(position).getUrl());
                context.startActivity(inten);

            }
         });
         
      } else {
         img.setVisibility(View.VISIBLE);
         videoFrame.setVisibility(View.GONE);


         if (list.get(position).getUrl().contains("http")) {
            Picasso.get().load(list.get(position).getUrl()).placeholder(R.drawable.default_image).into(img);
            
            rowView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  ArrayList<String> list3 = new ArrayList<>();
                  for (BannerModer x : list)
                     list3.add(x.getUrl());
                  
                  new ImageViewer.Builder(context, list3)
                          .setStartPosition(position)
                          .show();
               }
            });
            
         } else
            Picasso.get().load(new File(list.get(position).getUrl())).placeholder(R.drawable.default_image).into(img);
      }
      return rowView;
   }
}
