package ru.comtrans.camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,Camera.AutoFocusCallback {
    private SurfaceHolder mHolder;
    private Camera camera;
    private int screenWidth, screenHeight;
    private Camera.Size optimalPreviewSize;
    private Paint paint;
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

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.d("TAG","optimal size "+optimalSize.width+" "+optimalSize.height);
        return optimalSize;
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
            for (int i = 0; i < sizes.size(); i++) {
                Camera.Size size = sizes.get(i);
                Log.d("TAG","size "+size.width+" "+size.height);

                if(!isVideo&&size.width<=1920&&size.height<=1440){
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setPictureSize(size.width,size.height);
                    camera.setParameters(parameters);
                    break;
                }else if(isVideo&&size.width<=640&&size.height<=480){
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setPictureSize(size.width,size.height);
                    camera.setParameters(parameters);
                    break;
                }

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

    void setCameraDisplayOrientation(int cameraId) {
        // определяем насколько повернут экран от нормального положения
        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int rotation =windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;

        // получаем инфо по камере cameraId
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        // задняя камера
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = ((360 - degrees) + info.orientation);
        } else
            // передняя камера
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = ((360 - degrees) - info.orientation);
                result += 360;
            }
        result = result % 360;
        if(camera !=null)
            try{
                camera.setDisplayOrientation(result);
            }catch (Exception ignored){}

    }

    @Override
    protected void onDraw(Canvas canvas)
    {

            //  Find Screen size first
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            int screenWidth = metrics.widthPixels+100;
            int screenHeight = metrics.heightPixels;

            //  Set paint options
            paint.setAntiAlias(true);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.argb(255, 255, 255, 255));

            canvas.drawLine((screenWidth/3)*2,0,(screenWidth/3)*2,screenHeight,paint);
            canvas.drawLine((screenWidth/3),0,(screenWidth/3),screenHeight,paint);
            canvas.drawLine(0,(screenHeight/3)*2,screenWidth,(screenHeight/3)*2,paint);
            canvas.drawLine(0,(screenHeight/3),screenWidth,(screenHeight/3),paint);

    }


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
