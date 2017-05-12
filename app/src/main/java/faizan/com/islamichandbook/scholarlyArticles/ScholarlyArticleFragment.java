package faizan.com.islamichandbook.scholarlyArticles;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import faizan.com.islamichandbook.R;
import faizan.com.islamichandbook.model.ScholarlyArticle;
import faizan.com.islamichandbook.utilities.VolleySingleton;


public class ScholarlyArticleFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mLinearLayoutManager;
    private ScholarlyArticleRecyclerViewAdapter mAdapter;
    ArrayList<ScholarlyArticle> mScholarlyArticleList;

    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scholarly_article, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewScholarlyArticles);
        mLinearLayoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mScholarlyArticleList = new ArrayList<>();
        getScholarlyArticles();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new ScholarlyArticleRecyclerViewAdapter(mScholarlyArticleList);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    private void getScholarlyArticles() {
        requestQueue = VolleySingleton.getInstance().getmRequestQueue();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        String url = "http://rjtmobile.com/aamir/know-ai/MobileApp/book_type.php?&book_type=scholar";
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Books");
                            for(int i = 0; i < jsonArray.length(); ++i){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ScholarlyArticle scholarlyArticle = new ScholarlyArticle();
                                scholarlyArticle.setBookId(jsonObject.getString("BookId")+": ");
                                scholarlyArticle.setBookName(jsonObject.getString("BookName"));
                                scholarlyArticle.setBookDescription(jsonObject.getString("BookDesc"));
                                scholarlyArticle.setBookImage(jsonObject.getString("BookThumb"));
                                scholarlyArticle.setBookAddress(jsonObject.getString("BookFile"));
                                scholarlyArticle.setBookFileName(scholarlyArticle.getBookAddress().substring(scholarlyArticle.getBookAddress().lastIndexOf('/')+1
                                        ,scholarlyArticle.getBookAddress().length()));
                                mScholarlyArticleList.add(scholarlyArticle);
                            }
                            mAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Abdul", String.valueOf(error));
                        progressDialog.dismiss();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
