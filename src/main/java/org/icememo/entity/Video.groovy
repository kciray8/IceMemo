package org.icememo.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Video{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    String src

    Integer user

    String shortName

    Integer season

    String name

    Integer num = 0

    Boolean done = false

    Integer getVolume() {
        if(volume == null){
            return 1;
        }

        return volume
    }
    Integer volume
}
