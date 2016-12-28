package testxunfeitts.example.com.ttsapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText mEditText = null;
    private Button readButton = null;
    private Button saveButton = null;
    private CheckBox mCheckBox = null;
    private TextToSpeech mTextToSpeech=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText)this.findViewById(R.id.edittext);
        readButton = (Button)this.findViewById(R.id.rbutton);
        saveButton = (Button)this.findViewById(R.id.sbutton);
        mCheckBox = (CheckBox)this.findViewById(R.id.checkbox);

        //实例并初始化TTS对象
        mTextToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS) {
                    //设置朗读语言
                    int supported=mTextToSpeech.setLanguage(Locale.US);
                    if ((supported!=TextToSpeech.LANG_AVAILABLE)&&(supported!=TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
                        Toast.makeText(MainActivity.this, "不支持当前语言！", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        //朗读监听按钮
        readButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //朗读EditText里的内容
                mTextToSpeech.speak(mEditText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        //保存按钮监听
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //将EditText里的内容保存为语音文件
                int r = mTextToSpeech.synthesizeToFile(mEditText.getText().toString(), null, "/mnt/sdcard/speak.wav");
                if (r==TextToSpeech.SUCCESS) {
                    Toast.makeText(MainActivity.this, "保存成功！", 1).show();
                }
            }
        });

        //EditText内容变化监听
        mEditText.addTextChangedListener(mTextWatcher);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTextToSpeech!=null) {
            mTextToSpeech.shutdown();//关闭TTS
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher(){

        @Override
        public void afterTextChanged(Editable s) {
            //如果是边写边读
            if(mCheckBox.isChecked()&&(s.length()!=0)){
                //获得EditText的所有内容
                String t = s.toString();
                mTextToSpeech.speak(t.substring(s.length()-1), TextToSpeech.QUEUE_FLUSH, null);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int before,
                                      int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub

        }
    };
}
