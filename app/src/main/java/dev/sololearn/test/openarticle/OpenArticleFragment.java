package dev.sololearn.test.openarticle;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import dev.sololearn.test.GlideApp;
import dev.sololearn.test.R;
import dev.sololearn.test.databinding.OpenArticleActivityBinding;
import dev.sololearn.test.datamodel.local.Article;
import dev.sololearn.test.datamodel.local.ArticleFields;
import dev.sololearn.test.feed.FeedActivity;
import dev.sololearn.test.feed.FeedViewModel;
import dev.sololearn.test.util.CacheFileManager;
import dev.sololearn.test.util.Constants;
import dev.sololearn.test.util.PinUnPinPendingEvent;

public class OpenArticleFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "openArticleFragment";

    private OpenArticleActivityBinding binding;

    private ObservableBoolean isSaved = new ObservableBoolean();
    private Article currentArticle;
    private FeedViewModel feedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.open_article_activity, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        feedViewModel = FeedActivity.obtainViewModel(getActivity());
        init();
    }

    private void init() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            currentArticle = arguments.getParcelable(Constants.EXTRA_ARTICLE);
            binding.articleThumbnail.setTransitionName(arguments.getString(Constants.EXTRA_TRANSITION_NAME_THUMB));

        }
        binding.setListener(this);
        binding.setArticle(currentArticle);
        isSaved.set(currentArticle.savedForOffline);
        binding.setSaved(isSaved);
        binding.executePendingBindings();
        loadImage(currentArticle);
    }

    private void loadImage(Article article) {
        String url;
        if (article.savedForOffline && article.articleFields.articleThumbnailPath != null) {
            File cacheFile = new File(getActivity().getFilesDir(), article.articleFields.articleThumbnailPath);
            url = cacheFile.getPath();
        } else {
            url = article.articleFields.articleThumbnail;
        }
        postponeEnterTransition();
        GlideApp.with(OpenArticleFragment.this)
                .asBitmap().load(url).apply(
                RequestOptions.placeholderOf(R.drawable.image_place_holder))
                .dontAnimate().listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e,
                                        Object model, Target<Bitmap> target, boolean isFirstResource) {
                startPostponedEnterTransition();
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model,
                                           Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                startPostponedEnterTransition();
                return false;
            }
        }).into(binding.articleThumbnail);
    }

    private void saveArticleThumbnail(Runnable runnable) {
        if (currentArticle != null) {
            ArticleFields articleFields = currentArticle.articleFields;
            if (articleFields != null) {
                articleFields.articleThumbnailPath = currentArticle.constructArticleThumbnailPath();
                CacheFileManager.downloadAndSaveImageInternal(getActivity(), articleFields.articleThumbnail,
                        articleFields.articleThumbnailPath, runnable);
            } else {
                runnable.run();
            }
        } else {
            runnable.run();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        switch (id) {
            case R.id.open_full_article:
                Intent showArticleIntent = new Intent();
                showArticleIntent.setAction(Intent.ACTION_VIEW);
                showArticleIntent.setData(Uri.parse(currentArticle.webUrl));
                PackageManager packageManager = activity.getPackageManager();
                if (showArticleIntent.resolveActivity(packageManager) != null) {
                    startActivity(showArticleIntent);
                } else {
                    Toast.makeText(activity, R.string.no_app_for_handle, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.save_for_offline:
                saveArticleThumbnail(() -> {
                    isSaved.set(true);
                    feedViewModel.saveArticleForOffline(currentArticle);
                });
                break;
            case R.id.pin_unpin_article:
                if (currentArticle.pinned) {
                    feedViewModel.setPendingPinUnPinAricle(currentArticle, PinUnPinPendingEvent.MAKE_UN_PIN);
                } else {
                    saveArticleThumbnail(() -> feedViewModel.setPendingPinUnPinAricle(currentArticle, PinUnPinPendingEvent.MAKE_PIN));
                }
                break;
        }
    }
}
