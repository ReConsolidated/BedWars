package io.github.reconsolidated.BedWars.DataBase;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "BedWars")
public class BedWarsPlayerDomain {
    @Id
    @Column(length=18)
    private String playerName;

    @Column
    private int kills;

    @Column
    private int finalKills;

    @Column
    private int wins;

    @Column
    private int bedsDestroyed;

    @Column
    private int deaths;

    @Column
    private int sumOfPlaces;

    @Column
    private int gamesPlayed;

}
