package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

  @Bind(R.id.pager) ViewPager mPager;
  private Cursor mCursor;
  private long mStartId;
  private long mSelectedItemId;
  private MyPagerAdapter mPagerAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article_detail);
    ButterKnife.bind(this);

    getLoaderManager().initLoader(0, null, this);

    mPagerAdapter = new MyPagerAdapter(getFragmentManager());
    mPager.setAdapter(mPagerAdapter);
    mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override public void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
      }

      @Override public void onPageSelected(int position) {
        if (mCursor != null) {
          mCursor.moveToPosition(position);
        }
        mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
      }
    });

    if (savedInstanceState == null) {
      if (getIntent() != null && getIntent().getData() != null) {
        mStartId = ItemsContract.Items.getItemId(getIntent().getData());
        mSelectedItemId = mStartId;
      }
    }
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return ArticleLoader.newAllArticlesInstance(this);
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    mCursor = cursor;
    mPagerAdapter.notifyDataSetChanged();

    // Select the start ID
    if (mStartId > 0) {
      mCursor.moveToFirst();
      // TODO: optimize
      while (!mCursor.isAfterLast()) {
        if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
          final int position = mCursor.getPosition();
          mPager.setCurrentItem(position, false);
          break;
        }
        mCursor.moveToNext();
      }
      mStartId = 0;
    }
  }

  @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {
    mCursor = null;
    mPagerAdapter.notifyDataSetChanged();
  }

  private class MyPagerAdapter extends FragmentStatePagerAdapter {
    public MyPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public void setPrimaryItem(ViewGroup container, int position, Object object) {
      super.setPrimaryItem(container, position, object);
    }

    @Override public Fragment getItem(int position) {
      mCursor.moveToPosition(position);
      return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
    }

    @Override public int getCount() {
      return (mCursor != null) ? mCursor.getCount() : 0;
    }

    //        @Override
    //        public void destroyItem(ViewGroup container, int position, Object object) {
    //            // TODO Auto-generated method stub
    //            ((ViewPager) container).removeView((View) object);
    //        }
  }
}