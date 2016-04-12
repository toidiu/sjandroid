package sandjentrance.com.sj.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import sandjentrance.com.sj.R;

/**
 * Created by toidiu on 2/25/16.
 */
public class ArtistImageCropActivity extends BaseActivity {

    public static final int IMAGE_RESOLUTION = 600;
    public static final int RESULT_CODE = 48662;
    public static final String PATH_EXTRA = "PATH_EXTRA";
    public static final String RESULT_FILE_PATH = "FILE_PATH";
    //region Field----------------------
    //~=~=~=~=~=~=~=~=~=~=~=~=Constants
//    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
//    private static final MediaType MEDIA_TYPE_BMP = MediaType.parse("image/bmp; charset=utf-8");
//    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg; charset=utf-8");
//    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png; charset=utf-8");
//    private static final MediaType MEDIA_TYPE_BINARY = MediaType.parse("application/octet-stream; charset=utf-8");
    //~=~=~=~=~=~=~=~=~=~=~=~=Views
    @Bind(R.id.crop)
    View cropBtn;
    @Bind(R.id.back)
    View back;
    @Bind(R.id.crop_image)
    CropImageView cropImageView;
    @Bind(R.id.progress)
    View progress;
    //~=~=~=~=~=~=~=~=~=~=~=~=Fields
    private File avatarFile;
    //endregion

    //region Lifecycle----------------------
    public static Intent getInstance(Context context, String path) {
        Intent intent = new Intent(context, ArtistImageCropActivity.class);
        intent.putExtra(PATH_EXTRA, path);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_img_crop);
        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void endAndSetImage() {
        Intent intent = new Intent().putExtra(RESULT_FILE_PATH, avatarFile.getAbsolutePath());
        setResult(RESULT_OK, intent);
        finish();
    }
    //endregion

    //region Network--------------------------
//    private void putImageS3(String id) {
//        Observable<File> observable = Observable.create(s -> {
//            avatarFile = ImageUtil.getAvatarFile(this);
//
//            //resize image
//            Bitmap croppedImage = cropImageView.getCroppedImage();
//            croppedImage = ImageUtil.getResizedBitmap(croppedImage, IMAGE_RESOLUTION, IMAGE_RESOLUTION);
//
//            //save image to file
//            try {
//                FileOutputStream fOut = new FileOutputStream(avatarFile);
//                croppedImage.compress(Bitmap.CompressFormat.PNG, 60, fOut);
//                fOut.flush();
//                fOut.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            s.onNext(avatarFile);
//
//        });
//
//        observable.observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(new Subscriber<File>() {
//
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        progress.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onNext(File res) {
//                        new QueryArtistSetImgUrl(ArtistImageCropActivity.this).query();
//                    }
//
//                });
//    }

    //endregion

    //region Init----------------------
    private void init() {
        String path = getIntent().getStringExtra(PATH_EXTRA);


        if (path.startsWith("content")) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(path));
                cropImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            cropImageView.setImageBitmap(bitmap);
        }

        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                //// FIXME: 4/12/16 set image in drive and locally
//            putImageS3(prefs.getUserId());
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
    //endregion

}
