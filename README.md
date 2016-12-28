# AndroidXunfeiTTS

Android系统从1.6版本开始就支持TTS（Text-To-Speech）,即语音合成。但是android系统默认的TTS引擎:Pic TTS不支持中文。所以我们得安装自己的TTS引擎和语音包。

 

在项目中，一开始用的是手说TTS，免费版的，感觉声音很不僵硬，不自然，不连贯。付费版的还要钱激活，如果不激活，限制每次只能度5个字，而且比免费版也好不了多少。最后采用了讯飞语音TTS1.0，发音连贯自然，个人感觉很不错了，以后就用它。

介绍下使用方法：

1.首先下载讯飞的语音包apk，以及语音引擎apk，安装在手机上。

　　链接：http://pan.baidu.com/s/1mgL7elU  提取码：oelh

2.进入系统设置-->语音输入输出设置-->勾选“讯飞语音合成" , 默认引擎“讯飞语音合成”, 语言“中文"。

        

调用android自带的TTS api，就可实现中文版语音合成。
复制代码

package com.example.testxunfeitts;

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

public class MainActivity extends Activity {
    
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
                        Toast.makeText(MainActivity.this, "不支持当前语言！", 1).show();
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

复制代码

中文语音合成（TTS）可以分为两类：

1.跟系统接口吻合的

跟系统接口吻合的，都是把TTS引擎跟语音包数据分开。像系统默认的Pico TTS,SVOX,科大讯飞等就是这样的。

优点：可以通过系统提供的接口去使用TTS功能，以便于做多国语言的扩展。

缺点：设置语音朗读的角色和设置语速的快慢就得通过系统的接口去设置。

2.不与系统接口吻合的

不与系统接口吻合的，都是会把引擎和语音包打包成一个apk，然后安装完之后，通过指定的接口去调用其所提供的中文语音合成功能。

除了这两类方法之外，还有一些是直接把TTS功能做成共享库so文件，然后通过NDK去调用TTS的功能。这一种就比较好，因为不需要额外的再安装TTS引擎跟语音包，并且可以直接在应用里面设置朗读人是男声还是女声，语速等。想旧版的科大讯飞就是以这种形式出现的，比较有代表的应用是“听说”或者"vBook"。

 

分析下市面上有名的中文语音合成TTS各自的差异：

一、跟系统接口吻合的。

1.讯飞语音TTS1.0

这个原来讯飞是没有这个与系统接口吻合的TTS的，原来是以动态库的形式的，最近才以这种apk的形式华丽登场，合成的效果清晰流畅，不生硬，英文也读得挺好的，听起来很舒服。

2.SVOX

这个也做得很强大，N多的语言支持，中文不仅支持普通话，还支持广东话！不过有一些感觉吐字不是很清晰，听感不是很好，不过还好，因为大部分是可以听得懂的，下载地址：http://www.coolapk.com/apk-4192-com.svox.classic.langpack.cmn_chn_fem/

3.三星TTS

支持韩文，中文跟英文，每一字吐字还比较清晰，但是连起来的时候，不是很顺畅。

安装apk，然后把SMT文件夹整个拷贝到SD卡的根目录

下载地址是：http://115.com/file/e7z2iliv

二、是以独立的apk形式存在的主要在下面的两种

1.捷通华声

捷通华声也是中文语音做得比较好的，跟科大讯飞有得一拼。它的调用方法是使用Java的反射机制来使用已经安装的TTS类。

捷通华声TTS语音包下载地址：http://www.yingyong.so/app/3/1917.htm

2.手说TTS

手说也是独立安装的一个TTS引擎，目前好像只支持中文语言。它接口公开，语音质量还行。本人做过手说TTS的，感觉声音很不僵硬，不自然，连贯。

它的使用方法在例程里面有很详细的介绍，手说的主页：http://shoushuo.com/index.html

 

对于第一种作为切合系统接口的TTS中文引擎，安装之后，需要安装语言包，然后还得简单的设置一下才可以使用，方法如下：

设置－》语音输入与输出－》文字转语音设置，把对应的TTS引擎后面的勾，勾上，再在“默认引擎”里面设置你所需要的TTS，然后就可以聆听示例了。

而对于第二种，一般都会带有一个activity可以聆听示例的。

 

推荐使用科大讯飞的中文TTS，跟捷通华声的音色都比较自然，应该是目前所有的中文TTS里面最好的了。
