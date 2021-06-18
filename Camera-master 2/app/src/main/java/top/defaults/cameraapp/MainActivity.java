package top.defaults.cameraapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.defaults.cameraapp.options.Commons;
import top.defaults.cameraapp.options.ResultActivity;

public class MainActivity extends AppCompatActivity {

    private View prepareToRecord;

    @BindView(R.id.media_dir) TextView mediaDir;
    @BindView(R.id.gallery) GridView gallery;
    private List<File> mediaFiles = new ArrayList<>();
    private MediaFileAdapter adapter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        RxPermissions rxPermissions = new RxPermissions(this);
        prepareToRecord = findViewById(R.id.open_camera);
        RxView.clicks(prepareToRecord)
                .compose(rxPermissions.ensure(Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe(granted -> {
                    if (granted) {
                        startVideoRecordActivity();
                    } else {
                        Snackbar.make(prepareToRecord, getString(R.string.no_enough_permission), Snackbar.LENGTH_SHORT).setAction("Confirm", null).show();
                    }
                });

        mediaDir.setText(String.format("Media files are saved under:\n%s", Commons.MEDIA_DIR));

        adapter = new MediaFileAdapter(this, mediaFiles);
        gallery.setAdapter(adapter);
        gallery.setOnItemClickListener((parent, view, position, id) -> {
            File file = adapter.getItem(position);
            playOrViewMedia(file);
        });

        Button maps_button=findViewById(R.id.open_maps);
        maps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=petrol pump");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        Button maps_button2=findViewById(R.id.open_maps2);
        maps_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=mechanic");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        Button gallery_button=findViewById(R.id.open_gallery);
        gallery_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, PICK_IMAGE);


            }
        });
        Button login_button=findViewById(R.id.login_first);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(in);
            }
        });


    }

    private void startVideoRecordActivity() {
        Intent intent = new Intent(this, PhotographerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        File file = new File(Commons.MEDIA_DIR);
        if (file.isDirectory()) {
            mediaFiles.clear();
            File[] files = file.listFiles();
            Arrays.sort(files, (f1, f2) -> {
                if (f1.lastModified() - f2.lastModified() == 0) {
                    return 0;
                } else {
                    return f1.lastModified() - f2.lastModified() > 0 ? -1 : 1;
                }
            });
            mediaFiles.addAll(Arrays.asList(files));
            adapter.notifyDataSetChanged();
        }
    }

    private void playOrViewMedia(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uriForFile = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", file);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uriForFile = Uri.fromFile(file);
        }
        intent.setDataAndType(uriForFile, isVideo(file) ? "video/mp4" : "image/jpg");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "No media viewer found", Toast.LENGTH_SHORT).show();
        }


    }

    private class MediaFileAdapter extends BaseAdapter {

        private List<File> files;

        private Context context;

        MediaFileAdapter(Context c, List<File> files) {
            context = c;
            this.files = files;
        }

        public int getCount() {
            return files.size();
        }

        public File getItem(int position) {
            return files.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_media, parent, false);
            }
            imageView = convertView.findViewById(R.id.item_image);
            View indicator = convertView.findViewById(R.id.item_indicator);
            File file = getItem(position);
            if (isVideo(file)) {
                indicator.setVisibility(View.VISIBLE);
            } else {
                indicator.setVisibility(View.GONE);
            }
            Glide.with(context).load(file).into(imageView);
            return convertView;
        }
    }

    private boolean isVideo(File file) {
        return file != null && file.getName().endsWith(".mp4");
    }


    public static final int PICK_IMAGE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.e("My Act",picturePath);
//            ImageView imageView = (ImageView) findViewById(R.id.imageView);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            String filepath = picturePath;
            File imagefile = new File(filepath);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imagefile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

//            Bitmap bm = BitmapFactory.decodeStream(fis);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
//            byte[] b = baos.toByteArray();
//            String encImage = Base64.encodeToString(b, Base64.DEFAULT);
//            System.out.println(encImage);
//            Log.e("Answer array is........",encImage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            // Read BitMap by file path
            Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Log.e("Answer array is........"," "+byteArray.length);
            Intent i2=new Intent(MainActivity.this, ResultActivity.class);
            startActivity(i2);

        }


    }




}
