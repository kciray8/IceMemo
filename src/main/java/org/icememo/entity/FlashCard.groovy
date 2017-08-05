package org.icememo.entity

import javax.persistence.*

@Entity
class FlashCard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Integer id

    Integer user

    @Lob
    String front = "";

    @Lob
    String back = "";

    Integer cardId;

    Integer videoId;

    Long date
}
