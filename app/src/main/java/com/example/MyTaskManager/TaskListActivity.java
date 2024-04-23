package com.example.MyTaskManager;

import static com.example.MyTaskManager.TaskDBHelper.COLUMN_DESC;
import static com.example.MyTaskManager.TaskDBHelper.COLUMN_DUE_DATE;
import static com.example.MyTaskManager.TaskDBHelper.COLUMN_ID;
import static com.example.MyTaskManager.TaskDBHelper.COLUMN_TITLE;
import static com.example.MyTaskManager.TaskDBHelper.TABLE_TASKS;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskListActivity extends AppCompatActivity implements TaskAdapter.TaskListener {
    RecyclerView recyclerView;
    Button home;

    private static ArrayList<Task> taskList;
    private TaskAdapter adapter;
    private TaskDBHelper taskDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        recyclerView = findViewById(R.id.recycler_view);
        home = findViewById(R.id.homeButton);

        home.setOnClickListener(v -> {
            onBackPressed();
        });

        taskList = getTaskListDataFromDb();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditTask(Task task) {
        openModifyDialog(task);
    }

    private void openModifyDialog(@Nullable Task task) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_modify_task, null);
        dialogBuilder.setView(dialogView);

        EditText editTextTitle = dialogView.findViewById(R.id.titleEditText);
        EditText editTextDescription = dialogView.findViewById(R.id.descriptionEditText);
        EditText editTextDueDate = dialogView.findViewById(R.id.dueDateEditText);
        Button btnSave = dialogView.findViewById(R.id.saveButton);

        if (task != null) {
            editTextTitle.setText(task.getTaskTitle());
            editTextDescription.setText(task.getTaskDescription());
            editTextDueDate.setText(task.getTaskDueDate());
        }

        AlertDialog alertDialog = dialogBuilder.create();

        btnSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            String dueDate = editTextDueDate.getText().toString().trim();

            if (title.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(TaskListActivity.this, "Please fill all the required fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidDate(dueDate)) {
                Toast.makeText(TaskListActivity.this, "Invalid Date Format (use MM/dd/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            task.setTaskTitle(title);
            task.setTaskDescription(description);
            task.setTaskDueDate(dueDate);
            updateTask(task);
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void addTask(Task task) {
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskDBHelper.COLUMN_TITLE, task.getTaskTitle());
        values.put(TaskDBHelper.COLUMN_DESC, task.getTaskDescription());
        values.put(TaskDBHelper.COLUMN_DUE_DATE, task.getTaskDueDate());
        long id = db.insert(TaskDBHelper.TABLE_TASKS, null, values);

        task.setTaskId((int) id);
        db.close();
        Intent intent = new Intent(TaskListActivity.this, TaskListActivity.class);
        startActivity(intent);
    }

    private void updateTask(Task task) {
        SQLiteDatabase db = taskDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskDBHelper.COLUMN_TITLE, task.getTaskTitle());
        values.put(TaskDBHelper.COLUMN_DESC, task.getTaskDescription());
        values.put(TaskDBHelper.COLUMN_DUE_DATE, task.getTaskDueDate());
        db.update(TaskDBHelper.TABLE_TASKS, values, TaskDBHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(task.getTaskId())});
        db.close();

        int index = taskList.indexOf(task);
        if (index != -1) {
            taskList.set(index, task);
            adapter.notifyItemChanged(index);
        }
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(dateStr);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }
    private ArrayList<Task> getTaskListDataFromDb() {
        taskDBHelper = new TaskDBHelper(this);
        ArrayList<Task> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS;

        SQLiteDatabase db = taskDBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Task task = new Task(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DESC)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE))
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return taskList;
    }

    @Override
    public void onDeleteTask(Task task) {

    }
}