package me.carc.stolpersteine.data.db.blocks;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;

import java.util.List;

import me.carc.stolpersteine.data.db.AppDatabase;
import me.carc.stolpersteine.data.remote.model.Stolpersteine;


/**
 * Created by bamptonm on 12/05/2018.
 */

public class StolpersteineRepository {

    public static final int PAGE_SIZE = 20;

    private static final int INITIAL_LOAD_KEY = 0;
    private static final int PREFETCH_DISTANCE = 5;


    private StolpersteineDao mDao;
    public LiveData<PagedList<Stolpersteine>> mutableLiveData;


    StolpersteineRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDao = db.stolpersteineDao();
    }

    LiveData<List<Stolpersteine>> getAllBlocks() {
        return mDao.getAllStumblingBlocks();
    }

    LiveData<List<Stolpersteine>> getAroundLocation(Double lat1, Double lat2, Double lon1, Double lon2) {
        return mDao.getAroundLocation(lat1, lat2, lon1, lon2);
    }

    LiveData<Stolpersteine> getBlock(int id) {
        return mDao.getStumblingBlock(id);
    }

    LiveData<PagedList<Stolpersteine>> getPagedList() {
        return new LivePagedListBuilder<>(mDao.loadPagedList(), PAGE_SIZE).build();
    }

    LiveData<PagedList<Stolpersteine>> getSearchList(String search) {
        return new LivePagedListBuilder<>(mDao.search(search), PAGE_SIZE).build();
    }

    LiveData<Stolpersteine> getBlock(String bioUrl) {
        return mDao.getStumblingBlock(bioUrl);
    }




    void insert (Stolpersteine tour) {
        new insertAsyncTask(mDao).execute(tour);
    }

    private static class insertAsyncTask extends AsyncTask<Stolpersteine, Void, Void> {

        private StolpersteineDao mAsyncTaskDao;

        insertAsyncTask(StolpersteineDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Stolpersteine... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}