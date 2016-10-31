package com.example.dell.wordsapp5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
public class MainActivity extends Activity implements OnClickListener, TextWatcher
{
    //¶¨ÒåÊý¾Ý¿âµÄ´æ·ÅÂ·¾¶
    private final String DATABASE_PATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/dictionary";
    //ÓÃ»§ÊäÈëÎÄ±¾¿ò
    private AutoCompleteTextView word;
    //¶¨ÒåÊý¾Ý¿âµÄÃû×Ö
    private final String DATABASE_FILENAME = "dictionary.db";
    private SQLiteDatabase database;
    //ËÑË÷°´Å¥
    private Button searchWord;
    //ÓÃ»§ÏÔÊ¾²éÑ¯½á¹û
    private TextView showResult;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //´ò¿ªÊý¾Ý¿â
        database = openDatabase();
        searchWord = (Button) findViewById(R.id.searchWord);
        word = (AutoCompleteTextView) findViewById(R.id.word);
        //°ó¶¨¼àÌýÆ÷
        searchWord.setOnClickListener(this);
        //°ó¶¨ÎÄ×Ö¸Ä±ä¼àÌýÆ÷
        word.addTextChangedListener(this);
        showResult=(TextView)findViewById(R.id.result);
    }
    //×Ô¶¨ÒåAdapterÀà
    public class DictionaryAdapter extends CursorAdapter
    {
        private LayoutInflater layoutInflater;
        @Override
        public CharSequence convertToString(Cursor cursor)
        {
            return cursor == null ? "" : cursor.getString(cursor
                    .getColumnIndex("_id"));
        }
        //½«µ¥´ÊÐÅÏ¢ÏÔÊ¾µ½ÁÐ±íÖÐ
        private void setView(View view, Cursor cursor)
        {
            TextView tvWordItem = (TextView) view;
            tvWordItem.setText(cursor.getString(cursor.getColumnIndex("_id")));
        }
        //°ó¶¨Ñ¡Ïîµ½ÁÐ±íÖÐ
        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
            setView(view, cursor);
        }
        //Éú³ÉÐÂµÄÑ¡Ïî
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent)
        {
            View view = layoutInflater.inflate(R.layout.word_list_item, null);
            setView(view, cursor);
            return view;
        }
        public DictionaryAdapter(Context context, Cursor c, boolean autoRequery)
        {
            super(context, c, autoRequery);
            layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }
    public void afterTextChanged(Editable s)
    {
        //  ±ØÐë½«english×Ö¶ÎµÄ±ðÃûÉèÎª_id
        Cursor cursor = database.rawQuery("select english as _id from t_words where english like ?", new String[]{ s.toString() + "%" });
        //ÐÂ½¨ÐÂµÄAdapter
        DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this,
                cursor, true);
        //°ó¶¨ÊÊÅäÆ÷
        word.setAdapter(dictionaryAdapter);

    }
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after)
    {
        // TODO Auto-generated method stub

    }
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        // TODO Auto-generated method stub

    }
    public void onClick(View view)
    {
        //²éÑ¯Ö¸¶¨µÄµ¥´Ê
        String sql = "select chinese from t_words where english=?";
        Cursor cursor = database.rawQuery(sql, new String[]
                {word.getText().toString()});
        String result = "Î´ÕÒµ½¸Ãµ¥´Ê.";
        //  Èç¹û²éÕÒµ¥´Ê£¬ÏÔÊ¾ÆäÖÐÎÄµÄÒâË¼
        if (cursor.getCount() > 0)
        {
            //  ±ØÐëÊ¹ÓÃmoveToFirst·½·¨½«¼ÇÂ¼Ö¸ÕëÒÆ¶¯µ½µÚ1Ìõ¼ÇÂ¼µÄÎ»ÖÃ
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("chinese")).replace("&amp;", "&");
        }
        //½«½á¹ûÏÔÊ¾µ½TextViewÖÐ
        showResult.setText(word.getText()+"\n"+result.toString());
    }
    private SQLiteDatabase openDatabase()
    {
        try
        {
            // »ñµÃdictionary.dbÎÄ¼þµÄ¾ø¶ÔÂ·¾¶
            String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            // Èç¹û/sdcard/dictionaryÄ¿Â¼ÖÐ´æÔÚ£¬´´½¨Õâ¸öÄ¿Â¼
            if (!dir.exists())
                dir.mkdir();
            // Èç¹ûÔÚ/sdcard/dictionaryÄ¿Â¼ÖÐ²»´æÔÚ
            // dictionary.dbÎÄ¼þ£¬Ôò´Óres\rawÄ¿Â¼ÖÐ¸´ÖÆÕâ¸öÎÄ¼þµ½
            // SD¿¨µÄÄ¿Â¼£¨/sdcard/dictionary£©
            if (!(new File(databaseFilename)).exists())
            {
                // »ñµÃ·â×°dictionary.dbÎÄ¼þµÄInputStream¶ÔÏó
                InputStream is = getResources().openRawResource(
                        R.raw.dictionary);
                FileOutputStream fos = new FileOutputStream(databaseFilename);
                byte[] buffer = new byte[8192];
                int count = 0;
                // ¿ªÊ¼¸´ÖÆdictionary.dbÎÄ¼þ
                while ((count = is.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, count);
                }
                //¹Ø±ÕÎÄ¼þÁ÷
                fos.close();
                is.close();
            }
            // ´ò¿ª/sdcard/dictionaryÄ¿Â¼ÖÐµÄdictionary.dbÎÄ¼þ
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
                    databaseFilename, null);
            return database;
        }
        catch (Exception e)
        {
        }
        //Èç¹û´ò¿ª³ö´í£¬Ôò·µ»Ønull
        return null;
    }
}
