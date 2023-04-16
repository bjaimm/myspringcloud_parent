package com.herosoft.user.mongo;

import lombok.*;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@Data
public class Student implements Serializable {
    @Id
    private ObjectId studentId;
    private String studentName;
    private Integer studentAge;
    private Date birthday;

}
