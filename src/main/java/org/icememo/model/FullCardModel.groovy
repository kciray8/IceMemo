package org.icememo.model

class FullCardModel{
    Integer id

    Integer user

    Double timeStart

    Double timeEnd

    Double duration

    String text

    Long date

    Integer video

    String videoShortNameCache

    Integer num

    Integer lastRepetition = 0

    Integer repetitionNum = 0;

    Long lastRepetitionDate = 0

    Long nextRepetitionDate = 0

    Long repetitionDayId

    Boolean suspended = false;

    Boolean marked = false;

    List<FlashCardModel> flashcards = new ArrayList<>();
}