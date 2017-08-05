package org.icememo

import org.icememo.api.MainController
import org.icememo.entity.Card
import org.icememo.entity.FlashCard
import org.icememo.entity.Video

class FixCollection {
    MainController main

    public void fixVideoNames() {
        List<Video> videos = main.videoDao.all

        for (Video video : videos) {
            if (video.name != null) {
                video.name = video.name.replaceAll("^[0-9]{1,2}\\s-\\s", "")
                main.videoDao.update(video)
            }
        }
    }

    public void fixVideoDone() {
        List<Video> videos = main.videoDao.all

        for (Video video : videos) {
            if (main.cardDao.getCardCount(video.id) == 0) {
                video.done = false;
            } else {
                video.done = true;
            }
            main.videoDao.update(video)
        }
    }

    private void updateCard() {
        for (Card card : main.cardDao.all) {
            Video video = main.videoDao.get(card.video)
            card.season = video.season
            card.series = main.seasonDao.get(video.season).series
        }
    }

    private void fixFlashcardDate() {
        for (FlashCard flashCard : main.flashCardDao.all) {
            if (flashCard.cardId != null) {
                Card card = main.cardDao.get(flashCard.cardId)
                if(card != null){
                    flashCard.date = card.date
                    main.flashCardDao.update(flashCard)
                }else{
                    println("FAIL!!!")
                }
            }else{
                println("NULL " + flashCard.front)
            }
        }
    }

    public void doFix() {
        fixFlashcardDate()
    }
}
