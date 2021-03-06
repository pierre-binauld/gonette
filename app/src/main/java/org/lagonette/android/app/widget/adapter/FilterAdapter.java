package org.lagonette.android.app.widget.adapter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.lagonette.android.R;
import org.lagonette.android.content.reader.PartnerReader;
import org.lagonette.android.content.reader.PartnersVisibilityReader;

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnPartnerClickListener {

        void onPartnerClick(@NonNull FilterAdapter.PartnerViewHolder holder);

        void onAllPartnerVisibilityClick(@NonNull FilterAdapter.AllPartnerViewHolder holder);

        void onPartnerVisibilityClick(@NonNull FilterAdapter.PartnerViewHolder holder);

    }

    private static final String TAG = "FilterAdapter";

    private static final int HEADER_ID = -2;

    private static final int FOOTER_ID = -3;

    private static final int LOADING_ID = -4;

    private static final int HEADER_COUNT = 1;

    private static final int FOOTER_COUNT = 1;

    private static final int LOADING_COUNT = 1;

    @Nullable
    private PartnerReader mPartnerReader;

    @Nullable
    private PartnersVisibilityReader mPartnersVisibilityReader;

    @Nullable
    private OnPartnerClickListener mOnPartnerClickListener;

    private boolean mIsExpanded = true;

    public FilterAdapter(@Nullable OnPartnerClickListener onPartnerClickListener/*, @NonNull String search*/) {
        mOnPartnerClickListener = onPartnerClickListener;
    }

    @Override
    public int getItemCount() {
        if (mPartnersVisibilityReader == null) {
            return LOADING_COUNT;
        } else if (mPartnerReader == null) {
            return HEADER_COUNT + LOADING_COUNT;
        } else {
            return HEADER_COUNT + getPartnerCount() + FOOTER_COUNT;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mPartnersVisibilityReader == null) {
            return R.id.view_type_loading;
        } else {
            if (position < HEADER_COUNT) {
                return R.id.view_type_partner_all;
            }
            position -= HEADER_COUNT;

            if (mPartnerReader == null) {
                return R.id.view_type_loading;
            } else {
                if (position < getPartnerCount()) {
                    return R.id.view_type_partner;
                } else {
                    return R.id.view_type_footer;
                }
            }
        }
    }

    @Override
    public long getItemId(int position) {
        if (mPartnersVisibilityReader == null) {
            return LOADING_ID;
        } else {
            if (position < HEADER_COUNT) {
                return HEADER_ID;
            }
            position -= HEADER_COUNT;

            if (mPartnerReader == null) {
                return LOADING_ID;
            } else {
                if (position < getPartnerCount()) {
                    if (mPartnerReader.moveToPosition(position)) {
                        return mPartnerReader.getId();
                    } else {
                        return RecyclerView.NO_ID;
                    }
                } else {
                    return FOOTER_ID;
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.id.view_type_partner_all:
                return onCreateAllPartnerViewHolder(parent);
            case R.id.view_type_partner:
                return onCreatePartnerViewHolder(parent);
            case R.id.view_type_footer:
                return onCreateFooterViewHolder(parent);
            case R.id.view_type_loading:
                return onCreateLoadingViewHolder(parent);
            default:
                throw new IllegalArgumentException("Unknown view type:" + viewType);
        }
    }

    private LoadingViewHolder onCreateLoadingViewHolder(ViewGroup parent) {
        return new LoadingViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.row_loading,
                                parent,
                                false
                        )
        );
    }

    private FooterViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        return new FooterViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.row_footer,
                                parent,
                                false
                        )
        );
    }

    private PartnerViewHolder onCreatePartnerViewHolder(ViewGroup parent) {
        PartnerViewHolder holder = new PartnerViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.row_partner,
                                parent,
                                false
                        )
        );

        if (mOnPartnerClickListener != null) {
            holder.itemView.setTag(holder);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPartnerClickListener.onPartnerClick((PartnerViewHolder) v.getTag());
                }
            });

            holder.visibilityButton.setTag(holder);
            holder.visibilityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPartnerClickListener.onPartnerVisibilityClick((PartnerViewHolder) v.getTag());
                }
            });
        }

        return holder;
    }

    private AllPartnerViewHolder onCreateAllPartnerViewHolder(ViewGroup parent) {
        AllPartnerViewHolder holder = new AllPartnerViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.row_all_partner,
                                parent,
                                false
                        )
        );

        holder.expandButton.setTag(holder);
        holder.expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAllPartnerExpandClick((AllPartnerViewHolder) v.getTag());
            }
        });

        if (mOnPartnerClickListener != null) {
            holder.visibilityButton.setTag(holder);
            holder.visibilityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPartnerClickListener.onAllPartnerVisibilityClick((AllPartnerViewHolder) v.getTag());
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case R.id.view_type_partner_all:
                onBindAllPartnerViewHolder((AllPartnerViewHolder) holder);
                break;
            case R.id.view_type_partner:
                onBindPartnerViewHolder((PartnerViewHolder) holder, position - /*SEARCH_COUNT -*/ HEADER_COUNT);
                break;
            case R.id.view_type_footer:
            case R.id.view_type_loading:
                break;
            default:
                throw new IllegalArgumentException("Unknown view type:" + viewType);
        }
    }

    private void onBindAllPartnerViewHolder(@NonNull AllPartnerViewHolder holder) {
        if (mPartnersVisibilityReader.moveToFirst()) {
            holder.isVisible = mPartnersVisibilityReader.getPartnersVisibilityCount() > 0;
            if (holder.isVisible) {
                holder.visibilityButton.setImageResource(R.drawable.ic_visibility_accent_24dp);
            } else {
                holder.visibilityButton.setImageResource(R.drawable.ic_visibility_off_grey_24dp);
            }

            holder.isExpanded = mIsExpanded;
            if (holder.isExpanded) {
                holder.expandButton.setImageResource(R.drawable.ic_expand_less_grey_24dp);
            } else {
                holder.expandButton.setImageResource(R.drawable.ic_expand_more_grey_24dp);
            }
        }
    }

    private void onBindPartnerViewHolder(@NonNull PartnerViewHolder holder, int position) {
        if (mPartnerReader.moveToPosition(position)) {
            holder.partnerId = mPartnerReader.getId();
            holder.isVisible = mPartnerReader.isVisible();
            holder.nameTextView.setText(mPartnerReader.getName());
            holder.itemView.setClickable(holder.isVisible);
            if (holder.isVisible) {
                holder.visibilityButton.setImageResource(R.drawable.ic_visibility_accent_24dp);
            } else {
                holder.visibilityButton.setImageResource(R.drawable.ic_visibility_off_grey_24dp);
            }
        }
    }

    private void onAllPartnerExpandClick(@NonNull AllPartnerViewHolder holder) {
        mIsExpanded = !mIsExpanded;
        notifyDataSetChanged();
    }

    private int getPartnerCount() {
        if (mPartnerReader != null && mIsExpanded) {
            return mPartnerReader.getCount();
        } else {
            return 0;
        }
    }

    public void setPartnerReader(@Nullable PartnerReader partnerReader) {
        if (mPartnerReader == partnerReader) {
            return;
        }
        mPartnerReader = partnerReader;
        notifyDataSetChanged();
    }

    public void setPartnersVisibilityReader(@Nullable PartnersVisibilityReader partnersVisibilityReader) {
        if (mPartnersVisibilityReader == partnersVisibilityReader) {
            return;
        }
        mPartnersVisibilityReader = partnersVisibilityReader;
        notifyDataSetChanged();
    }

    public class PartnerViewHolder extends RecyclerView.ViewHolder {

        public long partnerId;

        public boolean isVisible;

        public final TextView nameTextView;

        public final ImageButton visibilityButton;

        public PartnerViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.partner_name);
            visibilityButton = (ImageButton) itemView.findViewById(R.id.partner_visibility);
        }
    }

    public class AllPartnerViewHolder extends RecyclerView.ViewHolder {

        public boolean isVisible;

        public boolean isExpanded;

        public final ImageButton visibilityButton;

        public final ImageButton expandButton;

        public AllPartnerViewHolder(View itemView) {
            super(itemView);
            visibilityButton = (ImageButton) itemView.findViewById(R.id.partners_visibility);
            expandButton = (ImageButton) itemView.findViewById(R.id.partners_expand);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

}
