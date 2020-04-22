package com.fbf.a2r;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.util.ArrayList;


public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.MyViewHolder> {
    private static ArrayList<PostDataSet> mDataset;
    private static ArrayList<String> keys;
    private static Context mContext;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView CardView_Title, CardView_ConTents, TextView_ViewCount, TextView_PostProfileName;
        public SimpleDraweeView ImageView_CardView_Image;
        public SimpleDraweeView CircleImageView_PostProfileImage;
        public MyViewHolder(View v) {
            super(v);
            CardView_Title = v.findViewById(R.id.TextView_CardView_Title);
            CardView_ConTents = v.findViewById(R.id.TextView_CardView_Contents);
            ImageView_CardView_Image = v.findViewById(R.id.ImageView_CardView_Image);
            TextView_ViewCount = v.findViewById(R.id.TextView_ViewCount);
            CircleImageView_PostProfileImage = v.findViewById(R.id.CircleImageView_PostProfileImage);

            TextView_PostProfileName = v.findViewById(R.id.TextView_PostProfileName);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Intent post = new Intent(mContext, ShowpostActivity.class);
                        post.putExtra("Title", mDataset.get(pos).getPostTitle());
                        post.putExtra("Contents", mDataset.get(pos).getPostExplanation());
                        post.putExtra("Author", mDataset.get(pos).getPostEditor());
                        post.putExtra("CommentOption", mDataset.get(pos).getCommentOption());
                        post.putExtra("Image", mDataset.get(pos).getDownloadURL());
                        post.putExtra("Uid", mDataset.get(pos).getUid());
                        post.putExtra("ViewCount", mDataset.get(pos).getPostViewCount());
                        post.putExtra("Key", keys.get(pos));
                        post.putExtra("ImageName", mDataset.get(pos).getImageName());
                        mContext.startActivity(post);
                    }
                }
            });
        }
    }
    public CardViewAdapter(){}
    // Provide a suitable constructor (depends on the kind of dataset)
    public CardViewAdapter(ArrayList<PostDataSet> myDataset, ArrayList<String> mykeys,Context context) {
        Fresco.initialize(context);
        mDataset = myDataset;
        mContext = context;
        keys = mykeys;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_contents, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }
    void updateViewSize(SimpleDraweeView view, @Nullable ImageInfo imageInfo) {
        if (imageInfo != null) {
            view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if(mDataset.get(position).getDownloadURL() != null){
            holder.ImageView_CardView_Image.setVisibility(View.VISIBLE);
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(mDataset.get(position).getDownloadURL()))
                    .build();
            holder.ImageView_CardView_Image.setController(Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.ImageView_CardView_Image.getController())
                    .setLowResImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .setControllerListener(new BaseControllerListener() {
                        @Override
                        public void onIntermediateImageSet(String id, @Nullable Object imageInfo) {
                            super.onIntermediateImageSet(id, imageInfo);
                            updateViewSize(holder.ImageView_CardView_Image, (ImageInfo) imageInfo);
                        }
                        @Override
                        public void onFinalImageSet(String id, @Nullable Object imageInfo, @Nullable Animatable animatable) {
                            super.onFinalImageSet(id, imageInfo, animatable);
                            updateViewSize(holder.ImageView_CardView_Image, (ImageInfo) imageInfo);
                        }
                    }).build());
        }
        else {
            holder.ImageView_CardView_Image.setVisibility(View.GONE);
        }
        holder.CircleImageView_PostProfileImage.setImageURI(Uri.parse(mDataset.get(position).getFirebaseUser()));
        holder.TextView_PostProfileName.setText(mDataset.get(position).getPostEditor());
        holder.CardView_Title.setText(mDataset.get(position).getPostTitle());
        holder.CardView_ConTents.setText(mDataset.get(position).getPostExplanation());
        holder.TextView_ViewCount.setText(mDataset.get(position).getPostViewCount());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

}

