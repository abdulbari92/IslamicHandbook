package faizan.com.islamichandbook.hadith;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
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
import faizan.com.islamichandbook.model.Hadith;
import faizan.com.islamichandbook.utilities.VolleySingleton;


public class HadithFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private HadithRecyclerViewAdapter mAdapter;
    ArrayList<Hadith> mHadithsList;

    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hadith, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewHadith);
        mGridLayoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mHadithsList = new ArrayList<>();
        getHadithsList();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mGridLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new HadithRecyclerViewAdapter(mHadithsList);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void getHadithsList() {

        requestQueue = VolleySingleton.getInstance().getmRequestQueue();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        String url = "http://rjtmobile.com/aamir/know-ai/MobileApp/book_type.php?&book_type=hadith";
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Books");
                            for(int i = 0; i < jsonArray.length(); ++i){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Hadith hadith = new Hadith();
                                hadith.setBookId(jsonObject.getString("BookId")+": ");
                                hadith.setBookDescription(jsonObject.getString("BookDesc"));
                                hadith.setBookImage(jsonObject.getString("BookThumb"));
                                hadith.setBookAddress(jsonObject.getString("BookFile"));
                                hadith.setBookFileName(hadith.getBookAddress().substring(hadith.getBookAddress().lastIndexOf('/')+1
                                        ,hadith.getBookAddress().length()));
                                hadith.setBookName(hadith.getBookAddress().substring(hadith.getBookAddress().lastIndexOf('/')+1
                                        ,hadith.getBookAddress().length()));
                                mHadithsList.add(hadith);
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
