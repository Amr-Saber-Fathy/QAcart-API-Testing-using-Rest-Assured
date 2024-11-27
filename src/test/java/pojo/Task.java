package pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {

    @JsonProperty("item")
    private Object item;

    @JsonProperty("isCompleted")
    private Object isCompleted;

}
