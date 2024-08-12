package serializers;

import task.Status;

public class Serializer {
    public static Status defineStatusOfTask(String stringStatus) {
        Status status = Status.NEW;
        if (stringStatus.equals("NEW")) {
            status = Status.NEW;
        } else if (stringStatus.equals("IN_PROGRESS")) {
            status = Status.IN_PROGRESS;
        } else if (stringStatus.equals("DONE")) {
            status = Status.DONE;
        }
        return status;
    }
}
