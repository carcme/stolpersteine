package me.carc.stolpersteine.data.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import me.carc.stolpersteine.App;
import me.carc.stolpersteine.data.db.blocks.StolpersteineDao;
import me.carc.stolpersteine.data.remote.model.Stolpersteine;

/**
 * Applicaiton database (Room database)
 *
 * Created by bamptonm on 04/10/2017.
 */

@Database(entities = {Stolpersteine.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract StolpersteineDao stolpersteineDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, App.DATABASE_NAME)
                            .addMigrations(Migrations.MIGRATION_1_2)
//                            .fallbackToDestructiveMigration()  // TODO: REMOVE FOR RELEASE
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

//        private final TourCatalogueDao catalogueDao;
//        private final AttractionDao attractionDao;

        PopulateDbAsync(AppDatabase db) {
//            catalogueDao = db.catalogueDao();
//            attractionDao = db.attractionDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            // PRE-POPULATE THE DB's HERE

            return null;
        }
    }

}