package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.model;

public enum NotificationMessages {
    TASK_CREATED("A new task has been created by %s"),
    TASK_UPDATED("The task %s details have been updated"),
    TASK_DELETED("The task %s has been deleted"),
    TEAM_CREATED("A new team has been created by %s"),
    TEAM_UPDATED("The team %s details have been updated"),
    TEAM_DELETED("The team %s has been deleted");


    private final String messageTemplate;

    NotificationMessages(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getMessage(String details) {
        return String.format(messageTemplate, details);
    }
}
