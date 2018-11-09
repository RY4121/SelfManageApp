package jp.next.coby.rariru.selfmanageapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class TimeTableActivity extends AppCompatActivity {
    TextView MtextView1,MtextView2,MtextView3,MtextView4,MtextView5;
    TextView TutextView2,TutextView3,TutextView4,TutextView5;
    TextView WtextView1,WtextView2,WtextView3;
    TextView ThtextView3;
    TextView FtextView2;

    WebView webView;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_time_table);
        webView = findViewById(R.id.webView);

        setTitle("時間割");

        MtextView1 = findViewById(R.id.MtextView1);
        MtextView2 = findViewById(R.id.MtextView2);
        MtextView3 = findViewById(R.id.MtextView3);
        MtextView4 = findViewById(R.id.MtextView4);
        MtextView5 = findViewById(R.id.MtextView5);

        TutextView2 = findViewById(R.id.TutextView2);
        TutextView3 = findViewById(R.id.TutextView3);
        TutextView4 = findViewById(R.id.TutextView4);
        TutextView5 = findViewById(R.id.TutextView5);

        WtextView1 = findViewById(R.id.WtextView1);
        WtextView2 = findViewById(R.id.WtextView2);
        WtextView3 = findViewById(R.id.WtextView3);

        ThtextView3 = findViewById(R.id.ThtextView3);

        FtextView2 = findViewById(R.id.FtextView2);

        MtextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"研究棟A302 9:00-10:30",Snackbar.LENGTH_LONG)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               /*webView.setWebViewClient(new WebViewClient(){
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        return false;
                                    }
                                });*/
                                Uri uri = Uri.parse("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9594");
                                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                startActivity(i);
                                //webView.loadUrl("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9594");
                            }
                        }).show();
            }
        });

        MtextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"片柳研究所地下一階 10:45-12:15",Snackbar.LENGTH_LONG)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9415");
                                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                startActivity(i);
                                //webView.loadUrl("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9415");
                            }
                        }).show();
            }
        });

        MtextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"研究棟A403 13:15-14:45",Snackbar.LENGTH_INDEFINITE)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9593");
                                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                startActivity(i);
                                //webView.loadUrl("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9593");
                            }
                        }).show();
            }
        });

        MtextView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"片柳研究所東：201 15:00-16:30",Snackbar.LENGTH_LONG)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("http://fire.cs.priv.teu.ac.jp/~uda/prog2/");
                                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                startActivity(i);
                                //webView.loadUrl("http://fire.cs.priv.teu.ac.jp/~uda/prog2/");
                            }
                        }).show();
            }
        });

        MtextView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"片柳研究所東：201 16:45-18:15",Snackbar.LENGTH_LONG)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("http://fire.cs.priv.teu.ac.jp/~uda/prog2/");
                                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                startActivity(i);
                                webView.loadUrl("http://fire.cs.priv.teu.ac.jp/~uda/prog2/");
                            }
                        }).show();
            }
        });


        TutextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"講義実験棟307 10:45-12:15",Snackbar.LENGTH_LONG)
                        .setAction("翻訳アプリへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*webView.setWebViewClient(new WebViewClient(){
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        return false;
                                    }
                                });
                                webView.loadUrl("https://ejje.weblio.jp/");
                                */
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.setAction("android.intent.category.LAUNCHER");
                                intent.setClassName("com.google.android.apps.translate",
                                        "com.google.android.apps.translate.TranslateActivity");
                                startActivity(intent);
                            }
                        }).show();
            }
        });

        TutextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"研究棟A403 13:15-14:45",Snackbar.LENGTH_LONG)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9415");
                                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                //startActivity(i);
                                //webView.loadUrl("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9594");
                            }
                        }).show();
            }
        });

        TutextView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"片柳研究所中央：1003 15:00-16:30",Snackbar.LENGTH_LONG)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                webView.setWebViewClient(new WebViewClient(){
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        return false;
                                    }
                                });
                                webView.loadUrl("https://daikore.com/report/");
                            }
                        }).show();
            }
        });

        TutextView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"片柳研究所中央：1003 16:45-18:15",Snackbar.LENGTH_LONG)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                webView.setWebViewClient(new WebViewClient(){
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        return false;
                                    }
                                });
                                webView.loadUrl("https://daikore.com/report/");
                            }
                        }).show();
            }
        });


        WtextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"研究棟A402 9:00-10:30",Snackbar.LENGTH_LONG)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("https://service.cloud.teu.ac.jp/lecture/CSF/kinoshi/%E9%80%9A%E4%BF%A1%E3%81%AE%E5%9F%BA%E7%A4%8E/");
                                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                startActivity(i);
                                //webView.loadUrl("https://service.cloud.teu.ac.jp/lecture/CSF/kinoshi/%E9%80%9A%E4%BF%A1%E3%81%AE%E5%9F%BA%E7%A4%8E/");
                            }
                        }).show();
            }
        });

        WtextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"講義実験棟207 10:45-12:15",Snackbar.LENGTH_LONG)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //webView.loadUrl("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9594");
                            }
                        }).show();
            }
        });

        WtextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"研究棟A403 13:15-14:45",Snackbar.LENGTH_LONG)
                        .setAction("講義ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9881");
                                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                startActivity(i);
                                //webView.loadUrl("https://service.cloud.teu.ac.jp/moodle3/course/view.php?id=9881");
                            }
                        }).show();
            }
        });


        ThtextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"講義実験棟301 13:15-14:45",Snackbar.LENGTH_LONG)
                        .setAction("リスニングページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*webView.setWebViewClient(new WebViewClient(){
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        return false;
                                    }
                                });
                                webView.loadUrl("https://text.asahipress.com/free/player/index.html?bookcode=215618");
                                */
                                Uri uri = Uri.parse("https://text.asahipress.com/free/player/index.html?bookcode=215618");
                                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                                startActivity(i);
                            }
                        }).show();
            }
        });

        FtextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"講義棟A202 10:45-12:15",Snackbar.LENGTH_LONG)
                        .setAction("復習ページへ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                webView.setWebViewClient(new WebViewClient(){
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        return false;
                                    }
                                });
                                webView.loadUrl("http://examist.jp/category/mathematics/");
                            }
                        }).show();
            }
        });




    }

}
