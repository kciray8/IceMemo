package org.icememo.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    String name

    Integer user;

    Integer series

    Double volume = 1

    Double subtitlesOffset = 0
}
