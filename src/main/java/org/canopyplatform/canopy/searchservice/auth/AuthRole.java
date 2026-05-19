package org.canopyplatform.canopy.searchservice.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Minimal projection of lkup_role for the search service's access filter.
 * Only the role name is consulted (for Curator/Admin override).
 */
@Getter
@Setter
@Entity
@Table(name = "lkup_role")
public class AuthRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;
}
