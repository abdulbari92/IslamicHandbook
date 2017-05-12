package faizan.com.islamichandbook.hadith;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import faizan.com.islamichandbook.PdfViewer.PdfViewerActivity;
import faizan.com.islamichandbook.R;
import faizan.com.islamichandbook.model.Hadith;
import faizan.com.islamichandbook.utilities.FileDownloader;

/**
 * Created by buste on 4/24/2017.
 */

public class HadithRecyclerViewAdapter extends RecyclerView.Adapter<HadithRecyclerViewAdapter.HadithHolder>{

    private ArrayList<Hadith> mHadiths;

    public HadithRecyclerViewAdapter(ArrayList<Hadith> hadiths) {
        mHadiths = hadiths;
    }

    public static class HadithHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mBookImage;
        private TextView mBookName;
        private Hadith mHadith;

        public HadithHolder(View v) {
            super(v);
            mBookImage = (ImageView) v.findViewById(R.id.imageViewHadithImage);
            mBookName = (TextView) v.findViewById(R.id.textViewHadithName);
            v.setOnClickListener(this);
        }

        public void bindHadith(Hadith hadith) {
            mHadith = hadith;
            Glide.with(mBookImage.getContext()).load(hadith.getBookImage()).into(mBookImage);
            mBookName.setText(hadith.getBookName());
        }

        @Override
        public void onClick(View v) {
            Log.i("Abdul",mHadith.getBookFileName());
            final File file = new File(Environment.getExternalStorageDirectory(), "hadith/"+mHadith.getBookFileName());

            if(!file.exists()) {
                new HadithRecyclerViewAdapter.HadithHolder.DownloadFile().execute(mHadith.getBookAddress(), mHadith.getBookFileName());
            }else{
                Intent intent = new Intent(v.getContext(), PdfViewerActivity.class);
                intent.putExtra("path","hadith/"+mHadith.getBookFileName());
                intent.putExtra("name",mHadith.getBookName());
                v.getContext().startActivity(intent);
            }
        }

        private class DownloadFile extends AsyncTask<String, Void, Void> {

            @Override
            protected Void doInBackground(String... strings) {
                String fileUrl = strings[0];   // -> URL
                String fileName = strings[1];  // -> FILENAME
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File folder = new File(extStorageDirectory, "hadith");
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
                intent.putExtra("path","hadith/"+mHadith.getBookFileName());
                intent.putExtra("name",mHadith.getBookName());
                itemView.getContext().startActivity(intent);
            }
        }

    }
    @Override
    public HadithRecyclerViewAdapter.HadithHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_hadith, parent, false);
        return new HadithHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(HadithRecyclerViewAdapter.HadithHolder holder, int position) {
        Hadith itemHadith = mHadiths.get(position);
        holder.bindHadith(itemHadith);
    }

    @Override
    public int getItemCount() {
        return mHadiths.size();
    }
}
