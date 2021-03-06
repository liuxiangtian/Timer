package com.lxt.xiang.timer.adaptor;


import android.support.v4.util.ArrayMap;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.model.Artist;
import com.lxt.xiang.timer.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArtistAdaptor extends RecyclerView.Adapter<ArtistAdaptor.VH> {

    private List<Artist> mArtists;
    private ArrayMap<Long, Palette.Swatch> mCache = new ArrayMap<>();
    private OnItemClickListener mOnItemClickListener;

    public ArtistAdaptor(List<Artist> items) {
        if(items == null) items = new ArrayList<>();
        this.mArtists = items;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_standant_grid, parent, false);
        ButterKnife.bind(this, view);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        final Artist artist = mArtists.get(position);
        holder.title.setText(artist.getArtist());
        holder.content.setText(artist.getAlbumNum()+" albums | "+artist.getTrackNum()+" tracks");
        Palette.Swatch swatch = mCache.get(artist.getId());
        if(swatch!=null){
            BitmapUtil.loadBitmapByArtistId(holder.albumArt, artist.getId());
            updateHolder(holder, artist, swatch);
        } else {
            BitmapUtil.loadBitmapByArtistId(holder.albumArt, artist.getId(), new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch newSwatch = palette.getVibrantSwatch();
                    if(newSwatch!=null){
                        updateHolder(holder, artist, newSwatch);
                        mCache.put(artist.getId(), newSwatch);
                    }
                }
            });
        }
    }

    private void updateHolder(VH holder, Artist artist, Palette.Swatch swatch) {
        holder.title.setTextColor(swatch.getTitleTextColor());
        holder.content.setTextColor(swatch.getTitleTextColor());
        holder.footerView.setBackgroundColor(swatch.getRgb());
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    public void replaceData(List<Artist> items) {
        if (items == null) return;
        this.mArtists = items;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Artist item, int position, Pair<View, String> pair);
    }

    public class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.album_art)
        ImageView albumArt;
        @Bind(R.id.footer)
        LinearLayout footerView;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.content) TextView content;

        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Artist item = mArtists.get(position);
            Pair<View,String> pair = Pair.create((View)albumArt, "transition_album_art");
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(item, position, pair);
            }
        }
    }
}
