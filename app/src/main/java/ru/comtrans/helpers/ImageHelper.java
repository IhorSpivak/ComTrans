package ru.comtrans.helpers;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Artco on 17.02.2016.
 */
public class ImageHelper {


    private static final int REQUIRED_SIZE_WIDTH = 1200;

    private static final int REQUIRED_SIZE_HEIGHT = 900;



    public static Bitmap compressBitmap(String path) throws IOException {
            File file = new File(path);

            Bitmap bitmap = decodeUri(path);
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            stream.flush();
            stream.close();
            return bitmap;
    }

    public static Bitmap scaleDown(Bitmap realImage) {

//        Matrix m = new Matrix();
//        m.setRectToRect(new RectF(0, 0, realImage.getWidth(), realImage.getHeight()), new RectF(0, 0, REQUIRED_SIZE_WIDTH, REQUIRED_SIZE_HEIGHT), Matrix.ScaleToFit.CENTER);
//        return Bitmap.createBitmap(realImage, 0, 0, realImage.getWidth(), realImage.getHeight(), m, true);
        int width = realImage.getWidth();
        int height = realImage.getHeight();
        int excessPart = realImage.getWidth()/4;
        Bitmap croppedBitmap =
                Bitmap.createBitmap(realImage, excessPart/2, 0, realImage.getWidth() - excessPart, realImage.getHeight());

        float scaleWidth = ((float) REQUIRED_SIZE_WIDTH) / croppedBitmap.getWidth();
        float scaleHeight = ((float) REQUIRED_SIZE_HEIGHT) / croppedBitmap.getHeight();
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                croppedBitmap, 0, 0, croppedBitmap.getWidth(), croppedBitmap.getHeight(), matrix, false);
        realImage.recycle();
        return resizedBitmap;
    }



//    public static Bitmap scaleDown(Bitmap realImage) {
//        float ratio = Math.min(
//                (float) REQUIRED_SIZE_WIDTH / realImage.getWidth(),
//                (float) REQUIRED_SIZE_WIDTH / realImage.getHeight());
//        if(realImage.getWidth()>1200&&realImage.getHeight()>900){
//            return Bitmap.createScaledBitmap(realImage, 1200,
//                    900, true);
//        }
//
//
//        return realImage;
//    }


    public static Bitmap decodeUri(String selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImage, o);


        // The new size we want to scale to


        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE_WIDTH
                    || height_tmp / 2 < REQUIRED_SIZE_WIDTH) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeFile(selectedImage, o2);

    }
    public static Bitmap decodeFile(InputStream in){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > -1 ) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
            InputStream is2 = new ByteArrayInputStream(baos.toByteArray());

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is1,null,o);

            final int IMAGE_MAX_SIZE=960;

            System.out.println("h:" + o.outHeight + " w:" + o.outWidth);
            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return  BitmapFactory.decodeStream(is2, null, o2);

        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
        }

        ca.close();
        return null;

    }
}
