package jp.next.coby.rariru.selfmanageapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.joanzapata.pdfview.PDFView;

public class MapActivity extends AppCompatActivity {
    ImageView imageView;
    Button button;
    Button mGoogleButton;

    private static float SCALESIZE = 2.4f;

    @Override
    protected void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_map);

        setTitle("学内Map");

        final PDFView pdfView = (PDFView)findViewById(R.id.pdfview);
        pdfView.fromAsset("学内マップ.pdf").load();

        mGoogleButton = (Button)findViewById(R.id.googleButton);
        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("geo:35.626304,139.33935?q=東京工科大学");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        /*imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.img3);

        button = (Button) findViewById(R.id.button);
        button.setBackgroundColor(Color.rgb(0, 100, 100));
        button.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("cat", "down!");
                    ScaleAnimation(imageView, 1.0f, 1.0f, SCALESIZE, SCALESIZE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("cat", "up!");
                    ScaleAnimation(imageView, SCALESIZE, SCALESIZE, 1.0f, 1.0f);
                }
                return true; // true にしとかないと UP が呼ばれない
            }
        });*/

    }

    private void ScaleAnimation(ImageView v, float src_scaleX, float src_scaleY, float dst_scaleX, float dst_scaleY) {
        ScaleAnimation anime = null;
        anime = new ScaleAnimation(
                src_scaleX, dst_scaleX, // Xスケールサイズ(開始時→終了時)
                src_scaleY, dst_scaleY, // Yスケールサイズ(開始時→終了時)
                ScaleAnimation.RELATIVE_TO_SELF, 0.05f, // X座標のタイプ、X座標(0.5fがview中央値)
                ScaleAnimation.RELATIVE_TO_SELF, 0.8f); // Y座標のタイプ、Y座標(0.5fがview中央値)

        // 0.9秒かけてアニメーションする(ms)
        anime.setDuration(900);

        // アニメーション終了時の表示状態を維持する
        anime.setFillEnabled(true);
        anime.setFillAfter(true);

        // アニメーションを開始
        v.startAnimation(anime);
    }

}
