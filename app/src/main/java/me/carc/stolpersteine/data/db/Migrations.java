package me.carc.stolpersteine.data.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

/**
 * Created by bamptonm on 27/10/2017.
 */

public class Migrations {


    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE stolpersteine_table ADD COLUMN imageTitle TEXT");
            database.execSQL("ALTER TABLE stolpersteine_table ADD COLUMN publicImage TEXT");
            database.execSQL("ALTER TABLE stolpersteine_table ADD COLUMN bigImage TEXT");
            database.execSQL("ALTER TABLE stolpersteine_table ADD COLUMN thumbnail TEXT");
        }
    };
}
