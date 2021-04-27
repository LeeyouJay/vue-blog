package com.arslinth.entity.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Arslinth
 * @ClassName RoleAuths
 * @Description TODO
 * @Date 2021/3/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAuths {

    private String roleId;

    private String auth;

}
