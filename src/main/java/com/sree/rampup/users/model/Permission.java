package com.sree.rampup.users.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

/**
 * Permission entity class
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(of = { "id" })
public class Permission implements Identifier {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type="uuid-char")
    @Getter @Setter
    @Column (nullable = false, unique = true, length = 50)
    private UUID id;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column (nullable = false)
    @Getter @Setter
    private String name;

    @JsonBackReference
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="role_id", referencedColumnName="id")
    @Getter @Setter
    private Role role;

    @Column(nullable = false)
    @Getter @Setter
    private boolean isEnabled;
}
