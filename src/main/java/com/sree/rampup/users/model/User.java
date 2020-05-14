package com.sree.rampup.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * User entity class
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(of = { "id" })
public class User implements Identifier {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    @Getter @Setter
    @Column (nullable = false, unique = true, length = 50)
    UUID id;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    @Getter @Setter
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    @Getter @Setter
    private String lastName;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(nullable = false)
    @Getter @Setter
    private String email;

    @JsonManagedReference
    @JsonIgnore
    @ManyToMany(fetch= FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Getter @Setter
    private Set<Role> roles;

    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    public void removeRole(Role role) {
        if (roles != null) {
            roles.remove(role);
        }
    }
}
