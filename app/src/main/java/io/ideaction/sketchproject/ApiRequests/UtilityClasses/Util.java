package io.ideaction.sketchproject.ApiRequests.UtilityClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
   //SDF to generate a unique name for our compress file.
   public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault());
   
   /*
       compress the file/photo from @param <b>path</b> to a private location on the current device and return the compressed file.
       @param path = The original image path
       @param context = Current android Context
    */
   public static File getCompressed(Context context, String path , int width , int height) throws IOException {
      
      if(context == null)
         throw new NullPointerException("Context must not be null.");
      //getting device external cache directory, might not be available on some devices,
      // so our code fall back to internal storage cache directory, which is always available but in smaller quantity
      File cacheDir = context.getExternalCacheDir();
      if(cacheDir == null)
         //fall back
         cacheDir = context.getCacheDir();
      
      String rootDir = cacheDir.getAbsolutePath() + "/ImageCompressor";
      File root = new File(rootDir);
      
      //Create ImageCompressor folder if it doesnt already exists.
      if(!root.exists())
         root.mkdirs();
      
      //decode and resize the original bitmap from @param path.
      Bitmap bitmap = decodeImageFromFiles(path, /* your desired width*/width, /*your desired height*/ height);
      
      //create placeholder for the compressed image file
      File compressed = new File(root, SDF.format(new Date()) + ".jpg" /*Your desired format*/);
      
      //convert the decoded bitmap to stream
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        /*compress bitmap into byteArrayOutputStream
            Bitmap.compress(Format, Quality, OutputStream)
            Where Quality ranges from 1 - 100.
         */
      bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        /*
        Right now, we have our bitmap inside byteArrayOutputStream Object, all we need next is to write it to the compressed file we created earlier,
        java.io.FileOutputStream can help us do just That!
         */
      FileOutputStream fileOutputStream = new FileOutputStream(compressed);
      fileOutputStream.write(byteArrayOutputStream.toByteArray());
      fileOutputStream.flush();
      
      fileOutputStream.close();
      
      //File written, return to the caller. Done!
      return compressed;
   }
   
   public static Bitmap decodeImageFromFiles(String path, int width, int height) {
      BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
      scaleOptions.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(path, scaleOptions);
      int scale = 1;
      while (scaleOptions.outWidth / scale / 2 >= width
              && scaleOptions.outHeight / scale / 2 >= height) {
         scale *= 2;
      }
      // decode with the sample size
      BitmapFactory.Options outOptions = new BitmapFactory.Options();
      outOptions.inSampleSize = scale;
      return BitmapFactory.decodeFile(path, outOptions);
   }
   
   public static long convertStringToTimestamp(String str_date) {
      try {
         DateFormat formatter;
         formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
         // you can change format of date
         Date date = formatter.parse(str_date);
         return  date.getTime() / 1000;
      } catch (ParseException e) {
         System.out.println("Exception :" + e);
         return 0;
      }
   }
  public static Transformation cropPosterTransformation = new Transformation() {
      
      @Override public Bitmap transform(Bitmap source) {
         int targetWidth = 500;
         double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
         int targetHeight = (int) (targetWidth * aspectRatio);
         Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
         if (result != source) {
            // Same bitmap is returned if sizes are the same
            source.recycle();
         }
         return result;
      }
      
      @Override public String key() {
         return "cropPosterTransformation" + 500;
      }
   };
   
}