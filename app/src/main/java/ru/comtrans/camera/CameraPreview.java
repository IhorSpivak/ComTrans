package ru.comtrans.camera;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera camera;
    int screenWidth, screenHeight;
    Camera.Size optimalPreviewSize;


    public CameraPreview(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context,attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
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
}
