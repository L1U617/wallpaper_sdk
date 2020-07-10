package cs.hku.wallpaper_sdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cs.hku.wallpaper_sdk.service.WallPaperOrientationService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        /*
        Button btn = findViewById(R.id.back_to_home);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });
        WallPaperOrientationService.StartOrientationListener(this);

         */
        //GridView是通过map格式处理数据的
        GridView show_gridview = (GridView) findViewById(R.id.grid);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, get_data(), R.layout.grid_item,
                new String[]{"img", "name"}, new int[]{R.id.img, R.id.img_name});
        show_gridview.setAdapter(simpleAdapter);

    }

    private ArrayList<Map<String, Object>> get_data(){
        ArrayList<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < 10; i++){
            Map<String, Object> data_map = new HashMap<String, Object>();
            data_map.put("name", "Pic" + i);
            data_map.put("img", R.drawable.wall02);
            data_list.add(data_map);
        }
        return data_list;
    }
}
