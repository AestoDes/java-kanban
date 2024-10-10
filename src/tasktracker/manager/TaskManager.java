package tasktracker.manager;

import tasktracker.model.Epic;
import tasktracker.model.Subtask;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private int idCounter = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public int generateId() {
        return idCounter++;
    }

    // Создание задачи
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // Создание эпика
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // Создание подзадачи
    public void createSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    // Обновление задачи
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // Обновление подзадачи
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    // Обновление статуса эпика на основе подзадач
    private void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean hasInProgress = false;
        boolean allDone = true;
        boolean allNew = true;

        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);

            if (subtask != null) {
                TaskStatus subtaskStatus = subtask.getStatus();

                if (subtaskStatus == TaskStatus.IN_PROGRESS) {
                    hasInProgress = true;
                }

                if (subtaskStatus != TaskStatus.DONE) {
                    allDone = false;
                }

                if (subtaskStatus != TaskStatus.NEW) {
                    allNew = false;
                }
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (hasInProgress) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS); // Default to IN_PROGRESS if none of the other conditions are met
        }
    }

    // Получение списка всех задач
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Получение всех эпиков
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Получение всех подзадач эпика
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> subtaskList = new ArrayList<>();
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtaskList.add(subtasks.get(subtaskId));
            }
        }
        return subtaskList;
    }

    // Удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Удаление всех эпиков и их подзадач
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    // Удаление задачи по идентификатору
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // Удаление эпика по идентификатору
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }
}
