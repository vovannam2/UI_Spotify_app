package com.example.spotify_app.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.spotify_app.R;

public class GradientHelper {
    public static void applyGradient(Context context, View view, String imageUrl) {
        GradientDrawable gradientDrawable = (GradientDrawable) context.getDrawable(R.drawable.background_gradient);
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                int defaultColorId = 0;
                                int firstColor = 0;
                                int mutedColor = 0;
                                int vibrantColor = 0;
                                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                                    defaultColorId = R.color.neutral2;
                                    firstColor = R.color.neutral0;
                                    mutedColor = palette.getMutedColor(context.getColor(defaultColorId));
                                    vibrantColor = palette.getDarkVibrantColor(context.getColor(defaultColorId));
                                    if (mutedColor == context.getColor(defaultColorId))
                                        gradientDrawable.setColors(new int[]{context.getColor(firstColor), vibrantColor});
                                    else
                                        gradientDrawable.setColors(new int[]{context.getColor(firstColor), mutedColor});
                                }
                                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                                    defaultColorId = R.color.primary_light_1;
                                    firstColor = R.color.neutral5;
                                    mutedColor = palette.getMutedColor(context.getColor(defaultColorId));
                                    vibrantColor = palette.getVibrantColor(context.getColor(defaultColorId));
                                    if (mutedColor == context.getColor(defaultColorId))
                                        gradientDrawable.setColors(new int[]{context.getColor(firstColor), context.getColor(firstColor), vibrantColor});
                                    else
                                        gradientDrawable.setColors(new int[]{context.getColor(firstColor), context.getColor(firstColor), mutedColor});
                                }

                            }
                        });
                        if (view != null) {
                            view.setBackground(gradientDrawable);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public static void applyGradient(Context context, View view, int drawableId) {
        GradientDrawable gradientDrawable = (GradientDrawable) context.getDrawable(R.drawable.background_gradient);
        Glide.with(context)
                .asBitmap()
                .load(drawableId)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                int defaultColorId = 0;
                                int firstColor = 0;
                                int mutedColor = 0;
                                int vibrantColor = 0;
                                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                                    defaultColorId = R.color.neutral2;
                                    firstColor = R.color.neutral0;
                                    mutedColor = palette.getMutedColor(context.getColor(defaultColorId));
                                    vibrantColor = palette.getDarkVibrantColor(context.getColor(defaultColorId));
                                    if (mutedColor == context.getColor(defaultColorId))
                                        gradientDrawable.setColors(new int[]{context.getColor(firstColor), vibrantColor});
                                    else
                                        gradientDrawable.setColors(new int[]{context.getColor(firstColor), mutedColor});
                                }
                                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                                    defaultColorId = R.color.primary_light_1;
                                    firstColor = R.color.neutral5;
                                    mutedColor = palette.getMutedColor(context.getColor(defaultColorId));
                                    vibrantColor = palette.getVibrantColor(context.getColor(defaultColorId));
                                    if (mutedColor == context.getColor(defaultColorId))
                                        gradientDrawable.setColors(new int[]{context.getColor(firstColor), context.getColor(firstColor), vibrantColor});
                                    else
                                        gradientDrawable.setColors(new int[]{context.getColor(firstColor), context.getColor(firstColor), mutedColor});
                                }
                            }
                        });
                        if (view != null) {
                            view.setBackground(gradientDrawable);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public static void applyDoubleGradient(Context context, View view, String imageUrl) {
        GradientDrawable gradientDrawable = (GradientDrawable) context.getDrawable(R.drawable.linear_background_angle0);
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                int vibrantColor = palette.getVibrantColor(context.getColor(R.color.neutral2));
                                int mutedColor = palette.getMutedColor(context.getColor(R.color.neutral2));
                                int darkVibrant = palette.getDarkVibrantColor(context.getColor(R.color.neutral2));
                                if (mutedColor == context.getColor(R.color.neutral2))
                                    gradientDrawable.setColors(new int[]{vibrantColor, darkVibrant});
                                else
                                    gradientDrawable.setColors(new int[]{darkVibrant, mutedColor});
                            }
                        });
                        if (view != null) {
                            view.setBackground(gradientDrawable);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }
}
