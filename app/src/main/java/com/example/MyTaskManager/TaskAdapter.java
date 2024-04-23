package com.example.MyTaskManager;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private ArrayList<Task> tasks;
    private TaskListener taskListener;

    public TaskAdapter(ArrayList<Task> tasks, TaskListener taskListener) {
        this.tasks = tasks;
        this.taskListener = taskListener;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_view, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public interface TaskListener {
        void onDeleteTask(Task task);
        void onEditTask(Task task);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView;
        private TextView dueDateView;
        private ImageView moreOptionsView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.taskTitleTextView);
            dueDateView = itemView.findViewById(R.id.dueDateTextView);
            moreOptionsView = itemView.findViewById(R.id.moreOptionsImageView);
        }

        @SuppressLint("NonConstantResourceId")
        public void bind(Task task) {
            titleView.setText(task.getTaskTitle());
            dueDateView.setText("Due Date: " + task.getTaskDueDate());

            moreOptionsView.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenu().add("Delete");
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Delete")) {
                         SQLiteDatabase db = new TaskDBHelper(v.getContext()).getWritableDatabase();
                        db.delete(TaskDBHelper.TABLE_TASKS, TaskDBHelper.COLUMN_TITLE + " = ?", new String[]{String.valueOf(task.getTaskTitle())});
                        db.close();
                        tasks.remove(task);
                        notifyDataSetChanged();
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });

            itemView.setOnClickListener(v -> taskListener.onEditTask(task));
        }
    }
}


