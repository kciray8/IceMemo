package org.icememo.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class RepeatHistory {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id

    Integer cardId

    Integer user

    public static int ANSWER_HARD = 0
    public static int ANSWER_GOOD = 1
    public static int ANSWER_EASY = 2
    public static int ANSWER_NOW = 3
    public static int ANSWER_LATER = 4
    public static int ANSWER_RESET = 5
    public static int ANSWER_SUSPEND = 6

    Integer answer

    Integer repetitionNum
    Integer lastRepetitionNum

    public static int TYPE_LEARN = 0
    public static int TYPE_REPEAT = 1
    Integer type

    Long dayId

    Long ms

    Boolean todayFirst = false
}
