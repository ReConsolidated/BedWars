package io.github.reconsolidated.BedWars.Party;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "Party")
public class PartyDomain {
    @Id
    @Column(length = 64)
    private String owner;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> members;
}
