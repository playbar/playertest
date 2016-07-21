package com.rednovo.ace.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rednovo.ace.R;
import com.rednovo.libs.common.FileUtils;
import com.rednovo.libs.common.ImageCompress;
import com.rednovo.libs.common.ImageUtils;
import com.rednovo.libs.common.ScreenUtils;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StatusBarUtils;
import com.rednovo.libs.common.StorageUtils;
import com.rednovo.libs.ui.base.AppManager;
import com.rednovo.libs.ui.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 照片裁剪Activity
 */
public class PhotoEditActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "PhotoEditActivity";
    private static final int TAKE_PIC_CODE = 1;
    private static final int GET_PIC_CODE = 2;
    public static final String TAKE_PIC = "TAKE_PIC";
    public static final String GET_PIC = "GET_PIC";
    public static final String OPTION_TAG = "OPTION_TAG";
    /**
     * 图片高宽比例,float,默认为1.0f
     */
    public static final String H_W_SCALE = "H_W_SCALE";
    private float HwScale = 1.0f;
    ImageView mImageView;
    View squareView;
    LinearLayout photo_edit_bottomBar;
    View view_vv01;
    View view_vv02;
    RelativeLayout rel_relyout;

    private Bitmap mBitmap = null;
    private String mPhotoPath;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mTopOffset = 0;
    private int mLeftOffset = 0;
    private int mClipWidth;
    private int mClipHeight;
    private final int mScaleImageWidth = 500;
    private final int mScaleImageHeight = 500;
    private String tag = "";
    //private String tempIcon = getCameraFilePath() + File.separator + System.currentTimeMillis() + ".ace";
    private String tempIcon = StorageUtils.getCachePicPath() + File.separator + System.currentTimeMillis() + ".ace";
    private boolean isGetDisplayCard;
    private Bitmap bim;
    private String picPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        initView();

        mImageView.setOnTouchListener(new ImageTouchListener());
        HwScale = getIntent().getFloatExtra(H_W_SCALE, 1.0f);
        mClipWidth = ScreenUtils.getScreenWidth(this);
        mClipHeight = Math.round(mClipWidth * HwScale);
        isGetDisplayCard = getIntent().getBooleanExtra("get_display_card", false);
        if (isGetDisplayCard) {
            // squareView.setLayoutParams(new LinearLayout.LayoutParams(mClipWidth, LinearLayout.LayoutParams.MATCH_PARENT));
            // RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            // params.addRule(RelativeLayout.CENTER_IN_PARENT);
            // mImageView.setLayoutParams(params);
            view_vv01.setBackgroundColor(Color.TRANSPARENT);
            view_vv02.setBackgroundColor(Color.TRANSPARENT);
            rel_relyout.setBackgroundColor(Color.parseColor("#99000000"));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            squareView.setBackgroundColor(Color.TRANSPARENT);
            mImageView.setAdjustViewBounds(true);
            mImageView.setScaleType(ScaleType.FIT_XY);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            mImageView.setLayoutParams(params);
        } else {
            squareView.setLayoutParams(new LinearLayout.LayoutParams(mClipWidth, mClipHeight));
        }
        tag = getIntent().getStringExtra(OPTION_TAG);
        if (!TextUtils.isEmpty(tag) && tag.equals(GET_PIC)) {
            dispatchGetPictureIntent(GET_PIC_CODE);
        } else if (!TextUtils.isEmpty(tag) && tag.equals(TAKE_PIC)) {
            dispatchTakePictureIntent(TAKE_PIC_CODE);
        }
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setTranslucentImmersionBar(this);
    }

    private void initView() {
        mImageView = (ImageView) findViewById(R.id.photo_edit_Image);
        squareView = findViewById(R.id.photo_edit_square);
        photo_edit_bottomBar = (LinearLayout) findViewById(R.id.photo_edit_bottomBar);
        view_vv01 = findViewById(R.id.view_vv01);
        view_vv02 = findViewById(R.id.view_vv02);
        rel_relyout = (RelativeLayout) findViewById(R.id.rel_relyout);
        findViewById(R.id.photo_edit_submit_btn).setOnClickListener(this);
        findViewById(R.id.photo_edit_cancle_btn).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        super.onDestroy();
    }

    private class ImageTouchListener implements OnTouchListener {
        private Matrix matrix = new Matrix();
        private Matrix savedMatrix = new Matrix();
        private PointF startPoint = new PointF();
        private PointF midPoint = new PointF();

        private static final int STATE_NONE = 0;
        private static final int STATE_DRAG = 1;
        private static final int STATE_ZOOM = 2;

        private int currentMode;
        private float startDistance;
        private float[] mMatrixValue = new float[9];
        private float[] mTranslateLimit = new float[2];

        public ImageTouchListener() {
            currentMode = STATE_NONE;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    matrix.set(mImageView.getImageMatrix());
                    savedMatrix.set(matrix);
                    matrix.getValues(mMatrixValue);

                    float scaleFactor = mMatrixValue[0];
                    mTranslateLimit[0] = mClipWidth - mBitmapWidth * scaleFactor;
                    if (mTranslateLimit[0] > 0) {
                        mTranslateLimit[0] = 0;
                    }
                    mTranslateLimit[1] = mClipHeight + mTopOffset - mBitmapHeight * scaleFactor;
                    startPoint.set(event.getX(), event.getY());
                    currentMode = STATE_DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    startDistance = spacing(event);
                    if (startDistance > 10f) {
                        savedMatrix.set(matrix);
                        getMidPoint(midPoint, event);
                        currentMode = STATE_ZOOM;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (currentMode == STATE_DRAG) {
                        matrix.set(savedMatrix);
                        matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        matrix.getValues(mMatrixValue);
                        float fx = mMatrixValue[2];
                        float fy = mMatrixValue[5];
                        boolean overroll = false;
                        if (fx > 0) {
                            fx = 0;
                            overroll = true;
                        } else if (fx < mTranslateLimit[0]) {
                            fx = mTranslateLimit[0];
                            overroll = true;
                        }

                        if (fy > mTopOffset) {
                            fy = mTopOffset;
                            overroll = true;
                        } else if (fy < mTranslateLimit[1]) {
                            fy = mTranslateLimit[1];
                            overroll = true;
                        }
                        if (overroll) {
                            matrix.setScale(mMatrixValue[0], mMatrixValue[0]);
                            matrix.postTranslate(fx, fy);
                        }

                    } else if (currentMode == STATE_ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = newDist / startDistance;
                            matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        }
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    Matrix testMatrix = mImageView.getImageMatrix();
                    float[] values = new float[9];
                    testMatrix.getValues(values);
                    float scale = values[0];

                    if (scale < 1.0) {
                        matrix.setScale(1.0f, 1.0f);
                        matrix.postTranslate(0, mTopOffset);
                    } else {
                        float fx = values[2];
                        float fy = values[5];
                        boolean overroll = false;

                        mTranslateLimit[0] = mClipWidth - mBitmapWidth * scale;
                        if (mTranslateLimit[0] > 0) {
                            mTranslateLimit[0] = 0;
                        }
                        mTranslateLimit[1] = mClipHeight + mTopOffset - mBitmapHeight * scale;

                        if (fx > 0) {
                            fx = 0;
                            overroll = true;
                        } else if (fx < mTranslateLimit[0]) {
                            fx = mTranslateLimit[0];
                            overroll = true;
                        }

                        if (fy > mTopOffset) {
                            fy = mTopOffset;
                            overroll = true;
                        } else if (fy < mTranslateLimit[1]) {
                            fy = mTranslateLimit[1];
                            overroll = true;
                        }

                        if (overroll) {
                            matrix.setScale(values[0], values[0]);
                            matrix.postTranslate(fx, fy);
                        }
                    }
                    currentMode = STATE_NONE;
                    break;
                case MotionEvent.ACTION_UP:
                    currentMode = STATE_NONE;
                    break;
                default:
                    break;
            }
            mImageView.setImageMatrix(matrix);
            return true;
        }

        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }

        private void getMidPoint(PointF point, MotionEvent event) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo_edit_submit_btn:
                new SaveBitmapAsyncTask().execute();
                break;
            case R.id.photo_edit_cancle_btn:
                finish();
                break;
        }
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File picFile = new File(getCameraFilePath(), "temp.jpg");
            picFile.createNewFile();
            mPhotoPath = picFile.getPath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        startActivityForResult(intent, actionCode);
        return;
    }

    private void dispatchGetPictureIntent(int actionCode) {
        mPhotoPath = null;
        if (Build.VERSION.SDK_INT >= 19) {
            startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), actionCode);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, actionCode);
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == GET_PIC_CODE && data != null && data.getData() != null) {
                mPhotoPath = FileUtils.getPath(this, data.getData());
                String fileSuffix = FileUtils.getFileExtension(mPhotoPath);
                if (mPhotoPath == null || fileSuffix.contains("gif")) {
                    ShowUtils.showToast("不支持的文件格式");
                    finish();
                    return;
                }
                new LoadBitmapAsyncTask(mClipWidth, mClipHeight).execute(mPhotoPath);
            } else if (requestCode == TAKE_PIC_CODE) {
                if (mPhotoPath == null) {
                    ShowUtils.showToast("不支持的文件格式");
                    finish();
                    return;
                }
                new LoadBitmapAsyncTask(mClipWidth, mClipHeight).execute(mPhotoPath);
            }
        } else {
            finish();
        }
    }

    private String getCameraFilePath() {
        File rootPath = Environment.getExternalStorageDirectory();
        File filePath = new File(rootPath, "/imageClip");
        if (!filePath.exists()) {
            filePath.mkdir();
        }
        return filePath.getAbsolutePath();
    }

    private class LoadBitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ProgressDialog cmProgressDialog = null;
        private float cmSideWidthLimit;
        private float cmSideHeightLimit;

        public LoadBitmapAsyncTask(int viewWidth, int viewHeight) {
            cmSideWidthLimit = viewWidth;
            cmSideHeightLimit = viewHeight;
        }

        @Override
        protected void onPreExecute() {
            cmProgressDialog = new ProgressDialog(PhotoEditActivity.this);
            cmProgressDialog.setMessage("Loading");
            cmProgressDialog.setCancelable(false);
            cmProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            picPath = params[0];
//            String filePath = params[0];
            if (picPath == null) {
                return null;
            }

            Options opt = new Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picPath, opt);

            int sampleFctor = 1;
            if (opt.outWidth > 480 || opt.outHeight > 800) {
                int sampleW = opt.outWidth / 480;
                int sampleH = opt.outHeight / 800;
                sampleFctor = sampleW < sampleH ? sampleW : sampleH;
            }
            Bitmap bitmap = ImageUtils.decodeBitmap(picPath, sampleFctor);
            if (bitmap == null) {
                return null;
            }

            int bmpWidth = bitmap.getWidth();
            int bmpHeight = bitmap.getHeight();
            int rotateDegree = ImageUtils.readPictureDegree(picPath);
            Matrix matrix = new Matrix();
            if (rotateDegree != 0 || rotateDegree != 360) {
                matrix.postRotate(rotateDegree);
                bmpWidth = bitmap.getHeight();
                bmpHeight = bitmap.getWidth();
            }
            float scale = 1.0f;
            // TODO
            if (bmpWidth * HwScale <= bmpHeight) {
                scale = cmSideWidthLimit / bmpWidth;
            } else {
                scale = cmSideHeightLimit / bmpHeight;
            }

            matrix.postScale(scale, scale);

            Bitmap processedBitmap = null;
            try {
                processedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (processedBitmap != bitmap) {
                    bitmap.recycle();
                    bitmap = null;
                }
            } catch (OutOfMemoryError e) {

            }
            return processedBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                ShowUtils.showToast("您选择的图片不符合要求。");
                BaseActivity currentActivity = AppManager.getAppManager().currentActivity();
                if(currentActivity != null && currentActivity.getClass().equals(PhotoEditActivity.class.getClass()))
                    cmProgressDialog.dismiss();
                finish();
                return;
            }
            mBitmap = result;
            mBitmapWidth = result.getWidth();
            mBitmapHeight = result.getHeight();
            mImageView.setImageBitmap(result);
            View squareView = findViewById(R.id.photo_edit_square);
            mTopOffset = squareView.getTop();
            if (mBitmapWidth >= mBitmapHeight) {
                Matrix matrix = mImageView.getImageMatrix();
                matrix.postTranslate(0, mTopOffset);
                mImageView.setImageMatrix(matrix);
            }
            BaseActivity currentActivity = AppManager.getAppManager().currentActivity();
            if(currentActivity != null && PhotoEditActivity.class.equals(currentActivity.getClass()))
                cmProgressDialog.dismiss();

            if (isGetDisplayCard) {
                if (mBitmapHeight < squareView.getHeight()) { // 图片高度小于截图框
                    squareView.setLayoutParams(new LinearLayout.LayoutParams(mClipWidth, mBitmapHeight));
                    mClipHeight = mBitmapHeight;
                } else {
                    mClipHeight = squareView.getHeight();
                }

                mTopOffset = squareView.getTop();
            }

        }


    }

    private class SaveBitmapAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog cmProgressDialog = null;
        private Matrix matrix;

        @Override
        protected void onPreExecute() {
            cmProgressDialog = new ProgressDialog(PhotoEditActivity.this);
            cmProgressDialog.setMessage("Saving...");
            cmProgressDialog.show();
            matrix = mImageView.getImageMatrix();
        }

        @Override
        protected Void doInBackground(Void... params) {
//			Matrix matrix = mImageView.getImageMatrix();
            float[] values = new float[9];
            matrix.getValues(values);
            float scaleX = values[0];
            float transX = values[2];
            float scaleY = values[4];
            float transY = values[5];

            int x = (int) ((mLeftOffset - transX) / scaleX);
            int y = (int) ((mTopOffset - transY) / scaleY);
            int width = (int) (mClipWidth / scaleX);
            int height = (int) (mClipHeight / scaleY);
            if (x + width > mBitmapWidth) {
                width = mBitmapWidth - x;
            }
            if ((y + height) > mBitmap.getHeight()) {
                Log.w(TAG, "y + height must be <=mBitmap.getHeight() ");
                height = mBitmap.getHeight() - y;
            }
            if(x < 0){
                x = 0;
            }
            if(y < 0){
                y = 0;
            }
            Bitmap cropBitmap = Bitmap.createBitmap(mBitmap, x, y, width, height);
            Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, mScaleImageWidth, mScaleImageHeight, true);
            try {
                int quality = 90;

                File originalFile = new File(tempIcon);
                if (!originalFile.getParentFile().exists()) {
                    originalFile.createNewFile();
                }
                File file = new File(picPath);
                if(file.exists() && file.length() > 500*1024){
                    FileOutputStream fileOutputStream = new FileOutputStream(originalFile);
                    scaleBitmap.compress(CompressFormat.JPEG, quality, fileOutputStream);
                    File jpegTrueFile = new File(tempIcon);
                    ImageCompress.compressBitmap(scaleBitmap, quality, jpegTrueFile.getAbsolutePath(), true);
                }else{
                    FileOutputStream outStream = new FileOutputStream(originalFile);
                    scaleBitmap.compress(CompressFormat.JPEG, 100, outStream);
                    outStream.close();
                }
            } catch (IOException e) {
            }
            cropBitmap.recycle();
            scaleBitmap.recycle();
            cropBitmap = null;
            scaleBitmap = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            BaseActivity currentActivity = AppManager.getAppManager().currentActivity();
            if(currentActivity != null && PhotoEditActivity.class.equals(currentActivity.getClass()))
                cmProgressDialog.dismiss();
            Intent data = new Intent();
            data.putExtra("image", tempIcon);
            PhotoEditActivity.this.setResult(RESULT_OK, data);
            finish();
        }

    }

}
