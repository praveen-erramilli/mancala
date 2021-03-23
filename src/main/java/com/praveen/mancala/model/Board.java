package com.praveen.mancala.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(targetEntity = Pit.class, mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    @Where(clause = "dtype = 'Pit' AND player_number='0'")
    private List<Pit> pitsForPlayerZero;

    @OneToMany(targetEntity = Pit.class, mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    @Where(clause = "dtype = 'Pit' AND player_number='1'")
    private List<Pit> pitsForPlayerOne;

    @OneToOne(cascade = CascadeType.ALL)
    @Where(clause = "dtype = 'BigPit' AND player_number='0'")
    private BigPit bigPitForPlayerZero;

    @OneToOne(cascade = CascadeType.ALL)
    @Where(clause = "dtype = 'BigPit' AND player_number='1'")
    private BigPit bigPitForPlayerOne;

    public Pit fetchPit(Long id) {

        Pit currentPit1 = searchPit(id, pitsForPlayerZero);
        if (currentPit1 != null) {
            return currentPit1;
        }

        Pit currentPit = searchPit(id, pitsForPlayerOne);
        if (currentPit != null) {
            return currentPit;
        }
        throw new IllegalArgumentException("No pit found for given id");
    }

    private Pit searchPit(Long id, List<Pit> pits) {
        return pits.stream().filter(currentPit -> currentPit.getId().equals(id)).findFirst().orElse(null);
    }
}
