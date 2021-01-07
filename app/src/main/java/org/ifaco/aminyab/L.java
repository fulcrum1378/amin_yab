package org.ifaco.aminyab;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.RoomDatabase;

import java.util.List;

class L {
    static final String ID = "id", COORDINATES = "coordinates", LATITUDE = "latitude", LONGITUDE = "longtitude", TIME = "time";

    @Entity
    public static class Coordinates {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = ID)
        long id;

        @ColumnInfo(name = LATITUDE)
        double latitude;

        @ColumnInfo(name = LONGITUDE)
        double longitude;

        @ColumnInfo(name = TIME)
        long time;

        Coordinates(double latitude, double longitude, long time) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.time = time;
        }

        Coordinates set(String attr, Object value) {
            switch (attr) {
                case LATITUDE:
                    this.latitude = (double) value;
                    break;
                case LONGITUDE:
                    this.longitude = (double) value;
                    break;
                case TIME:
                    this.time = (long) value;
                    break;
            }
            return this;
        }
    }

    @Dao
    public interface CoorDao {
        @Query("SELECT * FROM " + COORDINATES)
        List<Coordinates> getAll();

        @Insert
        long insert(Coordinates coordinates);

        @Delete
        void delete(Coordinates coordinates);
    }

    @Database(entities = {Coordinates.class}, version = 1, exportSchema = false)
    public abstract static class CoordManager extends RoomDatabase {
        public abstract CoorDao dao();
    }
}
