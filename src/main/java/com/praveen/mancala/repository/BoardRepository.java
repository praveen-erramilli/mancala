package com.praveen.mancala.repository;

import com.praveen.mancala.model.Board;
import org.springframework.data.repository.CrudRepository;

public interface BoardRepository extends CrudRepository<Board, Long> {
}
