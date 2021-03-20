package com.praveen.mancala.repository;

import com.praveen.mancala.model.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {
}
