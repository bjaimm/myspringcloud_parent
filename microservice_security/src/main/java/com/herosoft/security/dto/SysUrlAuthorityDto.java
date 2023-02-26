package com.herosoft.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUrlAuthorityDto {
    private String url;
    private String authorities;
}
