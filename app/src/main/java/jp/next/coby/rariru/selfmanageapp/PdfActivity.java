package jp.next.coby.rariru.selfmanageapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.joanzapata.pdfview.PDFView;

public class PdfActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        setTitle("窓口取り扱い時間");

        final PDFView pdfView = (PDFView)findViewById(R.id.pdfview);
        pdfView.fromAsset("窓口取り扱い時間.pdf").load();
    }
}
