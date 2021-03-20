package com.praveen.mancala.repository;

import com.praveen.mancala.model.Pit;
import org.springframework.data.repository.CrudRepository;

public interface PitRepository extends CrudRepository<Pit, Long> {
}
