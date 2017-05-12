package faizan.com.islamichandbook.quran;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
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
import faizan.com.islamichandbook.model.Surah;
import faizan.com.islamichandbook.utilities.VolleySingleton;


public class QuranFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private QuranRecyclerViewAdapter mAdapter;
    private ArrayList<Surah> mSurahList;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quran, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewQuran);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSurahList = new ArrayList<>();
        getSurahList();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new QuranRecyclerViewAdapter(mSurahList);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    private void getSurahList() {
        requestQueue = VolleySingleton.getInstance().getmRequestQueue();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        String url = "http://rjtmobile.com/aamir/know-ai/MobileApp/book_type.php?&book_type=quran";
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Books");
                            for(int i = 0; i < jsonArray.length(); ++i){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Surah surah = new Surah();
                                surah.setSurahId(jsonObject.getString("BookId")+": ");
                                if(jsonObject.getString("BookId").compareTo("1")==0){
                                    surah.setSurahName("Al-Baqarah");
                                }else if (jsonObject.getString("BookId").compareTo("2")==0){
                                    surah.setSurahName("Al-Imran");
                                }else if (jsonObject.getString("BookId").compareTo("0")==0){
                                    surah.setSurahName("Al-Fatiha");
                                }else{
                                    surah.setSurahName("Unknown");
                                }
                                surah.setSurahAddress(jsonObject.getString("BookFile"));
                                surah.setSuranFileName(surah.getSurahAddress().substring(surah.getSurahAddress().lastIndexOf('/')+1
                                        ,surah.getSurahAddress().length()));
                                mSurahList.add(surah);
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
