package qiming.guo.ironman;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button full_btn = (Button) findViewById(R.id.fullscreen);
        final Button list_btn = (Button) findViewById(R.id.search);
        final Button search_btn = (Button) findViewById(R.id.ysearch);

        full_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent;
                intent = new Intent(MainActivity.this,FullscreenDemoActivity.class);
//                intent.setComponent(new ComponentName(getPackageName(), "FullscreenDemoActivity"));
                startActivity(intent);

            }
        });

        list_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent;
                intent = new Intent(MainActivity.this,VideoListDemoActivity.class);
//                intent.setComponent(new ComponentName(getPackageName(), "FullscreenDemoActivity"));
                startActivity(intent);

            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent;
                intent = new Intent(MainActivity.this,SearchActivity.class);
//                intent.setComponent(new ComponentName(getPackageName(), "FullscreenDemoActivity"));
                startActivity(intent);

            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
