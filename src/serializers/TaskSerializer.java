package serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import task.Task;

import java.lang.reflect.Type;

public class TaskSerializer implements JsonSerializer<Task> {
    @Override
    public JsonElement serialize(Task task, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        if (task.getId() != 0) {
            result.addProperty("id", task.getId());
        }

        result.addProperty("nameOfTask", task.getName());
        result.addProperty("descriptionOfTask", task.getDescription());
        result.addProperty("statusOfTask", task.getStatus().toString());
        result.addProperty("year", task.getStartTime().getYear());
        result.addProperty("month", task.getStartTime().getMonthValue());
        result.addProperty("day", task.getStartTime().getDayOfMonth());
        result.addProperty("hour", task.getStartTime().getHour());
        result.addProperty("minute", task.getStartTime().getMinute());
        result.addProperty("duration", task.getDuration().getSeconds());

        return result;
    }

}