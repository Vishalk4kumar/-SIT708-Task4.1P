package com.example.MyTaskManager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskListener {
    private static List<Task> taskList;
    private TaskAdapter adapter;
    private TaskDBHelper taskDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton addButton = findViewById(R.id.add_button);
        Button viewTaskButton = findViewById(R.id.viewTaskButton); // Added view task button

        taskDBHelper = new TaskDBHelper(this);


        taskList = new ArrayList<>();

        /*initTasks();*/

//        adapter = new TaskAdapter(taskList, this);
//        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> openAddDialog());

        // Click listener for the view task button
        viewTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
            startActivity(intent);
        });
    }


    /*private void initTasks() {
        SQLiteDatabase db = taskDBHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskDBHelper.TABLE_TASKS, null, null, null, null, null, null);
        taskList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(TaskDBHelper.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskDBHelper.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TaskDBHelper.COLUMN_DESC)); // Corrected column name
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TaskDBHelper.COLUMN_DUE_DATE));

                Task task = new Task(id, title, description, dueDate);
                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
    }*/

    private void openAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_modify_task, null);
        dialogBuilder.setView(dialogView);

        EditText editTextTitle = dialogView.findViewById(R.id.titleEditText);
        EditText editTextDescription = dialogView.findViewById(R.id.descriptionEditText);
        EditText editTextDueDate = dialogView.findViewById(R.id.dueDateEditText);
        Button btnSave = dialogView.findViewById(R.id.saveButton);

        AlertDialog alertDialog = dialogBuilder.create();

        btnSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            String dueDate = editTextDueDate.getText().toString().trim();

            if (title.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all the required fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidDate(dueDate)) {
                Toast.makeText(MainActivity.this, "Invalid Date Format (use MM/dd/yyyy)", Toast.LENGTH_SHORT).show();
                return;
            }

            Task newTask = new Task(0, title, description, dueDate);
            addTask(newTask);
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
        Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
        startActivity(intent);
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

    @Override
    public void onDeleteTask(Task task) {}

    @Override
    public void onEditTask(Task task) {}
}
