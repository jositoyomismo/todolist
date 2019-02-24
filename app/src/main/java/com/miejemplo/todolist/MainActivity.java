package com.miejemplo.todolist;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.support.v7.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.widget.ArrayAdapter;
import android.database.Cursor;
import android.widget.ListView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import com.miejemplo.todolist.db.TaskContract;
import com.miejemplo.todolist.db.TaskDbHelper;
public class MainActivity extends AppCompatActivity {
    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface miFuente = Typeface.createFromAsset(getAssets(),"merkur.ttf");
        TextView titulo = findViewById(R.id.titular);
        titulo.setTypeface(miFuente);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.trekaudio);
        mediaPlayer.start();

        mHelper = new TaskDbHelper(this);
        mTaskListView = (ListView) findViewById(R.id.list_todo);
        updateUI();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_add_task:

                final EditText taskEditText = new EditText(this);

                AlertDialog dialog = new AlertDialog.Builder(this)

                        .setTitle("Añade una nueva tarea")

                        .setMessage("¿Qué quieres hacer a continuación?")

                        .setView(taskEditText)

                        .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {

                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {

                                        String task = String.valueOf(taskEditText.getText());


                                        SQLiteDatabase db = mHelper.getWritableDatabase();

                                        ContentValues values = new ContentValues();

                                        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);

                                        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                                        db.close();

                                        updateUI();

                                    }

                                }

                                )

                        .setNegativeButton("Cancelar", null)

                        .create();



                dialog.show();
                Toast toast1 = Toast.makeText(this, "Se va a añadir una tarea",Toast.LENGTH_LONG);
                toast1.show();

                return true;



            default:

                return super.onOptionsItemSelected(item);

        }

    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView)
                parent.findViewById(R.id.titulo);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
        Toast toast = Toast.makeText(this, "El mensaje ha sido borrado",Toast.LENGTH_LONG);
        toast.show();
    }
    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx =
                    cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }
        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.titulo,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }
}