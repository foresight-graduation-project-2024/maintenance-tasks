package com.foresight.taskmanagmentservicebackend.taskmanagmentservicebackend.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequences")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TaskSequence {
    @Id
    private String id;
//    private String sequenceName;
    private long sequenceValue;
}
