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
import com.lxt.xiang.timer.model.Album;
import com.lxt.xiang.timer.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AlbumAdaptor extends RecyclerView.Adapter<AlbumAdaptor.VH> {

    private List<Album> mAlbums;
    private ArrayMap<Long, Palette.Swatch> mCache = new ArrayMap<>();
    private OnItemClickListener mOnItemClickListener;

    public AlbumAdaptor(List<Album> items) {
        if(items == null) items = new ArrayList<>();
        this.mAlbums = items;
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
        final Album album = mAlbums.get(position);
        Palette.Swatch swatch = mCache.get(album.getId());
        if(swatch!=null){
            BitmapUtil.loadBitmap(holder.albumArt, album.getId());
            updateItemHolder(holder, album, swatch);
        } else {
            BitmapUtil.loadBitmap(holder.albumArt, album.getId(), new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch newSwatch = palette.getVibrantSwatch();
                    if(newSwatch!=null){
                        updateItemHolder(holder, album, newSwatch);
                        mCache.put(album.getId(), newSwatch);
                    } else {
                        updateItemHolder(holder, album);
                    }
                }
            });
        }
    }

    private void updateItemHolder(VH holder, Album album) {
        holder.title.setText(album.getAlbum());
        holder.content.setText(album.getArtist()+" | "+album.getTrackNum()+" tracks");
    }

    private void updateItemHolder(VH holder, Album album, Palette.Swatch swatch) {
        holder.title.setTextColor(swatch.getTitleTextColor());
        holder.content.setTextColor(swatch.getTitleTextColor());
        holder.footerView.setBackgroundColor(swatch.getRgb());
        holder.title.setText(album.getAlbum());
        holder.content.setText(album.getArtist()+" | "+album.getTrackNum()+" tracks");
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public void replaceData(List<Album> items) {
        if (items == null) return;
        this.mAlbums = items;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Album item, int position, Pair<View, String> pair);
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
            Album item = mAlbums.get(position);
            Pair<View,String> pair = Pair.create((View)albumArt, "transition_album_art");
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(item, position, pair);
            }
        }
    }

}
