package com.sree.rampup.users.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Role entity class
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(of = { "id" })
public class Role implements Identifier {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    @Getter @Setter
    @Column (nullable = false, unique = true, length = 50)
    private UUID id;

    @NotBlank
    @Size(min = 1, max = 50)
    @Getter @Setter
    @Column (nullable = false)
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy="role", fetch = FetchType.LAZY)
    @Getter @Setter
    private Set<Permission> permissions;

    @JsonBackReference
    @ManyToMany(mappedBy = "roles", fetch= FetchType.LAZY)
    @Getter @Setter
    private Set<User> users;

    public void removePermission(Permission permission) {
        if (permissions != null) {
            permissions.remove(permission);
        }
    }

    public void addPermission(Permission permission) {
        if (permissions == null) {
            permissions = new HashSet<>();
        }
        permissions.add(permission);
    }

    public void removeAllPermissions() {
        if (permissions != null) {
            permissions.clear();
        }
    }
}
