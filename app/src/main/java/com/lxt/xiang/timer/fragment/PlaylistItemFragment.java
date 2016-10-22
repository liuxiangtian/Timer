package com.lxt.xiang.timer.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.activity.DetailsActivity;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.BitmapUtil;
import com.lxt.xiang.timer.util.NavUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlaylistItemFragment extends Fragment implements View.OnClickListener {

    private static final String PLAYLIST_ID = "PLAYLIST_ID";
    private static final String PLAYLIST_NAME = "PLAYLIST_NAME";
    private static final String PLAYLIST_COUNT = "PLAYLIST_COUNT";
    private static final String PLAYLIST_POSITION = "PLAYLIST_POSITION";
    @Bind(R.id.album_art)
    ImageView albumArt;
    @Bind(R.id.text_playlist_type)
    TextView typeText;
    @Bind(R.id.text_playlist_index)
    TextView indexText;
    @Bind(R.id.text_playlist_details)
    TextView detailsText;
    private long id;
    private int count;
    private int position;
    private String name;
    private Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
        @Override
        public void onGenerated(Palette palette) {
            int bgColor = palette.getDominantColor(Color.WHITE);
            int textColor = BitmapUtil.getContrastColor(bgColor);
            typeText.setTextColor(textColor);
            indexText.setTextColor(textColor);
            detailsText.setTextColor(textColor);
        }
    };

    public static Fragment newInstance(long id, String name, int num, int position) {
        PlaylistItemFragment fragment = new PlaylistItemFragment();
        Bundle args = new Bundle();
        args.putLong(PLAYLIST_ID, id);
        args.putString(PLAYLIST_NAME, name);
        args.putInt(PLAYLIST_COUNT, num);
        args.putInt(PLAYLIST_COUNT, num);
        args.putInt(PLAYLIST_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getLong(PLAYLIST_ID);
        name = getArguments().getString(PLAYLIST_NAME);
        count = getArguments().getInt(PLAYLIST_COUNT);
        position = getArguments().getInt(PLAYLIST_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist_item, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String type = id < 0 ? "Auto\nPlayist" : "";
        typeText.setText(type);
        String realPos = (position < 10) ? "0" + position : "" + position;
        indexText.setText(realPos);
        String detail = name + "\n    " + count + " tracks";
        detailsText.setText(detail);
        BitmapUtil.loadBitmapByPlaylist(albumArt, id, paletteListener);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(ConstantsUtil.DETAIL_TYPE, ConstantsUtil.DETAIL_TYPE_PLAYLIST);
        intent.putExtra(ConstantsUtil.DETAIL_PLAYLIST_ID, id);
        intent.putExtra(ConstantsUtil.DETAIL_PLAYLIST_NAME, name);
        Pair<View, String> pair = Pair.create((View) albumArt, "transition_album_art");
        NavUtil.navToDetailsActivity(getActivity(), pair, intent);
    }

}
