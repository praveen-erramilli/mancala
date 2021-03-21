package com.praveen.mancala.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

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
    @Where(clause = "opposite_id IS NOT NULL AND player_number='0'")
    private List<Pit> pits0;

    @OneToMany(targetEntity = Pit.class, mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    @Where(clause = "opposite_id IS NOT NULL AND player_number='1'")
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

    private Pit searchPit(Long id, List<Pit> pits) {
        return pits.stream().filter(currentPit -> currentPit.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                '}';
    }
}
