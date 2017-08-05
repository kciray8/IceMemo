package org.icememo.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
class ConvertTask {
    enum Status {
        READY, UPLOADING, UPLOADED, CONVERTING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    Integer user

    String tempFileName

    Status status = Status.READY

    Integer seasonId

    String seasonName

    String name

    Integer num
}