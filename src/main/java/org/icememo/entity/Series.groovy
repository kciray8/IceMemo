package org.icememo.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    String name

    Integer user;
}
