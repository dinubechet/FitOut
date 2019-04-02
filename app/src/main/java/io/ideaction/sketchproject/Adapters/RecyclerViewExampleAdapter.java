package io.ideaction.sketchproject.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

import io.ideaction.sketchproject.ApiRequests.UtilityClasses.Util;
import io.ideaction.sketchproject.Models.Image;
import io.ideaction.sketchproject.R;

public class RecyclerViewExampleAdapter extends RecyclerView.Adapter<RecyclerViewExampleAdapter.ViewHolder> {
   private List<Image> itemsData;
   Context context;
   
   public RecyclerViewExampleAdapter(List<Image> itemsData, Context context) {
      this.itemsData = itemsData;
      this.context = context;
   }
   
   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
      View itemLayoutView = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.item_layout, null);
      
      ViewHolder viewHolder = new ViewHolder(itemLayoutView);
      return viewHolder;
   }
   
   @Override
   public void onBindViewHolder(ViewHolder viewHolder, final int position) {
      Picasso.get().load(itemsData.get(position).getPath()).transform(Util.cropPosterTransformation).placeholder(R.drawable.default_image).into(viewHolder.imgViewIcon);
      viewHolder.imgViewIcon.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            ArrayList<String> list = new ArrayList<>();
            for (Image x : itemsData)
               list.add(x.getPath());
            
            new ImageViewer.Builder(context, list)
                    .setStartPosition(position)
                    .show();
         }
      });
   }
   
   public static class ViewHolder extends RecyclerView.ViewHolder {
      public ImageView imgViewIcon;
      public ViewHolder(View itemLayoutView) {
         super(itemLayoutView);
         imgViewIcon = itemLayoutView.findViewById(R.id.item_icon);
         imgViewIcon.setAdjustViewBounds(true);
      }
   }
   @Override
   public int getItemCount() {
      return itemsData.size();
   }
}