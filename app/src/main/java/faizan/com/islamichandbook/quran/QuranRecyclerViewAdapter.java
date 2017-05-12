package faizan.com.islamichandbook.quran;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import faizan.com.islamichandbook.PdfViewer.PdfViewerActivity;
import faizan.com.islamichandbook.R;
import faizan.com.islamichandbook.model.Surah;
import faizan.com.islamichandbook.utilities.FileDownloader;

/**
 * Created by buste on 4/23/2017.
 */

public class QuranRecyclerViewAdapter  extends RecyclerView.Adapter<QuranRecyclerViewAdapter.SurahHolder> {
    private ArrayList<Surah> mSurahs;
    public QuranRecyclerViewAdapter(ArrayList<Surah> surahs) {
        mSurahs = surahs;
    }

    public static class SurahHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mSurahId;
        private TextView mSurahName;
        private Surah mSurah;

        public SurahHolder(View v) {
            super(v);
            mSurahId = (TextView) v.findViewById(R.id.textViewSurahNumber);
            mSurahName = (TextView) v.findViewById(R.id.textViewSurahName);
            v.setOnClickListener(this);
        }

        public void bindSurah(Surah surah) {
            mSurah = surah;
            mSurahId.setText(surah.getSurahId());
            mSurahName.setText(surah.getSurahName());
        }

        @Override
        public void onClick(View v) {
            Log.i("Abdul",mSurah.getSuranFileName());
            Context context = itemView.getContext();
            final File file = new File(Environment.getExternalStorageDirectory(), "quran/"+mSurah.getSuranFileName());
            if(!file.exists()) {
                new DownloadFile().execute(mSurah.getSurahAddress(), mSurah.getSuranFileName());
            }else{
                Intent intent = new Intent(v.getContext(), PdfViewerActivity.class);
                intent.putExtra("path","quran/"+mSurah.getSuranFileName());
                intent.putExtra("name",mSurah.getSurahName());
                v.getContext().startActivity(intent);
            }

        }
        private class DownloadFile extends AsyncTask<String, Void, Void> {

            @Override
            protected Void doInBackground(String... strings) {
                String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
                String fileName = strings[1];  // -> maven.pdf
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File folder = new File(extStorageDirectory, "quran");
                folder.mkdir();
                File pdfFile = new File(folder, fileName);
                try{
                    pdfFile.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                FileDownloader.downloadFile(fileUrl, pdfFile);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent(itemView.getContext(), PdfViewerActivity.class);
                intent.putExtra("path","quran/"+mSurah.getSuranFileName());
                intent.putExtra("name",mSurah.getSurahName());
                itemView.getContext().startActivity(intent);
            }
        }
    }

    @Override
    public QuranRecyclerViewAdapter.SurahHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_quran, parent, false);
        return new SurahHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(QuranRecyclerViewAdapter.SurahHolder holder, int position) {
        Surah itemSurah = mSurahs.get(position);
        holder.bindSurah(itemSurah);
    }

    @Override
    public int getItemCount() {
        return mSurahs.size();
    }
}
