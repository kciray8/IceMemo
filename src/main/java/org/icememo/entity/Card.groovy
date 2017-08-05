package org.icememo.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Lob

@Entity
class Card{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    Integer user

    Double timeStart

    Double timeEnd

    Double duration

    @Lob
    String text

    Long date

    Integer video

    String videoShortNameCache

    Integer num

    Integer lastRepetition = 0//1,4,8 etc

    Integer repetitionNum = 0; //0 - learn, >0 - repeat

    Long lastRepetitionDate = 0 //MS

    Long nextRepetitionDate = 0 //MS (accurate to days!)

    Long repetitionDayId

    Boolean suspended = false;

    Boolean marked = false;

    Integer season

    Integer series
}
