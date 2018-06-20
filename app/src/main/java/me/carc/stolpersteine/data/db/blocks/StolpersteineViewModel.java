package me.carc.stolpersteine.data.db.blocks;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import org.osmdroid.util.BoundingBox;

import java.util.List;

import me.carc.stolpersteine.data.remote.model.Stolpersteine;


/**
 * Created by bamptonm on 12/05/2018.
 */

public class StolpersteineViewModel extends AndroidViewModel {

    private StolpersteineRepository mRepository;
    private LiveData<List<Stolpersteine>> mAllBlocks;

    public StolpersteineViewModel(@NonNull Application application) {
        super(application);
        mRepository = new StolpersteineRepository(application);
        mAllBlocks = mRepository.getAllBlocks();
    }

    public LiveData<PagedList<Stolpersteine>> getPagedList() {
        return mRepository.getPagedList();
    }

    public LiveData<PagedList<Stolpersteine>> getLocalPagedList(BoundingBox box) {
        return mRepository.loadLocalPagedList(box.getLatSouth(), box.getLatNorth(), box.getLonWest(), box.getLonEast());
    }

    public LiveData<PagedList<Stolpersteine>> search(String search) {
        return mRepository.getSearchList(search);
    }

    public LiveData<List<Stolpersteine>> getAroundLocation(BoundingBox box) {
        return mRepository.getAroundLocation(box.getLatSouth(), box.getLatNorth(), box.getLonWest(), box.getLonEast());
    }

    public LiveData<List<Stolpersteine>> getAllBlocks() {
        return mAllBlocks;
    }

    public LiveData<Stolpersteine> getBlock(int id) {
        return mRepository.getBlock(id);
    }

    public LiveData<Stolpersteine> getBlock(String bioUrl) {
        return mRepository.getBlock(bioUrl);
    }

    public void insert(Stolpersteine tour) { mRepository.insert(tour); }
}
