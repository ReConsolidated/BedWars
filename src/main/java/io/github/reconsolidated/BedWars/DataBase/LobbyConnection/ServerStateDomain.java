package io.github.reconsolidated.BedWars.DataBase.LobbyConnection;

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
@Table(name = "ServerState")
public class ServerStateDomain {
    @Id
    @Column(length=18)
    private String serverName;

    @Column(length=12)
    private String serverType;

    @Column
    private int availableSlots;

    @Column
    private int totalSlots;

    @Column
    private int maxAcceptableGroup;

    @Column
    private int parties;

}
