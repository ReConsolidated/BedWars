package io.github.reconsolidated.BedWars.DataBase;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "PlayerGlobalData")
public class PlayerGlobalDataDomain {
    @Id
    @Column
    private UUID uuid;

    @Column
    private int grypcioCoins;

    @Column
    private int experience;

    @Column
    private int inGameTime;
}
