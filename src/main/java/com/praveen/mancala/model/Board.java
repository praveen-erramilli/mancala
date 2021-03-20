package com.praveen.mancala.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.List;

import static com.praveen.mancala.AppConstants.NUM_PITS;

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
    private List<Pit> pits0;

    @OneToMany(targetEntity = Pit.class, mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Pit> pits1;

    @OneToOne(cascade = CascadeType.ALL)
    private Mancala mancala0;

    @OneToOne(cascade = CascadeType.ALL)
    private Mancala mancala1;

    public Pit fetchPit(Long id) {

        Pit currentPit1 = searchPit(id, pits0);
        if (currentPit1 != null) {
            return currentPit1;
        }

        Pit currentPit = searchPit(id, pits1);
        if (currentPit != null) {
            return currentPit;
        }
        throw new IllegalArgumentException("No pit found for given id");
    }

    private Pit searchPit(Long id, List<Pit> pits0) {
        for (int j = 0; j < NUM_PITS; j++) {
            Pit currentPit = pits0.get(j);
            if (currentPit.getId().equals(id)) {
                return currentPit;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                '}';
    }
}
