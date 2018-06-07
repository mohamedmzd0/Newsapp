package com.example.android.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ItemListActivity extends AppCompatActivity {

    private SearchView mSearchView;
    private RecyclerView recyclerView;
    boolean loading = false;
    private int visibleThreshold = 5;
    String query = "programming";
    SimpleItemRecyclerViewAdapter adapter;
    List<Result> model = new ArrayList<>();
    int num_of_result = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        mSearchView = findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query1) {
                query = query1;
                getNews();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        recyclerView = findViewById(R.id.item_list);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager
                        .findLastVisibleItemPosition();

                if (!loading
                        && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    num_of_result += 5;
                    getNews();
                    loading = true;
                }

            }
        });

        getNews();
    }

    private void getNews() {
        ApiServices apiServices = ApiClient.getApi().create(ApiServices.class);
        Call<Model> call = apiServices.getNews("search?q=" + query + "&show-tags=contributor&show-fields=thumbnail&api-key=bbca5e03-9e10-4908-9690-c007c98b91d6&page-size=" + num_of_result);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if (response.code() == 200) {
                    if (model.size() == 0) {
                        model = response.body().getResponse().getResults();
                        adapter = new SimpleItemRecyclerViewAdapter(model);
                        recyclerView.setAdapter(adapter);
                    } else {
                        if (response.body().getResponse().getResults() != null)
                            model.addAll(model.size(), response.body().getResponse().getResults().subList(model.size() - 1, response.body().getResponse().getResults().size() - 1));
                        adapter.notifyDataSetChanged();
                    }
                    loading = false;
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                Toast.makeText(ItemListActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Result> mValues;

        SimpleItemRecyclerViewAdapter(
                List<Result> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mIdView.setText(mValues.get(position).getSectionName());
            holder.mContentView.setText(mValues.get(position).getWebTitle());
            holder.mDate.setText(mValues.get(position).getWebPublicationDate());
            if (mValues.get(position).getTags() != null && (mValues.get(position).getTags().size() > 0)) {
                holder.mAuthor.setText(mValues.get(position).getTags().get(0).getWebTitle());
                Glide.with(holder.itemView.getContext()).load(mValues.get(position).getTags().get(0).getBylineImageUrl()).into(holder.mImageView);
            }
            holder.itemView.setTag(mValues.get(position).getFields());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra("webtitle", mValues.get(position).getWebUrl());
                    context.startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            if (mValues == null)
                return 0;
            else
                return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final TextView mDate;
            final TextView mAuthor;
            final ImageView mImageView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                mDate = (TextView) view.findViewById(R.id.date);
                mAuthor = (TextView) view.findViewById(R.id.author);
                mImageView = view.findViewById(R.id.iv);
            }
        }
    }
}
