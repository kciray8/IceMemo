package org.icememo.model.post

import org.icememo.model.FlashCardModel

class PostCard {
    Double timeStart

    Double timeEnd

    String text

    Integer video;

    Double subtitlesOffset;

    List<FlashCardModel> flashcards = new ArrayList<>();
}
