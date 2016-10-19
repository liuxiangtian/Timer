package com.lxt.xiang.timer.adaptor;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.BitmapUtil;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrackAdaptor extends RecyclerView.Adapter<TrackAdaptor.VH> {

    private List<Track> mTracks;
    private OnItemClickListener mOnItemClickListener;
    private boolean isPlaying = false;
    private int currentPosition = -1;
    private int textColor = -1;

    public TrackAdaptor(List<Track> items) {
        if(items == null) items = new ArrayList<>();
        this.mTracks = items;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_standant_list, parent, false);
        ButterKnife.bind(this, view);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        final Track track = mTracks.get(position);
        if(textColor!=-1){
            holder.trackTitle.setTextColor(textColor);
            holder.trackArtist.setTextColor(textColor);
        }
        holder.trackTitle.setText(track.getTitle());
        holder.trackArtist.setText(track.getArtist());
        BitmapUtil.loadBitmap(holder.albumArt, track.getAlbumId());
        if(position==currentPosition){
            holder.icon.setVisibility(View.VISIBLE);
            if(isPlaying){
                holder.icon.setIcon(MaterialDrawableBuilder.IconValue.PAUSE);
            } else {
                holder.icon.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
            }
        } else {
            holder.icon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    public void replaceData(List<Track> items) {
        if (items == null) return;
        this.mTracks = items;
        notifyDataSetChanged();
    }

    public void notifyItem(int position, boolean playing) {
        isPlaying = playing;
        int oldPosition = currentPosition;
        currentPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(currentPosition);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setTextColor(int color) {
        textColor = color;
        notifyDataSetChanged();
    }

    public int refreshTrack(Track track) {
        if (track == null) {
            return 0;
        }
        int position = mTracks.indexOf(track);
        if(position!=-1){
            int oldPosition = currentPosition;
            currentPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(currentPosition);
            return position;
        }
        return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(Track item, int position, long[] ids);
    }

    public long[] getIds(){
        if(mTracks==null || mTracks.size()==0) return null;
        long[] ids = new long[mTracks.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = mTracks.get(i).getId();
        }
        return ids;
    }

    public class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.album_art)
        ImageView albumArt;
        @Bind(R.id.track_title)
        TextView trackTitle;
        @Bind(R.id.track_artist)
        TextView trackArtist;
        @Bind(R.id.icon)
        MaterialIconView icon;

        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Track item = mTracks.get(position);
            long[] ids = getIds();
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(item, position, ids);
            }
        }
    }
}
