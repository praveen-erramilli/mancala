package com.praveen.mancala.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Player {
    @GeneratedValue
    @Id
    private final Long id;

    private final int playerNumber;
}