package com.pegasusgroup.riarewards.interfaces;

public interface OnFavouriteClickListener {
    void favouriteClick(final String userId, final String productId, final boolean flag, int position);
}