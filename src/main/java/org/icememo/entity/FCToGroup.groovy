package org.icememo.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
class FCToGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    Integer user

    Integer flashcard

    Integer myGroup
}
