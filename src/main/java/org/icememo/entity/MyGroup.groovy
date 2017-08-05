package org.icememo.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class MyGroup {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id

    Integer user

    String name

    Integer size = 0
}
