package com.herosoft.commons.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DefinitionException class
 *
 * @author HanWei
 * @date 2023/01/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DefinitionException extends RuntimeException{
    protected Integer errorCode;
    protected String errorMessage;
}
