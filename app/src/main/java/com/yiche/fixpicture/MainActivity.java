package com.yiche.fixpicture;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DragViewLayout dragViewLayout;
    private ImageView imageViewForAdd2;
    private ImageView disPlayImg;
    private Bitmap bitmapForRecycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        FrameLayout.LayoutParams layoutParams1
                = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView imageViewForAdd1 = new ImageView(this);
        imageViewForAdd1.setImageResource(R.mipmap.ic_launcher);
        imageViewForAdd1.setLayoutParams(layoutParams1);
        //控制初始化位置,默认0,0
        PositionEntry positionEntry1 = new PositionEntry();
        positionEntry1.x = 100;
        positionEntry1.y = 100;
        imageViewForAdd1.setTag(positionEntry1);

        imageViewForAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "click1", Toast.LENGTH_SHORT).show();
            }
        });

        FrameLayout.LayoutParams layoutParams2
                = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        imageViewForAdd2 = new ImageView(this);
        imageViewForAdd2.setImageResource(R.mipmap.about_logo);
        imageViewForAdd2.setLayoutParams(layoutParams2);
        PositionEntry positionEntry2 = new PositionEntry();
        positionEntry2.x = 200;
        positionEntry2.y = 200;
        imageViewForAdd2.setTag(positionEntry2);

        imageViewForAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "click2", Toast.LENGTH_SHORT).show();
            }
        });

        dragViewLayout.setAllViewCanDrag(true);
        dragViewLayout.removeAllViews();
        dragViewLayout.addView(imageViewForAdd1);

    }

    private void initView() {
        Button clickAddView = (Button) findViewById(R.id.click_addView);
        Button fixPic = (Button) findViewById(R.id.click_fix);
        clickAddView.setOnClickListener(this);
        fixPic.setOnClickListener(this);
        dragViewLayout = (DragViewLayout) findViewById(R.id.dragLayout);
        disPlayImg = (ImageView) findViewById(R.id.imageView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.click_addView:
                if (dragViewLayout.getChildCount() >= 2) return;
                Log.e("states", dragViewLayout.getStates() + "");
                if (dragViewLayout.getStates() == ViewDragHelper.STATE_IDLE)
                    dragViewLayout.addView(imageViewForAdd2);
                break;
            case R.id.click_fix:
                fixPic();
                break;
        }
    }

    /**
     * 将view映射成图片
     */
    private void fixPic() {
        Log.e("fixPic", "合成图片");
        if (bitmapForRecycle != null && !bitmapForRecycle.isRecycled()) {
            bitmapForRecycle.recycle();
        }
        Bitmap bitmap = Bitmap.createBitmap(dragViewLayout.getWidth(), dragViewLayout.getHeight(), Bitmap.Config.ARGB_8888);
        bitmapForRecycle = bitmap;
        Canvas canvas = new Canvas(bitmap);
        dragViewLayout.draw(canvas);
        disPlayImg.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmapForRecycle != null && !bitmapForRecycle.isRecycled()) {
            bitmapForRecycle.recycle();
        }
    }
}
