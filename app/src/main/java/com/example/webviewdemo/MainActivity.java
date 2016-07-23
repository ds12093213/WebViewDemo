package com.example.webviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initWebSettings();
        loadUrl();
    }

    /**
     * 加载URL
     */
    private void loadUrl() {
        // 加载网络的url地址
//		mWebView.loadUrl("https://www.baidu.com/");

        // 从assets目录下面的加载html
        mWebView.loadUrl("file:///android_asset/test.html");
    }

    /**
     * 初始化WebView的基本配置
     */
    private void initWebSettings() {
        // 启用WebView对JavaScript的支持
        mWebView.getSettings().setJavaScriptEnabled(true);

        // 设置JavascriptInterface
        // javainterface实际就是一个普通的java类，里面是我们本地实现的java代码
        // 将object 传递给webview，并指定别名，这样js脚本就可以通过我们给的这个别名来调用我们的方法
        // 在代码中，TestInterface是实例化的对象，testInterface是这个对象在js中的别名
        mWebView.addJavascriptInterface(new TestInterface(), "testInterface");
                                                                testInterface

        // 触摸焦点起作用（如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件）
        mWebView.requestFocus();

        // 取消滚动条
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        // 允许网页缩放
        mWebView.getSettings().setSupportZoom(true);

        // 把图片加载放在最后来加载渲染
        mWebView.getSettings().setBlockNetworkImage(true);

        mWebView.setWebViewClient(new WebViewClient() {
            /**
             * 给WebView加一个事件监听对象（WebViewClient)并重写shouldOverrideUrlLoading，
             * 可以对网页中超链接按钮的响应
             * 当按下某个连接时WebViewClient会调用这个方法，并传递参数：当前响应的的url地址
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 此处可添加一些逻辑：是否拦截此url，自行处理
                // 下方2行代码是指在当前的webview中跳转到新的url
                view.loadUrl(url);
                Log.e("WebViewDemo", "shouldOverrideUrlLoading url:" + url);
                return true;
            }

            /**
             * WebView加载url完成时，会回调此api，可在这个api中隐藏加载进度框
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                // 此处可添加一些逻辑：隐藏加载进度框
                Log.e("WebViewDemo", "onPageFinished");
            }

            /**
             * WebView开始加载url时，会回调此api，可在这个api中显示加载进度框
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 此处可添加一些逻辑：显示加载进度框
                Log.e("WebViewDemo", "onPageStarted");
            }
        });
    }

    /**
     * 初始化控件实例
     */
    private void initView() {
        mWebView = (WebView) findViewById(R.id.webview);
        findViewById(R.id.btn_load_js).setOnClickListener(this);
        findViewById(R.id.btn_load_js_args).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load_js: // Java调用JS
                // 无参数调用
                mWebView.loadUrl("javascript:javacalljs()");
                break;
            case R.id.btn_load_js_args: // Java调用JS并传递参数
                String content = "hello js, form Android code!";
                mWebView.loadUrl("javascript:javacalljswithargs('" + content + "')");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否按下返回键，且WebView现在的层级，可以返回
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            // 返回WebView的上一页面
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**iang
     * Js调用的JavascriptInterface
     */
    public class TestInterface {

        /**
         * 因为安全问题，在Android4.2以后(如果应用的android:targetSdkVersion数值为17+)
         * JS只能访问带有 @JavascriptInterface注解的Java函数。
         */
        @JavascriptInterface
        public void startCall() {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + 10086));
            startActivity(intent);
        }

        @JavascriptInterface
        public void showToast(String content) {
            Toast.makeText(MainActivity.this, "js调用了java函数并传递了参数：" + content, Toast.LENGTH_SHORT).show();
        }
    }
}
