package io.github.reconsolidated.BedWars.PostgreDB;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode

public class PlayerGlobalDataDomain {

    private UUID uuid;

    private int grypcioCoins;

    private int experience;

    private int inGameTime;
}
