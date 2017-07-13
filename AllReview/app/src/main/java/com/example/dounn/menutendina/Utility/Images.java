package com.example.dounn.menutendina.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by lucadiliello on 02/06/2017.
 */

public class Images {

    public static ImagesDataBase database;
    private static Context context;

    public static void setContext(Context c) {
        context = c;
        database = new ImagesDataBase(c);
    }

    ///// IMAGE SAVING

    public synchronized static Bitmap getImage(String name) {
        SQLiteDatabase db = database.getWritableDatabase();
        Bitmap res = null;

        db.execSQL("UPDATE " + ImagesDataBase.TABLE_NAME + " SET " + ImagesDataBase.COLUMN_COUNT + " = " + ImagesDataBase.COLUMN_COUNT + " + 1" + " WHERE " + ImagesDataBase.COLUMN_NAME + " = ?", new String[]{name});
        Cursor cursor = db.rawQuery("SELECT changes() AS affected_row_count", null);
        if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst() && cursor.getLong(cursor.getColumnIndex("affected_row_count")) > 0) {
            res = retrieveBitmapFromDisk(name);
        }
        if(res == null) {
            String selection = ImagesDataBase.COLUMN_NAME + " LIKE ?";
            String[] selectionArgs = {name};
            db.delete(ImagesDataBase.TABLE_NAME, selection, selectionArgs);
        }
        if(cursor != null) {
            cursor.close();
        }
        db.close();
        return res;
    }


    public synchronized static void freeUpSpace() {
        SQLiteDatabase db = database.getReadableDatabase();
        String[] projection = {database.COLUMN_NAME};
        Cursor cursor = db.query(database.TABLE_NAME, projection, null, null, null, null, null);
        ArrayList<String> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            String tmp = cursor.getString(0);
            items.add(tmp);
        }
        for(String s : items) {
            deleteBitmapFromDisk(s);
        }
        db.delete(database.TABLE_NAME, null, null);
        cursor.close();
        db.close();
    }

    public synchronized static boolean containsImage(String name) {
        if(name == null) return false;

        SQLiteDatabase database = Images.database.getWritableDatabase();
        Cursor cursor = database.query(ImagesDataBase.TABLE_NAME, new String[]{ImagesDataBase.COLUMN_NAME}, ImagesDataBase.COLUMN_NAME + " = ?", new String[]{name}, null, null, null);
        boolean res = cursor.getCount() > 0;
        cursor.close();
        database.close();

        return res;
    }

    public synchronized static void addImage(String name, Bitmap bitmap) {
        //non ho motivo di continuare....
        if(bitmap == null || name == null) return;
        //se l'immagine è già presente della cache, esco
        if(containsImage(name)) return;

        //apro il la connessione al database
        SQLiteDatabase database = Images.database.getWritableDatabase();
        ContentValues values = new ContentValues();
        //leggo quante foto ho già nella cache
        int count = rowCount(database);
        //se sono apposto ....
        if(count < Constants.maxDatabaseDimension) {

            values.put(ImagesDataBase.COLUMN_NAME, name);
            values.put(ImagesDataBase.COLUMN_COUNT, 0);

            try {
                database.insert(ImagesDataBase.TABLE_NAME, null, values);
                saveBitmapToDisk(name, bitmap);

            } catch(SQLiteConstraintException e) {
                e.printStackTrace();
            }
        } else {
            //altrimenti devo eliminare qualche foto vecchia per non appesantire lo smartphone
            String toDelete;
            //scelgo quelle meno utilizzate per far posto a quelle nuove
            Cursor cursor = database.query(ImagesDataBase.TABLE_NAME, new String[]{ImagesDataBase.COLUMN_NAME}, "COUNT = MIN(COUNT)", null, null, null, null);
            if(cursor.moveToNext()) {
                toDelete = cursor.getString(0);
                //elimino la foto da disco
                deleteBitmapFromDisk(toDelete);

                //inserisco il nuovo nome dell'immagine al posto della precedente e la salvo su disco
                values.put(ImagesDataBase.COLUMN_NAME, name);
                values.put(ImagesDataBase.COLUMN_COUNT, 0);
                database.update(ImagesDataBase.TABLE_NAME, values, "NAME = ?", new String[]{toDelete});
                saveBitmapToDisk(name, bitmap);
            }
            cursor.close();
        }
        database.close();
    }


    private synchronized static int rowCount(SQLiteDatabase database) {
        Cursor cursor = database.query(ImagesDataBase.TABLE_NAME, null, null, null, null, null, null);
        int res = cursor.getCount();
        cursor.close();
        return res;
    }

    /// DISK PRIMITIVES
    private static void saveBitmapToDisk(String name, Bitmap bitmap) {
        String string = Images.toEncodedString(bitmap);
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static Bitmap retrieveBitmapFromDisk(String name) {

        FileInputStream fis;
        String res = null;
        try {
            fis = context.openFileInput(name);

            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line = "";
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            res = sb.toString();
            fis.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return StringToBitMap(res);

    }

    private static boolean deleteBitmapFromDisk(String name) {
        File dir = context.getFilesDir();
        File file = new File(dir, name);
        return file.delete();
    }


    public static String toEncodedString(Bitmap bt) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bt.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap resizeDefaultBitmap(Bitmap bt) {
        return ThumbnailUtils.extractThumbnail(bt, Constants.defaultImageHeight, Constants.defaultImageWidth, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }
}
