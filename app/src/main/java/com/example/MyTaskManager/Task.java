package com.example.MyTaskManager;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    private int taskId;
    private String taskTitle;
    private String taskDescription;
    private String taskDueDate;

    public Task(int taskId, String taskTitle, String taskDescription, String taskDueDate) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskDueDate = taskDueDate;
    }

    protected Task(Parcel in) {
        taskId = in.readInt();
        taskTitle = in.readString();
        taskDescription = in.readString();
        taskDueDate = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(String taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(taskId);
        dest.writeString(taskTitle);
        dest.writeString(taskDescription);
        dest.writeString(taskDueDate);
    }
}