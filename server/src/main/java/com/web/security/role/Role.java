package com.web.security.role;

import javax.persistence.*;

/**
 * Entity class used for holding information about a role.
 * Note: Roles are not particularly used in a project but could
 * be utilized for more complicated resource access & feature management.
 */
@Entity
public class Role
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleType name;

    public Role()
    {
    }

    public Role(RoleType name)
    {
        this.name = name;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public RoleType getName()
    {
        return name;
    }

    public void setName(RoleType name)
    {
        this.name = name;
    }
}
