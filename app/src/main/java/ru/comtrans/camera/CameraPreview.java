package ru.comtrans.camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,Camera.AutoFocusCallback {
    private SurfaceHolder mHolder;
    private Camera camera;
    private int screenWidth, screenHeight;
    private Camera.Size optimalPreviewSize;
    private Paint paint;
    private Point size = new Point();
    private DisplayMetrics realMetrics = new DisplayMetrics();
    private boolean isVideo;

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public CameraPreview(Context context) {
        super(context);
        init();
    }



    public CameraPreview(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    private void init(){
        paint = new Paint();
        setWillNotDraw(false);
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }








    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("TAG","surface created");

//        try {
//            // create the surface and start camera preview
//            if (camera != null) {
//                camera.setPreviewDisplay(holder);
//                camera.startPreview();
//            }
//        } catch (IOException e) {
//            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
//        }
    }

    public void refreshCamera(Camera camera) {
        Log.d("TAG","refresh camera");
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            this.camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
     //   setCameraDisplayOrientation(0);
        try {
            setCamera(camera);

            setScreenSize();
            this.camera.setPreviewDisplay(mHolder);
            this.camera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }





    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d("TAG","surface changed");

        refreshCamera(camera);

    }

    public void setScreenSize() {

        // boolean widthIsMax = w > h;

        // определяем размеры превью камеры
        Log.d("TAG","view w "+screenWidth+" view h "+screenHeight);


        try{



            RectF rectDisplay = new RectF();
            RectF rectPreview = new RectF();

            // RectF экрана, соотвествует размерам экрана
            rectDisplay.set(0, 0, screenWidth,screenHeight);

            // RectF первью

            // превью в горизонтальной ориентации
            rectPreview.set(0, 0, optimalPreviewSize.width, optimalPreviewSize.height);


            Matrix matrix = new Matrix();

            // если экран будет "втиснут" в превью (третий вариант из урока)
            matrix.setRectToRect(rectDisplay, rectPreview,
                    Matrix.ScaleToFit.START);
            matrix.invert(matrix);

            // преобразование
            matrix.mapRect(rectPreview);

            // установка размеров surface из получившегося преобразования
            Log.d("TAG","rect bottom "+rectPreview.bottom+" rect right "+rectPreview.right);
            // mPreview.setLayoutParams(new FrameLayout.LayoutParams((int)rectPreview.bottom,(int)rectPreview.right));
            getLayoutParams().height = (int) (rectPreview.bottom);
            getLayoutParams().width = (int) (rectPreview.right);
            mHolder.setFixedSize((int)rectPreview.right,(int)rectPreview.bottom);


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void setCamera(Camera camera) {
        this.camera = camera;
        try {
            optimalPreviewSize = //getOptimalPreviewSize(camera.getParameters().getSupportedPreviewSizes(),screenWidth,screenHeight);
            camera.getParameters().getPreviewSize();

            List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
            Collections.sort(sizes, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size o1, Camera.Size o2) {
                    if(o1.width>o2.width&&o1.height>o2.height){
                        return -1;
                    }else if(o1.width<o2.width&&o1.height<o2.height){
                        return 1;
                    }else {
                        return 0;
                    }
                }
            });
            for (int i = 0; i < sizes.size(); i++) {
                Camera.Size size = sizes.get(i);
                Log.d("TAG","size "+size.width+" "+size.height);

                if(size.width<=1920&&size.height<=1080) {
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setPictureSize(size.width, size.height);
                    camera.setParameters(parameters);
                    break;
                }
//                }else if(isVideo&&size.width<=640&&size.height<=480){
//                    Camera.Parameters parameters = camera.getParameters();
//                    parameters.setPictureSize(size.width,size.height);
//                    camera.setParameters(parameters);
//                    break;
//                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public  void setScreenSize(int screenWidth, int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("TAG","surface destroyed");
        camera = null;

    }

//    @Override
//    protected void onDraw(Canvas canvas)
//    {
//        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//
//
//        int realWidth;
//        int realHeight;
//
//        if (Build.VERSION.SDK_INT >= 17){
//            //new pleasant way to get real metrics
//
//            display.getRealMetrics(realMetrics);
//            realWidth = realMetrics.widthPixels;
//            realHeight = realMetrics.heightPixels;
//
//        } else if (Build.VERSION.SDK_INT >= 14) {
//            //reflection for this weird in-between time
//            try {
//                Method mGetRawH = Display.class.getMethod("getRawHeight");
//                Method mGetRawW = Display.class.getMethod("getRawWidth");
//                realWidth = (Integer) mGetRawW.invoke(display);
//                realHeight = (Integer) mGetRawH.invoke(display);
//            } catch (Exception e) {
//                //this may not be 100% accurate, but it's all we've got
//                realWidth = display.getWidth();
//                realHeight = display.getHeight();
//                Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
//            }
//
//        } else {
//            //This should be close, as lower API devices should not have window navigation bars
//            realWidth = display.getWidth();
//            realHeight = display.getHeight();
//        }
//
//
//        Log.d("TAG","screen height "+screenHeight);
//
//            //  Set paint options
//            paint.setAntiAlias(true);
//            paint.setStrokeWidth(3);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setColor(Color.argb(255, 255, 255, 255));
//
//            canvas.drawLine((realWidth/3)*2,0,(realWidth/3)*2,realHeight+300,paint);
//            canvas.drawLine((realWidth/3),0,(realWidth/3),realHeight+300,paint);
//            canvas.drawLine(0,(realHeight/3)*2,realWidth+300,(realHeight/3)*2,paint);
//            canvas.drawLine(0,(realHeight/3),realWidth+300,(realHeight/3),paint);
//
//    }


    public void focusOnTouch(MotionEvent event) {
        try {
            if (camera != null) {
                camera.cancelAutoFocus();

                Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);

                Camera.Parameters parameters = camera.getParameters();
                if (!Objects.equals(parameters.getFocusMode(), Camera.Parameters.FOCUS_MODE_MACRO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                }
                if (parameters.getMaxNumFocusAreas() > 0) {
                    List<Camera.Area> mylist = new ArrayList<>();
                    mylist.add(new Camera.Area(focusRect, 1000));
                    parameters.setFocusAreas(mylist);
                }

                if (parameters.getMaxNumMeteringAreas() > 0){ // check that metering areas are supported
                    List<Camera.Area> meteringAreas = new ArrayList<>();
                    meteringAreas.add(new Camera.Area(focusRect,1000));
                    parameters.setMeteringAreas(meteringAreas);
                }



                try {
                    camera.cancelAutoFocus();
                    camera.startPreview();
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (camera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_MACRO)) {
                                Camera.Parameters parameters = camera.getParameters();
                                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                if (parameters.getMaxNumFocusAreas() > 0) {
                                    parameters.setFocusAreas(null);
                                }
                                camera.setParameters(parameters);
                                camera.startPreview();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG","e",e);
                }
            }
        }catch (Exception e){
            Log.e("TAG","e",e);
        }

    }


    private Rect calculateTapArea(float x, float y, float coefficient) {
        Matrix matrix = new Matrix();
        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,90,getResources().getDisplayMetrics());
        int areaSize = Float.valueOf(dp * coefficient).intValue();
        int left = clamp((int) x - areaSize / 2, 0, getLayoutParams().width - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, getLayoutParams().height - areaSize);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        matrix.mapRect(rectF);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }


    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }
}
