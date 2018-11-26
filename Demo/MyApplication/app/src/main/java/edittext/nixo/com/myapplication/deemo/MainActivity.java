package edittext.nixo.com.myapplication.deemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import edittext.nixo.com.myapplication.NixoEditText;
import edittext.nixo.com.myapplication.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NixoEditText editText = findViewById(R.id.nixo_edit);
        final TextView test = findViewById(R.id.tv_test_text);
        editText.setOnClearListener(new NixoEditText.OnClearListener() {
            @Override
            public void onClear(String str) {
                test.setText(str);
            }
        });
    }
}
