package io.github.reconsolidated.BedWars.Party;

import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class PartyDomain {
    private String owner;
    private List<String> members;
}
