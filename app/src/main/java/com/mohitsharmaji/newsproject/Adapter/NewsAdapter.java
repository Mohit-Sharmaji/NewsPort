package com.mohitsharmaji.newsproject.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mohitsharmaji.newsproject.Models.Article;
import com.mohitsharmaji.newsproject.R;
import com.mohitsharmaji.newsproject.Utils;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.mViewHolder> {
    List<Article> articleList;
    Context context;
    // 2. Create a variable of Interface
    private OnItemClickListener listener;

    // 1. Created an Interface to handle onItemClickListener
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // 3. Create a method to setOnItemClick listener to instantiate listener
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.listener = onItemClickListener;
    }

    public NewsAdapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new mViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holders, int position) {
        final mViewHolder holder = holders;
        Article article = articleList.get(position);

        RequestOptions requestOptions = new RequestOptions();
        Glide.with(context)
                .load(article.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

        holder.title.setText(article.getTitle());
        holder.desc.setText(article.getDescription());
        holder.source.setText(article.getSource().getName());
        holder.time.setText(" \u2022 " + Utils.DateToTimeFormat(article.getPublishedAt()));
        holder.published_ad.setText(Utils.DateFormat(article.getPublishedAt()));
        holder.author.setText(article.getAuthor());

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    // 4. Implement View.OnClickListener in ViewHolder Class
    public static class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, desc, author, published_ad, source, time;
        ImageView imageView;
        ProgressBar progressBar;
        // 5. Again create a variable for interface
        OnItemClickListener onItemClickListener;

        // 6.Get the interface instance via constructor parameter
        public mViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            author = itemView.findViewById(R.id.author);
            published_ad = itemView.findViewById(R.id.publishedAt);
            source = itemView.findViewById(R.id.source);
            time = itemView.findViewById(R.id.time);
            imageView = itemView.findViewById(R.id.img);
            progressBar = itemView.findViewById(R.id.prograss_load_photo);

            // 7. Assign the value to instance
            itemView.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            // 8. Send the View & Item Position to the Actual Interface here so that it can be used in the MainActivity to send intent.
            onItemClickListener.onItemClick(view,  getAdapterPosition());
        }
    }
}

