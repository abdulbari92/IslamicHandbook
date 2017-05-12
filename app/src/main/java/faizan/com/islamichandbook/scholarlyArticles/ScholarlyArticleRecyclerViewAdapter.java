package faizan.com.islamichandbook.scholarlyArticles;

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
import faizan.com.islamichandbook.model.ScholarlyArticle;
import faizan.com.islamichandbook.utilities.FileDownloader;

/**
 * Created by buste on 4/24/2017.
 */

public class ScholarlyArticleRecyclerViewAdapter extends RecyclerView.Adapter<ScholarlyArticleRecyclerViewAdapter.ArticleHolder> {

    private ArrayList<ScholarlyArticle> mScholarlyArticles;

    public ScholarlyArticleRecyclerViewAdapter(ArrayList<ScholarlyArticle> scholarlyArticles) {
        mScholarlyArticles = scholarlyArticles;
    }

    public static class ArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mBookImage;
        private TextView mBookDescription;
        private ScholarlyArticle mScholarlyArticle;

        public ArticleHolder(View v) {
            super(v);
            mBookImage = (ImageView) v.findViewById(R.id.imageViewArticleImage);
            mBookDescription = (TextView) v.findViewById(R.id.textViewArticleDescription);
            v.setOnClickListener(this);
        }

        public void bindArticle(ScholarlyArticle scholarlyArticle) {
            mScholarlyArticle = scholarlyArticle;
            Glide.with(mBookImage.getContext()).load(scholarlyArticle.getBookImage()).into(mBookImage);
            mBookDescription.setText(scholarlyArticle.getBookDescription());
        }

        @Override
        public void onClick(View v) {
            Log.i("Abdul",mScholarlyArticle.getBookFileName());
            final File file = new File(Environment.getExternalStorageDirectory(), "article/"+mScholarlyArticle.getBookFileName());
            if(!file.exists()) {
                new ScholarlyArticleRecyclerViewAdapter.ArticleHolder.DownloadFile().execute(mScholarlyArticle.getBookAddress(), mScholarlyArticle.getBookFileName());
            }else{
                Intent intent = new Intent(v.getContext(), PdfViewerActivity.class);
                intent.putExtra("path","article/"+mScholarlyArticle.getBookFileName());
                intent.putExtra("name",mScholarlyArticle.getBookName());
                v.getContext().startActivity(intent);
            }
        }

        private class DownloadFile extends AsyncTask<String, Void, Void> {

            @Override
            protected Void doInBackground(String... strings) {
                String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
                String fileName = strings[1];  // -> maven.pdf
                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                File folder = new File(extStorageDirectory, "article");
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
                intent.putExtra("path","article/"+mScholarlyArticle.getBookFileName());
                intent.putExtra("name",mScholarlyArticle.getBookName());
                itemView.getContext().startActivity(intent);
            }
        }
    }

    @Override
    public ScholarlyArticleRecyclerViewAdapter.ArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_scholarly_article, parent, false);
        return new ArticleHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ScholarlyArticleRecyclerViewAdapter.ArticleHolder holder, int position) {
        ScholarlyArticle itemScholarlyArticle = mScholarlyArticles.get(position);
        holder.bindArticle(itemScholarlyArticle);
    }

    @Override
    public int getItemCount() {
        return mScholarlyArticles.size();
    }
}
