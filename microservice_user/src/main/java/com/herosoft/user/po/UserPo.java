package com.herosoft.user.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@TableName("user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPo implements Serializable {
    @TableId(type= IdType.AUTO)
    private Integer id;
    @NotBlank(message = "用户名不可以为空")
    private String username;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdt;

    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedt;
    private String password;
    private String sex;
    private Double balance;
    @Version
    @TableField("version")
    private Integer version;
    @Override
    public String toString() {
        String createdtStr;
        String updatedtStr;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(Optional.ofNullable(createdt).isPresent()){
            createdtStr= sdf.format(Date.from(createdt.atZone(ZoneId.systemDefault()).toInstant()));
        }
        else{
            createdtStr="null";
        }

        if(Optional.ofNullable(updatedt).isPresent()){
            updatedtStr= sdf.format(Date.from(updatedt.atZone(ZoneId.systemDefault()).toInstant()));
        }
        else{
            updatedtStr="null";
        }

        //Date转为LocalDateTime
        Date today = new Date(System.currentTimeMillis());
        today.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", balance=" + balance +'\''+
                ",createdt='"+ createdtStr+'\''+
                ",updatedt='"+updatedtStr+
                '}';
    }





}
