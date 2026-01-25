package com.example.golbackend.modules.team_managment.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "team")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @NotBlank(message = "El nombre del campeonato es obligatorio")
    private String name;
    private String logo;
    private String presidente;
    private String motto;


    public Long getTeamId() {
        return id;
    }

}
