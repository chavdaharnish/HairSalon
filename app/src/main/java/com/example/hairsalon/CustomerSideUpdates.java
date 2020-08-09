package com.example.hairsalon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomerSideUpdates extends RecyclerView.Adapter<CustomerSideUpdates.ImageViewHolder>   {
    public static Context mContext,pContext;
    private List<Upload> mUploads;

    public static OnItemClickListener mListener;
    public static RadioButton radioButton;
    public static RecyclerView mRecyclerView;
    public static int position;
    public static int radio;
    public static Upload uploadCurrent;

    public CustomerSideUpdates(Context context,Context pcontext ,List<Upload> uploads) {
        mContext = context;
        pContext=pcontext;
        mUploads = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).from ( pContext ).inflate(R.layout.image_layout, parent, false);
        //parent.setOnClickListener(mOnClickListener);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getmName ());
        holder.textViewPrice.setText(uploadCurrent.getPrice());

        Picasso.get()
                .load(uploadCurrent.getmImageUrl (  ))
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }



    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView textViewName,textViewPrice;
        public ImageView imageView;


        public ImageViewHolder(final View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            imageView = itemView.findViewById(R.id.image_view_upload);


            itemView.setOnClickListener(this);

            // Intent intent = new Intent(view.getContext() , HairStyle.class);
            //intent.putExtra("name" ,uploadCurrent.getmName());
            //Toast.makeText(view.getContext() ,uploadCurrent.getmName() , Toast.LENGTH_LONG ).show();
            //mContext.startActivity(intent);

            //   }
            //});


        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                position = getAdapterPosition();


                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }

            }

        }






    }

    public interface OnItemClickListener {
        void onItemClick(int position);


    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;


    }


}



